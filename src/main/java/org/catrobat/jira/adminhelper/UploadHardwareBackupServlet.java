package org.catrobat.jira.adminhelper;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.config.util.JiraHome;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.template.TemplateSource;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.protobuf.ByteString;
import org.apache.commons.collections.iterators.ObjectArrayIterator;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOExceptionWithCause;
import org.catrobat.jira.adminhelper.activeobject.*;
import org.catrobat.jira.adminhelper.helper.JSONExporter;
import org.catrobat.jira.adminhelper.helper.JSONImporter;
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
public class UploadHardwareBackupServlet extends HelperServlet  {
    private final TemplateRenderer renderer;
    private final PageBuilderService pageBuilderService;
    private final JSONImporter jsonImporter;
    private final UserManager userManager;
    private final DeviceService deviceService;
    private final ActiveObjects ao;

    private String DEVICE_FILE = "device.JSON";

    public UploadHardwareBackupServlet(UserManager userManager, LoginUriProvider loginUriProvider, WebSudoManager webSudoManager,
                                       GroupManager groupManager, AdminHelperConfigService configurationService,
                                       PageBuilderService pageBuilderService, TemplateRenderer renderer, DeviceService deviceService,
                                       HardwareModelService hardwareModelService, LendingService lendingService,
                                       DeviceCommentService deviceCommentService, ActiveObjects ao) {
        super(userManager, loginUriProvider, webSudoManager, groupManager, configurationService);
        this.jsonImporter = new JSONImporter(deviceService,userManager,hardwareModelService, lendingService, deviceCommentService);
        this.pageBuilderService = pageBuilderService;
        this.renderer = renderer;
        this.userManager = userManager;
        this.deviceService = deviceService;
        this.ao = ao;
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
        JsonDeviceList devices = null;
        Gson gson = new Gson();

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
            if(!fileItem.getContentType().equals("application/zip")) {
                response.sendError(500, "An error occurred: you may only upload Zip files");
                return;
            }
            System.out.println("wrote file to temp folder");
            fileItem.write(temp);
            failure_file = generateFailureBackup();
        }
        catch (Exception e ) {
            e.printStackTrace();
            return;
        }
        try {
            files = extractZip(temp);
        }
        catch (IOException e) {
            e.printStackTrace();
            response.sendError(500, "There was an error extracting the backup Zip");
            return;
        }
        try {
            devices = importDataFromZip(files);
        }
        catch (Exception e){
            System.out.println("[INFO] An error uncured while importing reinstancing old data");
            files = extractZip(failure_file);
            try {
                devices = importDataFromZip(files);

                response.setStatus(200);

                Map<String, Object> params = prepareParams(gson.toJson(devices));
                params.put("success", false);
                params.put("message", e.getMessage());

                renderer.render("upload_result.vm", params,response.getWriter());
                return;
            }
            catch (Exception ex) {
                ex.printStackTrace();
                response.sendError(500, "something went horribly wrong");
            }
        }

        response.setStatus(200);
        Map<String, Object> params = prepareParams(gson.toJson(devices));
        params.put("success", true);

        renderer.render("upload_result.vm", params,response.getWriter());
    }

    private Map<String, File> extractZip(File zip_file) throws IOException
    {
        System.out.println("Extracting zip file");

        ZipInputStream zip = new ZipInputStream(new FileInputStream(zip_file));
        ZipEntry ze = zip.getNextEntry();
        Map<String, File> uniziped_files = new HashMap<>();
        byte[] buffer = new byte[1024];

        while(ze != null) {
            System.out.println(ze.getName());
            File temp_unzip = File.createTempFile(ze.getName(),".JSON");
            FileOutputStream fos = new FileOutputStream(temp_unzip);
            int len;

            while((len = zip.read(buffer)) >0) {
                fos.write(buffer, 0, len);
            }
            uniziped_files.put(ze.getName(),temp_unzip);
            fos.close();
            zip.closeEntry();
            ze = zip.getNextEntry();
        }
        zip.closeEntry();
        zip.close();

        return uniziped_files;
    }

    private File generateFailureBackup() throws IOException
    {
        System.out.println("Generating failure Backup");

        JSONExporter exporter = new JSONExporter(deviceService, userManager);
        List<JsonDevice> devices = exporter.getDevicesAsJSON();
        JsonDeviceList device_list = new JsonDeviceList(devices);

        File save = File.createTempFile("upload_failure", ".zip");

        Gson gson = new Gson();
        String json_string = gson.toJson(device_list);

        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(save));
        ZipEntry e = new ZipEntry(DEVICE_FILE);

        out.putNextEntry(e);
        out.write(json_string.getBytes());
        out.closeEntry();
        out.close();

        return save;
    }

    private JsonDeviceList importDataFromZip(Map<String, File> files) throws Exception {
        System.out.println("Importing Zip deleting old entries");
        resetHardwareData();
        Gson gson = new Gson();
        System.out.println("printing map");
        System.out.println(files);
        if (files.get(DEVICE_FILE) != null) {
            System.out.println("Importing device.JSON");
            JsonReader jsonReader = new JsonReader(new FileReader(new File(files.get(DEVICE_FILE).getAbsolutePath())));
            JsonDeviceList deviceList = gson.fromJson(jsonReader, JsonDeviceList.class);
            jsonImporter.ImportDevices(deviceList);

            return deviceList;
        }
        else {
            throw new Exception("The Zip Archive does not contain the following file: " + DEVICE_FILE);
        }
    }

    private void resetHardwareData()
    {
        ao.executeInTransaction(() -> {
            for (DeviceComment deviceComment : ao.find(DeviceComment.class)) {
                ao.delete(deviceComment);
            }
            for (Lending lending : ao.find(Lending.class)) {
                ao.delete(lending);
            }
            for (Device device : ao.find(Device.class)) {
                ao.delete(device);
            }
            for (HardwareModel hardwareModel : ao.find(HardwareModel.class)) {
                ao.delete(hardwareModel);
            }
            for (TypeOfDevice typeOfDevice : ao.find(TypeOfDevice.class)) {
                ao.delete(typeOfDevice);
            }
            for (Producer producer : ao.find(Producer.class)) {
                ao.delete(producer);
            }
            for (OperatingSystem operatingSystem : ao.find(OperatingSystem.class)) {
                ao.delete(operatingSystem);
            }
            return null;
        });
    }

    private Map<String, Object> prepareParams(String json_string)
    {
        Map<String, Object> params = new HashMap<>();

        params.put("device", json_string);
        params.put("success", true);
        return params;
    }

}

