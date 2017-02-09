package org.catrobat.jira.adminhelper;

import com.atlassian.activeobjects.external.ActiveObjects;

import com.atlassian.jira.security.groups.GroupManager;

import com.atlassian.jira.user.util.UserManager;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import org.catrobat.jira.adminhelper.activeobject.*;
import org.catrobat.jira.adminhelper.helper.HelperUtil;
import org.catrobat.jira.adminhelper.helper.JSONExporter;
import org.catrobat.jira.adminhelper.helper.JSONImporter;
import org.catrobat.jira.adminhelper.rest.json.JsonConfig;
import org.catrobat.jira.adminhelper.rest.json.JsonDevice;
import org.catrobat.jira.adminhelper.rest.json.JsonDeviceList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


/**
 * Created by dominik on 18.01.17.
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class UploadBackupServlet extends HelperServlet  {

    private final TemplateRenderer renderer;
    private final PageBuilderService pageBuilderService;
    private final JSONImporter jsonImporter;
    private final UserManager userManager;
    private final DeviceService deviceService;
    private final ActiveObjects ao;
    private final AdminHelperConfigService configService;
    private final HardwareModelService hardwareModelService;

    private final String DEVICE_FILE = "device.JSON";
    private final String CONFIG_FILE = "config.JSON";

    private JsonDeviceList deviceList = null;
    private JsonConfig config = null;

    public UploadBackupServlet(UserManager userManager, LoginUriProvider loginUriProvider, WebSudoManager webSudoManager,
                               GroupManager groupManager, AdminHelperConfigService configurationService,
                               PageBuilderService pageBuilderService, TemplateRenderer renderer, DeviceService deviceService,
                               HardwareModelService hardwareModelService, LendingService lendingService,
                               DeviceCommentService deviceCommentService, ActiveObjects ao, AdminHelperConfigService configService) {
        super(userManager, loginUriProvider, webSudoManager, groupManager, configurationService);
        this.jsonImporter = new JSONImporter(deviceService,userManager,hardwareModelService, lendingService,
                deviceCommentService, configService);
        this.pageBuilderService = pageBuilderService;
        this.renderer = renderer;
        this.userManager = userManager;
        this.deviceService = deviceService;
        this.ao = ao;
        this.configService = configService;
        this.hardwareModelService = hardwareModelService;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        super.doGet(request, response);
        pageBuilderService.assembler().resources().requireWebResource("org.catrobat.jira.adminhelper:upload-hardware-resources");
        renderer.render("upload.vm", response.getWriter());
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        super.doPost(request, response);

        this.config = null;
        this.deviceList = null;

        System.out.println("--------NEW IMPORT-----------");
        response.setContentType("text/html");

        File failure_file;
        Map<String, File> files;

        boolean isMultipartContent = ServletFileUpload.isMultipartContent(request);
        if (!isMultipartContent) {
            response.sendError(500, "An error occurred: no files wer given!");
            return;
        }

        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);

        File temp = File.createTempFile("backup", ".zip");

        try {
            List<FileItem> fields = upload.parseRequest(request);
            Iterator<FileItem> it = fields.iterator();
            if (!it.hasNext()) {
                return;
            }
            if (fields.size() != 1) {
                response.sendError(500, "An error occurred: Only one File is allowed");
                return;
            }
            FileItem fileItem = it.next();
            if(!(fileItem.getContentType().equals("application/zip") ||
                    fileItem.getContentType().equals("application/octet-stream"))) {
                response.sendError(500, "An error occurred: you may only upload Zip files");
                return;
            }
            fileItem.write(temp);
            try {
                files = extractZip(temp);
                validateBackup(files);
            }
            catch (IOException e) {
                e.printStackTrace();
                response.sendError(500, "There was an error extracting the backup Zip\n" +
                    e.getMessage());
                temp.delete();
                return;
            }
            failure_file = generateFailureBackup();
        }
        catch (Exception e ) {
            e.printStackTrace();
            response.sendError(500, e.getMessage());

            temp.delete();
            return;
        }
        try {
            importDataFromZip(files);
        }
        catch (Exception e){
            System.out.println("[INFO] An error uncured while importing reinstancing old data");
            if(e.getClass() == JsonIOException.class || e.getClass() == JsonSyntaxException.class) {

                files = extractZip(failure_file);

                resetHardwareData();
                try {
                    importDataFromZip(files);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    response.sendError(500, "something went horribly wrong");
                }

                response.setStatus(200);

                Map<String, Object> params = new HashMap<>();
                params.put("success", false);
                params.put("message", e.getMessage());
                params.put("error_type", "json");

                for(File file : files.values())
                    file.delete();

                renderer.render("upload_result.vm", params, response.getWriter());
                return;
            }
            else {
                try {
                    files = extractZip(failure_file);

                    resetHardwareData();

                    importDataFromZip(files);
                    response.setStatus(200);

                    Map<String, Object> params = prepareParams();
                    params.put("success", false);
                    params.put("message", e.getMessage());
                    params.put("error_type", "import");

                    temp.delete();
                    failure_file.delete();

                    for(File file : files.values())
                        file.delete();

                    renderer.render("upload_result.vm", params, response.getWriter());
                    return;
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    response.sendError(500, "something went horribly wrong");
                }
            }
        }
        //everything went fine without any errors
        response.setStatus(200);
        Map<String, Object> params = prepareParams();
        System.out.println("params are: ");
        params.put("success", true);
        params.put("error_type", "none");

        System.out.println(params);
        temp.delete();
        failure_file.delete();

        for(File file : files.values())
            file.delete();

        renderer.render("upload_result.vm", params,response.getWriter());
    }

    private Map<String, File> extractZip(File zip_file) throws IOException
    {
        System.out.println("Extracting zip file");

        ZipInputStream zip = new ZipInputStream(new FileInputStream(zip_file));
        ZipEntry ze = zip.getNextEntry();
        Map<String, File> unziped_files = new HashMap<>();
        byte[] buffer = new byte[1024];

        while(ze != null) {
            System.out.println(ze.getName());
            File temp_unzip = File.createTempFile(ze.getName(),".JSON");
            FileOutputStream fos = new FileOutputStream(temp_unzip);
            int len;

            while((len = zip.read(buffer)) >0) {
                fos.write(buffer, 0, len);
            }
            unziped_files.put(ze.getName(),temp_unzip);
            fos.close();
            zip.closeEntry();
            ze = zip.getNextEntry();
        }
        zip.closeEntry();
        zip.close();

        return unziped_files;
    }

    private File generateFailureBackup() throws IOException
    {
        System.out.println("Generating failure Backup");

        JSONExporter exporter = new JSONExporter(deviceService, userManager, configService, hardwareModelService);
        List<JsonDevice> devices = exporter.getDevicesAsJSON();

        JsonDeviceList device_list = new JsonDeviceList(devices);
        JsonConfig config = exporter.getConfig();

        File save = File.createTempFile("upload_failure", ".zip");

        Gson gson = new Gson();
        String dev_json = gson.toJson(device_list);
        String conf_json = gson.toJson(config);

        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(save));
        ZipEntry dev_entry = new ZipEntry(DEVICE_FILE);
        ZipEntry conf_entry = new ZipEntry(CONFIG_FILE);

        out.putNextEntry(dev_entry);
        out.write(dev_json.getBytes());
        out.closeEntry();

        out.putNextEntry(conf_entry);
        out.write(conf_json.getBytes());
        out.closeEntry();

        out.close();

        return save;
    }

    private boolean importDataFromZip(Map<String, File> files) throws Exception {
        Set<String> keys = files.keySet();
        if(keys.size() > 2)
            throw(new Exception("something went terrible wrong"));

        System.out.println("Importing Zip");
        Gson gson = new Gson();

        if(files.containsKey(DEVICE_FILE)){
            JsonReader jsonReader = new JsonReader(new FileReader(new File(files.get(DEVICE_FILE).getAbsolutePath())));
            JsonDeviceList deviceList = gson.fromJson(jsonReader, JsonDeviceList.class);
            this.deviceList = deviceList;

            resetHardwareData();
            jsonImporter.ImportDevices(deviceList);
        }
        if(files.containsKey(CONFIG_FILE)){
            JsonReader jsonReader = new JsonReader(new FileReader(new File(files.get(CONFIG_FILE).getAbsolutePath())));
            JsonConfig config = gson.fromJson(jsonReader, JsonConfig.class);
            this.config = config;

            HelperUtil.resetConfig(configService,ao,hardwareModelService);
            jsonImporter.importConfig(config);
        }
        return true;
    }

    private void resetHardwareData()
    {
        HelperUtil.resetHardware(ao);
    }

    private boolean validateBackup(Map<String, File> to_check) throws Exception
    {
        if(to_check.size() > 2) {
            throw(new Exception("The Backup may only consist of either one or two files. \n" +
                    "allowed files are: device.JSON and config.JSON"));
        }
        if(to_check.size() == 2) {
            if(!(to_check.containsKey(DEVICE_FILE) && to_check.containsKey(CONFIG_FILE))){
                throw(new Exception("The structure of the Backup has been violated! \n" +
                        "allowed files are: " + DEVICE_FILE +" or " + CONFIG_FILE + "or both \n" +
                        "Structure was: " + to_check.keySet()));
            }
        }
        if(to_check.size() == 1){
            if(!(to_check.containsKey(DEVICE_FILE) || to_check.containsKey(CONFIG_FILE))){
                throw(new Exception("The structure of the Backup has been violated! \n" +
                        "allowed files are: device.JSON or config.JSON or both \n" +
                        "given: "+ to_check.keySet()));
            }
        }
        return true;
    }

    private Map<String, Object> prepareParams()
    {
        Gson gson = new Gson();
        Map<String, Object> params = new HashMap<>();
        if(deviceList != null)
            params.put("device", gson.toJson(deviceList));
        else
            params.put("device", "none");
        if(config != null)
            params.put("config", gson.toJson(config));
        else
            params.put("config", "none");

        return params;
    }

}

