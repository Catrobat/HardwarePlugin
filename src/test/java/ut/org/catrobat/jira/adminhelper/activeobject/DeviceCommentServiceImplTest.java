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

package ut.org.catrobat.jira.adminhelper.activeobject;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.catrobat.jira.adminhelper.activeobject.Device;
import org.catrobat.jira.adminhelper.activeobject.DeviceComment;
import org.catrobat.jira.adminhelper.activeobject.DeviceCommentService;
import org.catrobat.jira.adminhelper.activeobject.DeviceCommentServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;
import static org.junit.Assert.*;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(AdminHelperDatabaseUpdater.class)
public class DeviceCommentServiceImplTest {

    @SuppressWarnings("UnusedDeclaration")
    private EntityManager entityManager;
    private ActiveObjects ao;
    private DeviceCommentService deviceCommentService;

    @Before
    public void setUp() throws Exception {
        assertNotNull(entityManager);
        ao = new TestActiveObjects(entityManager);
        deviceCommentService = new DeviceCommentServiceImpl(ao);
    }

    @Test
    public void testAddDeviceComment() {
        String author = " <script>alert('author');</script>    ";
        String comment = " <script>alert('comment');</script>    ";
        Device device = ao.find(Device.class)[0];

        assertEquals(0, ao.find(DeviceComment.class).length);

        deviceCommentService.addDeviceComment(device, author, comment);
        ao.flushAll();

        assertEquals(1, ao.find(DeviceComment.class).length);
        device = ao.find(Device.class)[0];
        assertEquals(1, device.getDeviceComments().length);
        assertEquals(escapeHtml4(author.trim()), device.getDeviceComments()[0].getAuthor());
        assertEquals(escapeHtml4(comment.trim()), device.getDeviceComments()[0].getComment());

        // messing around a bit
        assertNull(deviceCommentService.addDeviceComment(null, null, null));
        assertNull(deviceCommentService.addDeviceComment(device, "   ", "   "));
        assertNull(deviceCommentService.addDeviceComment(null, " ", " "));
        assertNull(deviceCommentService.addDeviceComment(null, " a", null));
        assertNull(deviceCommentService.addDeviceComment(null, "a", "b"));
    }
}
