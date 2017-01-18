package org.catrobat.jira.adminhelper;

import com.atlassian.jira.config.util.JiraHome;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import org.catrobat.jira.adminhelper.activeobject.AdminHelperConfigService;
import org.catrobat.jira.adminhelper.activeobject.DeviceService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by dominik on 18.01.17.
 */
public class UploadHardwareBackupServlet extends HelperServlet  {
    private final TemplateRenderer renderer;
    private final PageBuilderService pageBuilderService;

    public UploadHardwareBackupServlet(UserManager userManager, LoginUriProvider loginUriProvider, WebSudoManager webSudoManager,
                                         GroupManager groupManager, AdminHelperConfigService configurationService, PageBuilderService pageBuilderService,
                                       TemplateRenderer renderer) {
        super(userManager, loginUriProvider, webSudoManager, groupManager, configurationService);
        this.pageBuilderService = pageBuilderService;
        this.renderer = renderer;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        super.doGet(request, response);
        renderer.render("upload.vm", response.getWriter());
    }
}
