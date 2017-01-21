package org.catrobat.jira.adminhelper.activeobject;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;
import net.java.ao.Query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by dominik on 27.12.16.
 */
public class ReadOnlyHdwGroupServiceImpl implements ReadOnlyHdwGroupService {

    private final ActiveObjects ao;

    public ReadOnlyHdwGroupServiceImpl(ActiveObjects ao)
    {
        this.ao = ao;
    }
    @Override
    public ReadOnlyHdwGroup setGroupName(String group_name)
    {
        group_name = group_name.trim();

        ReadOnlyHdwGroup[] groups = ao.find(ReadOnlyHdwGroup.class, Query.select()
                .where("upper(\"GROUP_NAME\") = upper(?)", group_name));
        if(groups.length == 0) {
            ReadOnlyHdwGroup group = ao.create(ReadOnlyHdwGroup.class);
            group.setGroupName(group_name);
            group.save();

            return group;
        }
        else
            return groups[0];
    }

    @Override
    public ReadOnlyHdwGroup getReadOnlyHdwGroup()
    {
        ReadOnlyHdwGroup[] groups = ao.find(ReadOnlyHdwGroup.class);
        if(groups.length == 0) {
            ao.create(ReadOnlyHdwGroup.class).save();
            groups = ao.find(ReadOnlyHdwGroup.class);
        }
        return groups[0];
    }


    @Override
    public boolean isInReadOnlyGroup(String user_name)
    {
        boolean isInReadOnlyGroup = false;

        if(user_name != null)
            user_name = user_name.trim();

        ApplicationUser user = ComponentAccessor.getUserManager().getUserByName(user_name);
        if(user == null)
            return true;

        Collection<Group> groups_of_user = ComponentAccessor.getGroupManager().getGroupsForUser(user);

        for(Group group : groups_of_user)
        {
            if(ao.find(ReadOnlyHdwGroup.class, Query.select().where("upper(\"GROUP_NAME\") = upper(?)",
                    group.getName().trim())).length != 0) {
                isInReadOnlyGroup = true;
                break;
            }
        }
        return isInReadOnlyGroup;
    }

    @Override
    public void clearReadOnlyHdwGroups()
    {
        for(ReadOnlyHdwGroup group: ao.find(ReadOnlyHdwGroup.class))
            ao.delete(group);
    }

    @Override
    public List<String> all()
    {
        List<String> groups = new ArrayList<>();

        for(ReadOnlyHdwGroup group : ao.find(ReadOnlyHdwGroup.class))
        {
            groups.add(group.getGroupName());
        }
        return groups;
    }
}
