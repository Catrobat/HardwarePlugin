package org.catrobat.jira.adminhelper.activeobject;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import net.java.ao.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dominik on 27.12.16.
 */
public class ReadOnlyHdwUserServiceImpl implements ReadOnlyHdwUserService {

    private final ActiveObjects ao;

    public ReadOnlyHdwUserServiceImpl(ActiveObjects ao)
    {
        this.ao = ao;
    }

    @Override
    public ReadOnlyHdwUser addUserKey(String userKey)
    {
        if(userKey == null || userKey.trim().length() == 0)
            return null;

        userKey = userKey.trim();

        ReadOnlyHdwUser[] users = ao.find(ReadOnlyHdwUser.class, Query.select()
                .where("upper(\"USER_KEY\") = upper(?)", userKey));

        if(users.length == 0)
        {
            ReadOnlyHdwUser user = ao.create(ReadOnlyHdwUser.class);
            user.setUserKey(userKey);
            user.save();
            return user;
        }
        else
            return users[0];
    }

    @Override
    public ReadOnlyHdwUser getReadOnlyHardwareUser()
    {
        ReadOnlyHdwUser[] users = ao.find(ReadOnlyHdwUser.class);
        if(users.length == 0)
        {
            ao.create(ReadOnlyHdwUser.class).save();
            users = ao.find(ReadOnlyHdwUser.class);
        }
        return users[0];
    }

    @Override
    public boolean isReadOnlyHardwareUser(String userKey)
    {
        if(userKey != null)
            userKey = userKey.trim();

        return ao.find(ReadOnlyHdwUser.class, Query.select()
                .where("upper(\"USER_KEY\") = upper(?)", userKey)).length != 0;
    }

    @Override
    public void clearReadOnlyHardwareUsers()
    {
        for(ReadOnlyHdwUser user: ao.find(ReadOnlyHdwUser.class))
                ao.delete(user);
    }

    @Override
    public List<String> all()
    {
        List<String> users = new ArrayList<String>();
        UserManager userManager = ComponentAccessor.getUserManager();
        for(ReadOnlyHdwUser user : ao.find(ReadOnlyHdwUser.class))
        {
            String key = user.getUserKey();
            ApplicationUser temp_user = userManager.getUserByKey(key);
            String user_name = temp_user.getName();
            if(user_name != null)
                users.add(user_name);
        }
        return users;
    }
}
