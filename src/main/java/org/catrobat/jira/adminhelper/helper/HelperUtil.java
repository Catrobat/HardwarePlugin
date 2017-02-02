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

package org.catrobat.jira.adminhelper.helper;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;
import org.catrobat.jira.adminhelper.activeobject.*;
import org.catrobat.jira.adminhelper.rest.json.JsonConfig;
import org.catrobat.jira.adminhelper.rest.json.JsonResource;
import org.catrobat.jira.adminhelper.rest.json.JsonTeam;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHTeam;
import org.kohsuke.github.GitHub;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.*;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

public class HelperUtil {

    public static String formatString(final String toFormat) {
        String formatted;
        if (toFormat != null && toFormat.trim().length() != 0) {
            formatted = escapeHtml4(toFormat.trim());
        } else {
            formatted = null;
        }

        return formatted;
    }

    public static Date clearTime(final Date date) {
        Calendar beginCalendar = new GregorianCalendar();
        beginCalendar.setTime(date);
        beginCalendar.set(Calendar.HOUR_OF_DAY, 0);
        beginCalendar.clear(Calendar.MINUTE);
        beginCalendar.clear(Calendar.SECOND);
        beginCalendar.clear(Calendar.MILLISECOND);

        return beginCalendar.getTime();
    }

    public static void saveConfig(JsonConfig jsonConfig, AdminHelperConfigService configService) throws IOException {
        configService.setUserDirectoryId(jsonConfig.getUserDirectoryId());
        configService.editMail(jsonConfig.getMailFromName(), jsonConfig.getMailFrom(),
                jsonConfig.getMailSubject(), jsonConfig.getMailBody());

        if (jsonConfig.getResources() != null) {
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

        if (jsonConfig.getTeams() != null) {
            String token = configService.getConfiguration().getGithubApiToken();
            String organizationName = configService.getConfiguration().getGithubOrganisation();


            GitHub gitHub = GitHub.connectUsingOAuth(token);
            GHOrganization organization = gitHub.getOrganization(organizationName);
            Collection<GHTeam> teamList = organization.getTeams().values();


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
        }
    }

    public static void saveGithubConfig(JsonConfig jsonConfig, AdminHelperConfigService configService) throws Exception
    {
        if (jsonConfig.getGithubToken() != null && jsonConfig.getGithubToken().length() != 0) {
            configService.setApiToken(jsonConfig.getGithubToken());
        }
        if (jsonConfig.getGithubTokenPublic() != null)
            configService.setPublicApiToken(jsonConfig.getGithubTokenPublic());
        if(jsonConfig.getGithubOrganization() != null)
            configService.setOrganisation(jsonConfig.getGithubOrganization());
        else
            throw(new Exception("Github Configuration Settings are not valid"));

        if(jsonConfig.getDefaultGithubTeam() != null)
        {
            System.out.println("about to set default githug team " + jsonConfig.getDefaultGithubTeam());
            configService.setDefaultGithubTeam(jsonConfig.getDefaultGithubTeam());
        }

        String token = configService.getConfiguration().getGithubApiToken();
        String organizationName = configService.getConfiguration().getGithubOrganisation();

        GitHub gitHub = GitHub.connectUsingOAuth(token);
        GHOrganization organization = gitHub.getOrganization(organizationName);
        Collection<GHTeam> teamList = organization.getTeams().values();

        if (jsonConfig.getDefaultGithubTeam() != null) {
            for (GHTeam team : teamList) {
                if (jsonConfig.getDefaultGithubTeam().toLowerCase().equals(team.getName().toLowerCase())) {
                    configService.setDefaultGithubTeamId(team.getId());
                    break;
                }
            }
        }
    }

    public static void resetHardware(ActiveObjects ao)
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
}
