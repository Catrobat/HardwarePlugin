/*
 * Copyright 2014 Stephan Fellhofer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.catrobat.jira.adminhelper;

import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.web.pagebuilder.PageBuilder;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import org.catrobat.jira.adminhelper.activeobject.AdminHelperConfigService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AdminServlet extends HelperServlet {
    private final TemplateRenderer renderer;
    private final PageBuilderService pageBuilderService;

    public AdminServlet(UserManager userManager, LoginUriProvider loginUriProvider, TemplateRenderer renderer,
                        WebSudoManager webSudoManager, GroupManager groupManager,
                        AdminHelperConfigService configurationService, PageBuilderService page_service) {
        super(userManager, loginUriProvider, webSudoManager, groupManager, configurationService);
        this.renderer = renderer;
        this.pageBuilderService = page_service;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        super.doGet(request, response);
        pageBuilderService.assembler().resources().requireWebResource("org.catrobat.jira.adminhelper:admin-resources");
        renderer.render("admin.vm", response.getWriter());
    }
}