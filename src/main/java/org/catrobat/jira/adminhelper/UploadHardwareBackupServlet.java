package org.catrobat.jira.adminhelper;

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
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.catrobat.jira.adminhelper.activeobject.*;
import org.catrobat.jira.adminhelper.helper.JSONExporter;
import org.catrobat.jira.adminhelper.helper.JSONImporter;
import org.catrobat.jira.adminhelper.rest.json.JsonDeviceList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


/**
 * Created by dominik on 18.01.17.
 */
public class UploadHardwareBackupServlet extends HelperServlet  {
    private final TemplateRenderer renderer;
    private final PageBuilderService pageBuilderService;
    private final JSONImporter jsonImporter;

    String DEVICE_FILE = "device.JSON";

    public UploadHardwareBackupServlet(UserManager userManager, LoginUriProvider loginUriProvider, WebSudoManager webSudoManager,
                                       GroupManager groupManager, AdminHelperConfigService configurationService,
                                       PageBuilderService pageBuilderService, TemplateRenderer renderer, DeviceService deviceService,
                                       HardwareModelService hardwareModelService, LendingService lendingService,
                                       DeviceCommentService deviceCommentService) {
        super(userManager, loginUriProvider, webSudoManager, groupManager, configurationService);
        this.jsonImporter = new JSONImporter(deviceService,userManager,hardwareModelService, lendingService, deviceCommentService);
        this.pageBuilderService = pageBuilderService;
        this.renderer = renderer;
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
        PrintWriter out = response.getWriter();

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
                out.println("No fields found");
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
            System.out.print("wrote file to temp folder");
            fileItem.write(temp);

        }
        catch (Exception e ) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        try {
            System.out.println("printing map");
            Map<String, File> files = extractZip(temp);
            System.out.println(files);
            if(files.get(DEVICE_FILE) != null) {
                System.out.println("Importing device.JSON");
                JsonReader jsonReader = new JsonReader(new FileReader(new File(files.get(DEVICE_FILE).getAbsolutePath())));
                JsonDeviceList deviceList = gson.fromJson(jsonReader, JsonDeviceList.class);
                jsonImporter.ImportDevices(deviceList);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            response.sendError(500, "There was an error extracting the backup Zip");
        }

        out.print("<h1>Success</h1>");
        response.setStatus(200);
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
}

