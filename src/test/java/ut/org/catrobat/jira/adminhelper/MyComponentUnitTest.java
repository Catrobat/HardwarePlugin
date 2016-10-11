package ut.org.catrobat.jira.adminhelper;

import org.junit.Test;
import org.catrobat.jira.adminhelper.api.MyPluginComponent;
import org.catrobat.jira.adminhelper.impl.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}