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

package org.catrobat.jira.adminhelper.rest.json;

import org.catrobat.jira.adminhelper.activeobject.AdminHelperConfigService;
import org.catrobat.jira.adminhelper.activeobject.GithubTeam;
import org.catrobat.jira.adminhelper.activeobject.Team;
import org.catrobat.jira.adminhelper.activeobject.TeamToGroup;
import org.catrobat.jira.adminhelper.helper.GithubHelper;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class JsonTeam {
    @XmlElement
    private String name;
    @XmlElement
    private List<String> githubTeams;
    @XmlElement
    private List<String> coordinatorGroups;
    @XmlElement
    private List<String> seniorGroups;
    @XmlElement
    private List<String> developerGroups;

    public JsonTeam() {

    }

    public JsonTeam(String name) {
        this.name = name;
        githubTeams = new ArrayList<String>();
        coordinatorGroups = new ArrayList<String>();
        seniorGroups = new ArrayList<String>();
        developerGroups = new ArrayList<String>();
    }

    public JsonTeam(Team toCopy, AdminHelperConfigService configService) {
        this.name = toCopy.getTeamName();
        GithubHelper githubHelper = new GithubHelper(configService);

        this.githubTeams = new ArrayList<String>();
        for (GithubTeam githubTeam : toCopy.getGithubTeams()) {
            githubTeams.add(githubHelper.getTeamName(githubTeam.getGithubId()));
        }

        this.coordinatorGroups = configService.getGroupsForRole(this.name, TeamToGroup.Role.COORDINATOR);
        this.seniorGroups = configService.getGroupsForRole(this.name, TeamToGroup.Role.SENIOR);
        this.developerGroups = configService.getGroupsForRole(this.name, TeamToGroup.Role.DEVELOPER);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getGithubTeams() {
        return githubTeams;
    }

    public void setGithubTeams(List<String> githubTeams) {
        this.githubTeams = githubTeams;
    }

    public List<String> getCoordinatorGroups() {
        return coordinatorGroups;
    }

    public void setCoordinatorGroups(List<String> coordinatorGroups) {
        this.coordinatorGroups = coordinatorGroups;
    }

    public List<String> getSeniorGroups() {
        return seniorGroups;
    }

    public void setSeniorGroups(List<String> seniorGroups) {
        this.seniorGroups = seniorGroups;
    }

    public List<String> getDeveloperGroups() {
        return developerGroups;
    }

    public void setDeveloperGroups(List<String> developerGroups) {
        this.developerGroups = developerGroups;
    }
}
