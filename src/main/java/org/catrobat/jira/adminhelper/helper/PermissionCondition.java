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

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.plugin.webfragment.conditions.AbstractPermissionCondition;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import org.catrobat.jira.adminhelper.activeobject.AdminHelperConfig;
import org.catrobat.jira.adminhelper.activeobject.AdminHelperConfigService;
import org.catrobat.jira.adminhelper.activeobject.ReadOnlyHdwGroupService;
import org.catrobat.jira.adminhelper.activeobject.ReadOnlyHdwUserService;

import java.util.Collection;
import java.util.Map;

public class PermissionCondition extends AbstractPermissionCondition {

    private final AdminHelperConfigService configurationService;
    private final GroupManager groupManager;
    private final UserManager userManager;
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(PermissionCondition.class);

    public PermissionCondition(PermissionManager permissionManager, AdminHelperConfigService configurationService,
                               UserManager userManager, GroupManager groupManager) {
        super(permissionManager);
        this.configurationService = configurationService;
        this.groupManager = groupManager;
        this.userManager = userManager;
    }


    @Override
    public void init(Map params) {
        // needed to be overridden
    }

    @Override
    public boolean shouldDisplay(ApplicationUser applicationUser, JiraHelper jiraHelper) {
        ApplicationUser current_user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
        return isApproved(current_user);
    }

    public boolean isApproved(ApplicationUser applicationUser) {

        LOGGER.error("current user: " + applicationUser);

        if (applicationUser == null || !ComponentAccessor.getUserUtil().getJiraSystemAdministrators().contains(applicationUser)) {
            return false;
        }

        // check if permissions are set
        AdminHelperConfig config = configurationService.getConfiguration();
        if(config.getApprovedGroups().length == 0 && config.getApprovedUsers().length == 0){
            return true;
        }

        if (configurationService.isUserApproved(applicationUser.getKey())) {
            return true;
        }

        Collection<String> groupNameCollection = groupManager.getGroupNamesForUser(applicationUser);
        for (String groupName : groupNameCollection) {
            if (configurationService.isGroupApproved(groupName))
                return true;
        }

        return false;
    }

    public boolean isApproved(String userName) {
        return isApproved(ComponentAccessor.getUserUtil().getUserByName(userName));
    }
}
