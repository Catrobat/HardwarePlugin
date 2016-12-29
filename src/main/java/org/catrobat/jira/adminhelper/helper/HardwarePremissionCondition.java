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
import org.catrobat.jira.adminhelper.activeobject.Group;
import org.catrobat.jira.adminhelper.activeobject.ReadOnlyHdwGroupService;
import org.catrobat.jira.adminhelper.activeobject.ReadOnlyHdwUserService;

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
        return hasPremission(applicationUser);
    }

    private boolean isReadonlyHardwareUser(ApplicationUser applicationUser)
    {
        return (readOnlyHdwUserService.isReadOnlyHardwareUser(applicationUser.getKey()) ||
                readOnlyHdwGroupService.isInReadOnlyGroup(applicationUser.getName()));
    }

    private boolean hasPremission(ApplicationUser applicationUser)
    {
        boolean has_premission = false;

        if(adminHelperConfigService.getConfiguration().getApprovedGroups().length == 0 &&
                adminHelperConfigService.getConfiguration().getApprovedUsers().length == 0 &&
                groupManager.isUserInGroup(applicationUser, "jira-administrators"))
            has_premission = true;

        else if(isReadonlyHardwareUser(ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()))
            has_premission = true;

        else if(adminHelperConfigService.isUserApproved(applicationUser.getKey()))
            has_premission = true;

        return has_premission;
    }

    public boolean approvedHardwareUser(ApplicationUser user)
    {
       return isReadonlyHardwareUser(user);
    }
}
