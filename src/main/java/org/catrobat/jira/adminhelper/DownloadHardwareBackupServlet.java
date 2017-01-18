package org.catrobat.jira.adminhelper;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.util.json.JSONObject;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.websudo.WebSudoManager;
import org.catrobat.jira.adminhelper.activeobject.AdminHelperConfigService;
import org.catrobat.jira.adminhelper.activeobject.DeviceService;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Created by dominik on 18.01.17.
 */
public class DownloadHardwareBackupServlet extends HelperServlet {
    private final UserManager userManager;

    public DownloadHardwareBackupServlet(UserManager userManager, LoginUriProvider loginUriProvider, WebSudoManager webSudoManager,
                              GroupManager groupManager, AdminHelperConfigService configurationService) {
        super(userManager, loginUriProvider, webSudoManager, groupManager, configurationService);
        this.userManager = userManager;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);

        resp.setContentType("text/html; charset=utf-8");
        resp.setHeader("Content-Disposition", "attachment; filename=\"test.JSON\"");
        PrintWriter writer = resp.getWriter();
        writer.print("<html>" +
                "<body>" +
                "<h1>This is just a test</h1>" +
                "</body>" +
                "</html>");
        writer.flush();
        writer.close();


        JSONObject ob = new JSONObject();
        try {
            ob.append("test", "test");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        PrintStream printStream = new PrintStream(resp.getOutputStream(), false, "UTF-8");
        printStream.print(ob.toString());
        printStream.flush();
        printStream.close();
    }
}
