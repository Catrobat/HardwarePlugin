package org.catrobat.jira.adminhelper.activeobject;

import org.catrobat.jira.adminhelper.activeobject.ReadOnlyHdwGroup;

import java.util.List;

/**
 * Created by dominik on 27.12.16.
 */
public interface ReadOnlyHdwGroupService {

    ReadOnlyHdwGroup setGroupName(String group_name);

    ReadOnlyHdwGroup getReadOnlyHdwGroup();

    boolean isInReadOnlyGroup(String UserName);

    void clearReadOnlyHdwGroups();

    List<String> all();
}
