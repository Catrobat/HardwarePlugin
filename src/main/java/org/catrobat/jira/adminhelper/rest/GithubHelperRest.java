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

package org.catrobat.jira.adminhelper.rest;

import com.atlassian.core.AtlassianCoreException;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.preferences.ExtendedPreferences;
import com.atlassian.jira.user.preferences.UserPreferencesManager;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.sal.api.user.UserManager;
import org.catrobat.jira.adminhelper.activeobject.AdminHelperConfigService;
import org.catrobat.jira.adminhelper.helper.GithubHelper;
import org.catrobat.jira.adminhelper.rest.json.JsonUser;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/github")
public class GithubHelperRest extends RestHelper {
    private final AdminHelperConfigService configService;
    private final UserManager userManager;
    private final PermissionManager permissionManager;
    private final GroupManager groupManager;
    private final GithubHelper githubHelper;
    private final DirectoryManager directoryManager;

    public GithubHelperRest(final UserManager userManager, final AdminHelperConfigService configService,
                            final PermissionManager permissionManager, final GroupManager groupManager,
                            final DirectoryManager directoryManager) {
        super(permissionManager, configService, userManager, groupManager);
        this.configService = configService;
        this.userManager = userManager;
        this.permissionManager = permissionManager;
        this.groupManager = groupManager;
        this.githubHelper = new GithubHelper(configService);
        this.directoryManager = directoryManager;
    }

    @PUT
    @Path("/changeGithubname")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changeGithubnameRest(final JsonUser jsonUser, @Context HttpServletRequest request) {
        Response unauthorized = checkPermission(request);
        if (unauthorized != null) {
            return unauthorized;
        }

        if (jsonUser.getUserName() == null || jsonUser.getDeveloperList() == null) {
            return Response.serverError().entity("JSON User Object not complete").build();
        }

        boolean deleteGithubName = false;
        if(jsonUser.getGithubName() == null || jsonUser.getGithubName().trim().equals("")) {
            deleteGithubName = true;
        } else if (!githubHelper.doesUserExist(jsonUser.getGithubName())) {
            return Response.serverError().entity("Github User does not exist").build();
        }

        UserUtil userUtil = ComponentAccessor.getUserUtil();
        ApplicationUser applicationUser = userUtil.getUserByName(jsonUser.getUserName());

        if (applicationUser == null) {
            return Response.serverError().entity("Jira User does not exist").build();
        }

        UserPreferencesManager userPreferencesManager = ComponentAccessor.getUserPreferencesManager();
        ExtendedPreferences extendedPreferences = userPreferencesManager.getExtendedPreferences(applicationUser);
        String oldGithubName = extendedPreferences.getText(UserRest.GITHUB_PROPERTY);

        // remove user from older github teams if github user is existing only once
        boolean stillExists = false;
        if (oldGithubName != null) {
            for (ApplicationUser tempApplicationUser : userUtil.getAllApplicationUsers()) {
                if (tempApplicationUser.getUsername().toLowerCase().equals(applicationUser.getUsername().toLowerCase())) {
                    continue;
                }

                extendedPreferences = userPreferencesManager.getExtendedPreferences(tempApplicationUser);
                String tempUserGithubName = extendedPreferences.getText(UserRest.GITHUB_PROPERTY);
                if (tempUserGithubName != null && oldGithubName.toLowerCase().equals(tempUserGithubName.toLowerCase())) {
                    stillExists = true;
                    break;
                }
            }
        }

        // set the new github user name in the user preferences
        extendedPreferences = userPreferencesManager.getExtendedPreferences(applicationUser);
        try {
            if(deleteGithubName) {
                extendedPreferences.remove(UserRest.GITHUB_PROPERTY);
            } else {
                extendedPreferences.setText(UserRest.GITHUB_PROPERTY, jsonUser.getGithubName());
            }
        } catch (AtlassianCoreException e) {
            e.printStackTrace();
            return Response.serverError().entity(e.getMessage()).build();
        }

        if(!deleteGithubName) {
            UserRest userRest = new UserRest(userManager, userPreferencesManager, configService, permissionManager, groupManager, directoryManager, null);
            Response response = userRest.addUserToGithubTeams(jsonUser);
            if (response.getStatus() != 200) {
                return response;
            }
        }

        if (!stillExists && oldGithubName != null) {
            if(deleteGithubName) {
                githubHelper.removeUserFromOrganization(oldGithubName);
            } else if (oldGithubName.toLowerCase().equals(jsonUser.getGithubName().toLowerCase())) {
                githubHelper.removeUserFromAllOldGroups(jsonUser);
            } else {
                githubHelper.removeUserFromOrganization(oldGithubName);
            }
        }

        return Response.ok().build();
    }
}
