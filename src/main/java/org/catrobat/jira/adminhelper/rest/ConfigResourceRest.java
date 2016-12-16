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


import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import org.catrobat.jira.adminhelper.activeobject.AdminHelperConfigService;
import org.catrobat.jira.adminhelper.activeobject.Team;
import org.catrobat.jira.adminhelper.rest.json.JsonConfig;
import org.catrobat.jira.adminhelper.rest.json.JsonResource;
import org.catrobat.jira.adminhelper.rest.json.JsonTeam;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHTeam;
import org.kohsuke.github.GitHub;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Path("/config")
public class ConfigResourceRest extends RestHelper {
    private final AdminHelperConfigService configService;
    private final DirectoryManager directoryManager;

    public ConfigResourceRest(final UserManager userManager, final AdminHelperConfigService configService,
                              final PermissionManager permissionManager, final GroupManager groupManager, final DirectoryManager directoryManager) {
        super(permissionManager, configService, userManager, groupManager);
        this.configService = configService;
        this.directoryManager = directoryManager;
    }

    @GET
    @Path("/getConfig")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getConfig(@Context HttpServletRequest request) {
        Response unauthorized = checkPermission(request);
        if (unauthorized != null) {
            return unauthorized;
        }

        return Response.ok(new JsonConfig(configService)).build();
    }

    @GET
    @Path("/getDirectories")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDirectories(@Context HttpServletRequest request) {
        Response unauthorized = checkPermission(request);
        if (unauthorized != null) {
            return unauthorized;
        }

        List<Directory> directoryList = directoryManager.findAllDirectories();
        System.out.println("printing directory list");
        System.out.println(directoryList);
        List<JsonConfig> jsonDirectoryList = new ArrayList<JsonConfig>();
        for (Directory directory : directoryList) {
            JsonConfig config = new JsonConfig();
            config.setUserDirectoryId(directory.getId());
            config.setUserDirectoryName(directory.getName());
            jsonDirectoryList.add(config);
        }

        return Response.ok(jsonDirectoryList).build();
    }

    @GET
    @Path("/getTeamList")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTeamList(@Context HttpServletRequest request) {
        Response unauthorized = checkPermission(request);
        if (unauthorized != null) {
            return unauthorized;
        }

        List<String> teamList = new ArrayList<String>();
        for (Team team : configService.getConfiguration().getTeams()) {
            teamList.add(team.getTeamName());
        }

        return Response.ok(teamList).build();
    }

    @PUT
    @Path("/saveGithubConfig")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setGithubConfig(final JsonConfig jsonConfig, @Context HttpServletRequest request)
    {
        System.out.println("saving Github Settings");

        Response unauthorized = checkPermission(request);
        if (unauthorized != null) {
            return unauthorized;
        }

        if (jsonConfig.getGithubToken() != null && jsonConfig.getGithubToken().length() != 0) {
            configService.setApiToken(jsonConfig.getGithubToken());
        }
        if (jsonConfig.getGithubTokenPublic() != null)
            configService.setPublicApiToken(jsonConfig.getGithubTokenPublic());
        if(jsonConfig.getGithubOrganization() != null)
            configService.setOrganisation(jsonConfig.getGithubOrganization());
        else
            return Response.serverError().entity("Github Configuration Settings are not valid").build();

        if (jsonConfig.getTeams() != null) {
            String token = configService.getConfiguration().getGithubApiToken();
            String organizationName = configService.getConfiguration().getGithubOrganisation();

            try {
                GitHub gitHub = GitHub.connectUsingOAuth(token);
                GHOrganization organization = gitHub.getOrganization(organizationName);
                System.out.println("token:" + token);
                System.out.println("org: " + organizationName);
               /* GitHub gitHub = GitHub.connectUsingOAuth("13c778d7f93e166c4ae692ab0aa541ee10d1e9d8");
                GHOrganization organization = gitHub.getOrganization("Catrobat");*/
                Collection<GHTeam> teamList = organization.getTeams().values();

                if (jsonConfig.getDefaultGithubTeam() != null) {
                    for (GHTeam team : teamList) {
                        if (jsonConfig.getDefaultGithubTeam().toLowerCase().equals(team.getName().toLowerCase())) {
                            configService.setDefaultGithubTeamId(team.getId());
                            break;
                        }
                    }
                }

                for (JsonTeam jsonTeam : jsonConfig.getTeams()) {
                    configService.removeTeam(jsonTeam.getName());

                    List<Integer> githubIdList = new ArrayList<Integer>();
                    for (String teamName : jsonTeam.getGithubTeams()) {
                        for (GHTeam team : teamList) {
                            if (teamName.toLowerCase().equals(team.getName().toLowerCase())) {
                                githubIdList.add(team.getId());
                                break;
                            }
                        }
                    }

                    configService.addTeam(jsonTeam.getName(), githubIdList, jsonTeam.getCoordinatorGroups(),
                            jsonTeam.getSeniorGroups(), jsonTeam.getDeveloperGroups());
                }
            } catch (IOException e) {
                e.printStackTrace();
                return Response.serverError().entity("Some error with GitHub API (e.g. maybe wrong tokens, organisation, teams) occured").build();
            }
        }

        return Response.ok().build();
    }

