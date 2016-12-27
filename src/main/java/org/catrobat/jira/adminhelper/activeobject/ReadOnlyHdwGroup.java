package org.catrobat.jira.adminhelper.activeobject;


import net.java.ao.Entity;

/**
 * Created by dominik on 27.12.16.
 */
public interface ReadOnlyHdwGroup extends Entity {

    void setGroupName(String group_name);

    String getGroupName();
}
