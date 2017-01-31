package org.catrobat.jira.adminhelper;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.config.util.JiraHome;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.template.TemplateSource;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.util.IOUtil;
import com.atlassian.jira.util.json.JSONObject;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.google.gson.Gson;
import org.catrobat.jira.adminhelper.activeobject.AdminHelperConfigService;
import org.catrobat.jira.adminhelper.activeobject.DeviceService;
import org.catrobat.jira.adminhelper.helper.JSONExporter;
import org.catrobat.jira.adminhelper.rest.json.JsonConfig;
import org.catrobat.jira.adminhelper.rest.json.JsonDevice;
import org.catrobat.jira.adminhelper.rest.json.JsonDeviceList;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by dominik on 18.01.17.
 */
public class DownloadBackupServlet extends HelperServlet {
    private final UserManager userManager;
    private final DeviceService deviceService;
    private final JiraHome jiraHome;
    private final AdminHelperConfigService configService;

    public DownloadBackupServlet(UserManager userManager, LoginUriProvider loginUriProvider, WebSudoManager webSudoManager,
                                 GroupManager groupManager, AdminHelperConfigService configurationService, DeviceService deviceService,
                                 JiraHome jiraHome, AdminHelperConfigService configService) {
        super(userManager, loginUriProvider, webSudoManager, groupManager, configurationService);
        this.userManager = userManager;
        this.deviceService = deviceService;
        this.jiraHome = jiraHome;
        this.configService = configService;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);

        System.out.println("parameters are");
        System.out.println(req.getParameterMap());
        if(req.getParameterMap().size() == 0) {
           packHardware(resp);
        }
        if(req.getParameterMap().size() == 2) {
            String download_hardware = req.getParameter("hardware");
            if(download_hardware.equals("false")){
                packConfig(resp);
            }
            else{
                packConfigAndHardware(resp);
            }
        }
        else {
            resp.sendError(500);
        }
    }

    private void packHardware(HttpServletResponse resp) throws IOException
    {
        resp.setContentType("application/zip; charset=utf-8");
        String file_name = "hardwareBackup_" + new Date() + ".zip";
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + file_name + "\"");

        JSONExporter exporter = new JSONExporter(deviceService, userManager, configService);

        List<JsonDevice> devices = exporter.getDevicesAsJSON();

        JsonDeviceList device_list = new JsonDeviceList(devices);

        Gson gson = new Gson();
        String json_string = gson.toJson(device_list);

        ZipOutputStream out = new ZipOutputStream(resp.getOutputStream());
        ZipEntry e = new ZipEntry("device.JSON");

        out.putNextEntry(e);
        out.write(json_string.getBytes());
        out.closeEntry();
        out.close();
    }

    private void packConfig(HttpServletResponse resp) throws IOException
    {
        resp.setContentType("application/zip; charset=utf-8");
        String file_name = "configBackup_" + new Date() + ".zip";
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + file_name + "\"");

        JSONExporter exporter = new JSONExporter(deviceService, userManager, configService);

        JsonConfig config = exporter.getConfig();
        config.setGithubToken(configService.getConfiguration().getGithubApiToken());

        Gson gson = new Gson();
        String json_string = gson.toJson(config);

        ZipOutputStream out = new ZipOutputStream(resp.getOutputStream());
        ZipEntry e = new ZipEntry("config.JSON");

        out.putNextEntry(e);
        out.write(json_string.getBytes());
        out.closeEntry();
        out.close();
    }

    private void packConfigAndHardware(HttpServletResponse resp) throws IOException
    {
        resp.setContentType("application/zip; charset=utf-8");
        String file_name = "hdw_config_Backup_" + new Date() + ".zip";
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + file_name + "\"");

        JSONExporter exporter = new JSONExporter(deviceService, userManager, configService);

        JsonConfig config = exporter.getConfig();
        List<JsonDevice> devices = exporter.getDevicesAsJSON();

        config.setGithubToken(configService.getConfiguration().getGithubApiToken());

        Gson gson = new Gson();
        String json_config = gson.toJson(config);

        JsonDeviceList device_list = new JsonDeviceList(devices);

        String json_device = gson.toJson(device_list);

        ZipOutputStream out = new ZipOutputStream(resp.getOutputStream());
        ZipEntry config_entry = new ZipEntry("config.JSON");
        ZipEntry device_entry = new ZipEntry("device.JSON");

        out.putNextEntry(config_entry);
        out.write(json_config.getBytes());
        out.closeEntry();

        out.putNextEntry(device_entry);
        out.write(json_device.getBytes());
        out.closeEntry();

        out.close();
    }
}
