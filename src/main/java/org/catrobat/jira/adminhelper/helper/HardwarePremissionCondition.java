package org.catrobat.jira.adminhelper.helper;

/**
 * Created by dominik on 29.12.16.
 */

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.plugin.webfragment.conditions.JiraGlobalPermissionCondition;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.security.GlobalPermissionManager;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import org.catrobat.jira.adminhelper.activeobject.AdminHelperConfigService;
import org.catrobat.jira.adminhelper.activeobject.ReadOnlyHdwGroupService;
import org.catrobat.jira.adminhelper.activeobject.ReadOnlyHdwUserService;

import java.util.Collection;

public class HardwarePremissionCondition extends JiraGlobalPermissionCondition {

    private final UserManager userManager;
    private final ReadOnlyHdwGroupService readOnlyHdwGroupService;
    private final ReadOnlyHdwUserService readOnlyHdwUserService;
    private final AdminHelperConfigService adminHelperConfigService;
    private final GroupManager groupManager;

    public HardwarePremissionCondition(GlobalPermissionManager premissionManager, UserManager userManager,
                                       ReadOnlyHdwGroupService readOnlyHdwGroupService,
                                       ReadOnlyHdwUserService readOnlyHdwUserService,
                                       AdminHelperConfigService adminHelperConfigService,
                                       GroupManager groupManager)
    {
        super(premissionManager);
        this.userManager = userManager;
        this.readOnlyHdwGroupService = readOnlyHdwGroupService;
        this.readOnlyHdwUserService = readOnlyHdwUserService;
        this.adminHelperConfigService = adminHelperConfigService;
        this.groupManager = groupManager;
    }
    @Override
    public boolean shouldDisplay(ApplicationUser applicationUser, JiraHelper jiraHelper) {
        return hasPermission(applicationUser);
    }

    private boolean isReadonlyHardwareUser(ApplicationUser applicationUser)
    {
        return (readOnlyHdwUserService.isReadOnlyHardwareUser(applicationUser.getKey()) ||
                readOnlyHdwGroupService.isInReadOnlyGroup(applicationUser.getName()));
    }

    private boolean hasPermission(ApplicationUser applicationUser)
    {
        if(applicationUser == null || !ComponentAccessor.getUserUtil().getJiraSystemAdministrators().contains(applicationUser)) {
            return isReadonlyHardwareUser(ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser());
        }
        else if(adminHelperConfigService.isUserApproved(applicationUser.getKey())) {
            return true;
        }

        Collection<String> groupNameCollection = groupManager.getGroupNamesForUser(applicationUser);
        for (String groupName : groupNameCollection) {
            if (adminHelperConfigService.isGroupApproved(groupName))
                return true;
        }

        return false;
    }

    public boolean approvedHardwareUser(ApplicationUser user)
    {
       return isReadonlyHardwareUser(user);
    }
}
