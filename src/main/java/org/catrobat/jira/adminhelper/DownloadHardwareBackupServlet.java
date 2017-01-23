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
public class DownloadHardwareBackupServlet extends HelperServlet {
    private final UserManager userManager;
    private final DeviceService deviceService;
    private final JiraHome jiraHome;

    public DownloadHardwareBackupServlet(UserManager userManager, LoginUriProvider loginUriProvider, WebSudoManager webSudoManager,
                                         GroupManager groupManager, AdminHelperConfigService configurationService, DeviceService deviceService,
                                         JiraHome jiraHome) {
        super(userManager, loginUriProvider, webSudoManager, groupManager, configurationService);
        this.userManager = userManager;
        this.deviceService = deviceService;
        this.jiraHome = jiraHome;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);

        resp.setContentType("application/zip; charset=utf-8");
        String file_name = "hardwareBackup_" + new Date() +".zip";
        resp.setHeader("Content-Disposition", "attachment; filename=\""+file_name+"\"");

        JSONExporter exporter = new JSONExporter(deviceService, userManager);

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
}
