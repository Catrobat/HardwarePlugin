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

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.web.dispatcher.PluginsAwareViewMapping;
import org.catrobat.jira.adminhelper.activeobject.*;
import org.catrobat.jira.adminhelper.helper.HardwarePremissionCondition;
import org.catrobat.jira.adminhelper.helper.PermissionCondition;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

public abstract class RestHelper {

    private final PermissionCondition permissionCondition;
    private final UserManager userManager;
    private final GroupManager groupManager;
    private final AdminHelperConfigService configService;
    private HardwarePremissionCondition hardwarePremissionCondition;
    private ReadOnlyHdwGroupService readOnlyHdwGroupService;
    private ReadOnlyHdwUserService readOnlyHdwUserService;

    private boolean isHardwareSerlet;

    public RestHelper(PermissionManager permissionManager, AdminHelperConfigService configService,
                      UserManager userManager, GroupManager groupManager) {
        this.userManager = userManager;
        this.configService = configService;
        this.groupManager = groupManager;
        this.permissionCondition = new PermissionCondition(permissionManager, configService, userManager, groupManager);
        this.hardwarePremissionCondition = null;
    }

    protected Response checkPermission(HttpServletRequest request) {

        ApplicationUser currently_logged_in_user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();

        if (currently_logged_in_user.getUsername() == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } else if (!ComponentAccessor.getUserUtil().getJiraSystemAdministrators().contains(currently_logged_in_user)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } else if (!permissionCondition.isApproved(currently_logged_in_user.getUsername())) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        return null;
    }

    protected Response checkHardwareRestPremission()
    {
        hardwarePremissionCondition = new HardwarePremissionCondition(null, userManager, readOnlyHdwGroupService,
                readOnlyHdwUserService,configService,groupManager);

        ApplicationUser currently_logged_in_user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();

        if (currently_logged_in_user.getUsername() == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        else if (!permissionCondition.isApproved(currently_logged_in_user.getUsername())) {
            if (isHardwareSerlet) {
                if (!hardwarePremissionCondition.approvedHardwareUser(currently_logged_in_user))
                    return Response.status(Response.Status.UNAUTHORIZED).build();
            }
        }
        return null;
    }

    public void setHardwareServices(ReadOnlyHdwGroupService readOnlyHdwGroupService, ReadOnlyHdwUserService readOnlyHdwUserService)
    {
        this.isHardwareSerlet = true;
        this.readOnlyHdwGroupService = readOnlyHdwGroupService;
        this.readOnlyHdwUserService = readOnlyHdwUserService;
    }
}
