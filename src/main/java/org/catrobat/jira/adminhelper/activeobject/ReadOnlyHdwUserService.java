package org.catrobat.jira.adminhelper.activeobject;

import com.atlassian.activeobjects.tx.Transactional;

import java.util.List;

/**
 * Created by dominik on 26.12.16.
 */
@Transactional
public interface ReadOnlyHdwUserService {

    ReadOnlyHdwUser addUserKey(String userKey);

    ReadOnlyHdwUser getReadOnlyHardwareUser();

    boolean isReadOnlyHardwareUser(String userKey);

    void clearReadOnlyHardwareUsers();

    List<String> all();
}
