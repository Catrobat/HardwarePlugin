package org.catrobat.jira.adminhelper.activeobject;

/**
 * Created by dominik on 26.12.16.
 */

import net.java.ao.Entity;
import net.java.ao.Preload;

@Preload
public interface ReadOnlyHdwUser extends Entity {

    void setUserKey(String userKey);

    String getUserKey();
}