    @PUT
    @Path("/checkSettings")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response checkGithubSettings(final JsonConfig jsonConfig, @Context HttpServletRequest request)
    {
        String token = configService.getConfiguration().getGithubApiToken();
        String org = jsonConfig.getGithubOrganization();

        System.out.println("checking settings for:" );
        System.out.println("token:" + token);
        System.out.println("org:" + org);
        try
        {
            GitHub gitHub = GitHub.connectUsingOAuth(token);
            GHOrganization organization = gitHub.getOrganization(org);
        }
        catch(Exception e)
        {
           return Response.serverError().entity("There was an error! \n The given Organization cant be accessed with " +
                   "the current token!").build();
        }
        return Response.ok().build();
    }

    @PUT
    @Path("/saveConfig")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setConfig(final JsonConfig jsonConfig, @Context HttpServletRequest request) {
        System.out.println("--------------------------------------");
        System.out.println("config to save is");
        System.out.println(jsonConfig);

        Response unauthorized = checkPermission(request);
        if (unauthorized != null) {
            return unauthorized;
        }

        System.out.println("user directory id is: " + jsonConfig.getUserDirectoryId());

        configService.setUserDirectoryId(jsonConfig.getUserDirectoryId());
        configService.editMail(jsonConfig.getMailFromName(), jsonConfig.getMailFrom(),
                jsonConfig.getMailSubject(), jsonConfig.getMailBody());

        if(jsonConfig.getResources() != null)
        {
            for (JsonResource jsonResource : jsonConfig.getResources()) {
                configService.editResource(jsonResource.getResourceName(), jsonResource.getGroupName());
            }
        }

        if (jsonConfig.getApprovedGroups() != null) {
            configService.clearApprovedGroups();
            for (String approvedGroupName : jsonConfig.getApprovedGroups()) {
                configService.addApprovedGroup(approvedGroupName);
            }
        }

        com.atlassian.jira.user.util.UserManager jiraUserManager = ComponentAccessor.getUserManager();
        if (jsonConfig.getApprovedUsers() != null) {
            configService.clearApprovedUsers();
            for (String approvedUserName : jsonConfig.getApprovedUsers()) {
                ApplicationUser user = jiraUserManager.getUserByName(approvedUserName);
                if (user != null) {
                    configService.addApprovedUser(user.getKey());
                }
            }
        }

        return Response.noContent().build();
    }

    @PUT
    @Path("/addTeam")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addTeam(final String modifyTeam, @Context HttpServletRequest request) {
        Response unauthorized = checkPermission(request);
        if (unauthorized != null) {
            return unauthorized;
        }

        boolean successful = configService.addTeam(modifyTeam, null, null, null, null) != null;

        if (successful)
            return Response.noContent().build();

        return Response.serverError().build();
    }

    @PUT
    @Path("/editTeam")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response editTeam(final String[] teams, @Context HttpServletRequest request) {
        Response unauthorized = checkPermission(request);
        if (unauthorized != null) {
            return unauthorized;
        }

        if (teams == null || teams.length != 2) {
            return Response.serverError().build();
        } else if (teams[1].trim().length() == 0) {
            return Response.serverError().entity("Team name must not be empty").build();
        }

        boolean successful = configService.editTeam(teams[0], teams[1]) != null;

        if (successful)
            return Response.noContent().build();

        return Response.serverError().build();
    }

    @PUT
    @Path("/removeTeam")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeTeam(final String modifyTeam, @Context HttpServletRequest request) {
        Response unauthorized = checkPermission(request);
        if (unauthorized != null) {
            return unauthorized;
        }

        boolean successful = configService.removeTeam(modifyTeam) != null;

        if (successful)
            return Response.noContent().build();

        return Response.serverError().build();
    }

    @PUT
    @Path("/addResource")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addResource(final String resourceName, @Context HttpServletRequest request) {
        Response unauthorized = checkPermission(request);
        if (unauthorized != null) {
            return unauthorized;
        }

        if (resourceName == null || resourceName.trim().length() == 0) {
            return Response.serverError().entity("Resource-Name must not be empty").build();
        }

        boolean successful = configService.addResource(resourceName, null) != null;

        if (successful)
            return Response.noContent().build();

        return Response.serverError().entity("Maybe name already taken?").build();
    }

    @PUT
    @Path("/removeResource")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeResource(final String resourceName, @Context HttpServletRequest request) {
        Response unauthorized = checkPermission(request);
        if (unauthorized != null) {
            return unauthorized;
        }

        if (resourceName == null || resourceName.trim().length() == 0) {
            return Response.serverError().entity("Resource-Name must not be empty").build();
        }

        boolean successful = configService.removeResource(resourceName) != null;

        if (successful)
            return Response.noContent().build();

        return Response.serverError().entity("Maybe no resource with given name?").build();
    }


}