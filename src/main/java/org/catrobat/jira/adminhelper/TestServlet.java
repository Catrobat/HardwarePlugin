package org.catrobat.jira.adminhelper;

import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.webresource.api.assembler.PageBuilderService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by dominik on 10.11.16.
 */
public class TestServlet extends HttpServlet {

    private final TemplateRenderer renderer;
    private final PageBuilderService pageBuilderService;

    public TestServlet(TemplateRenderer renderer, PageBuilderService page_service)
    {
        this.renderer = renderer;
        this.pageBuilderService = page_service;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        pageBuilderService.assembler().resources().requireWebResource("org.catrobat.jira.adminhelper:test-resources");
        renderer.render("test.vm", resp.getWriter());
    }
}
