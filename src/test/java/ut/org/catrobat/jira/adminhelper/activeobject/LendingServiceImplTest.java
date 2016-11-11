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
import org.catrobat.jira.adminhelper.activeobject.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;
import static org.junit.Assert.*;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(AdminHelperDatabaseUpdater.class)
public class LendingServiceImplTest {

    @SuppressWarnings("UnusedDeclaration")
    private EntityManager entityManager;
    private ActiveObjects ao;
    private LendingService lendingService;

    @Before
    public void setUp() throws Exception {
        assertNotNull(entityManager);
        ao = new TestActiveObjects(entityManager);
        lendingService = new LendingServiceImpl(ao);
    }

    @Test
    public void testLendOut() {
        assertEquals(0, ao.find(Lending.class).length);

        Date date = new Date();
        String user1 = " <script>alert('user1');</script>    ";
        String user2 = " <script>alert('user2');</script>    ";
        String purpose = " <script>alert('purpose');</script>    ";
        String comment = " <script>alert('comment');</script>    ";
        Lending lending = lendingService.lendOut(ao.find(Device.class)[0], user1, user2, purpose, comment, date);

        ao.flushAll();
        assertEquals(1, ao.find(Lending.class).length);
        assertEquals(lending.getID(), ao.find(Lending.class)[0].getID());
        lending = ao.find(Lending.class)[0];
        assertEquals(escapeHtml4(user1.trim()), lending.getLendingByUserKey());
        assertEquals(escapeHtml4(user2.trim()), lending.getLendingIssuerUserKey());
        assertEquals(escapeHtml4(purpose.trim()), lending.getPurpose());
        assertEquals(escapeHtml4(comment.trim()), lending.getComment());

        // messing around a bit
        assertNull(lendingService.lendOut(null, null, null, null, null, null));
        assertNull(lendingService.lendOut(ao.find(Device.class)[0], " a ", " a ", null, null, new Date())); // already lent out
        assertNotNull(lendingService.lendOut(ao.find(Device.class)[1], " a ", " a ", null, null, new Date()));
    }

    @Test
    public void testBringBack() {
        assertEquals(0, ao.find(Lending.class).length);

        // time won't matter - just different days
        Date beginDate = new Date(System.currentTimeMillis());
        Date endDate = new Date(System.currentTimeMillis() + 86400000);
        Date wrongEndDate = new Date(beginDate.getTime() - 86400000);

        // test real scenario (plus escaping)
        lendingService.lendOut(ao.find(Device.class)[0], "user1", "user2", "purpose", "comment", beginDate);
        ao.flushAll();

        String purpose = " <script>alert('purpose');</script>    ";
        String comment = " <script>alert('comment');</script>    ";

        Lending lending = ao.find(Lending.class)[0];
        assertNotNull(lending);
        assertFalse(lending.getID() == 0);

        lendingService.bringBack(lending, purpose, comment, endDate);
        ao.flushAll();

        lending = ao.find(Lending.class)[0];
        assertEquals(escapeHtml4(purpose.trim()), lending.getPurpose());
        assertEquals(escapeHtml4(comment.trim()), lending.getComment());
        assertEquals(endDate, lending.getEnd());

        // test wrong end date (before begin date)
        lendingService.lendOut(ao.find(Device.class)[0], "user1", "user2", "purpose", "comment", beginDate);
        ao.flushAll();

        lending = ao.find(Lending.class)[1];
        assertNotNull(lending);
        assertFalse(lending.getID() == 0);

        assertNull(lendingService.bringBack(lending, "bring back wrong", "bring back wrong comment", wrongEndDate));
        ao.flushAll();

        lending = ao.find(Lending.class)[1];
        assertEquals("purpose", lending.getPurpose());
        assertEquals("comment", lending.getComment());
        assertNull(lending.getEnd());

        // messing around a bit
        assertNull(lendingService.bringBack(null, null, null, null));
        assertNull(lendingService.bringBack(null, null, null, new Date()));
        assertNotNull(lendingService.bringBack(lending, null, null, new Date()));
    }

    @Test
    public void testCurrentlyLentOut() {
        assertEquals(0, lendingService.currentlyLentOut().size());

        lendingService.lendOut(ao.find(Device.class)[0], "user1", "user2", "purpose", "comment", new Date());
        lendingService.lendOut(ao.find(Device.class)[1], "user1", "user2", "purpose", "comment", new Date());
        ao.flushAll();

        assertEquals(2, lendingService.currentlyLentOut().size());

        lendingService.bringBack(ao.find(Lending.class)[0], "purpose", "comment", new Date());
        ao.flushAll();

        assertEquals(1, lendingService.currentlyLentOut().size());
    }

    @Test
    public void testCurrentlyLentOutDevices() {
        assertEquals(0, lendingService.currentlyLentOutDevices(null).size());
        assertEquals(0, lendingService.currentlyLentOutDevices(ao.find(HardwareModel.class)[0]).size());

        lendingService.lendOut(ao.find(Device.class)[0], "user1", "user2", "purpose", "comment", new Date());
        lendingService.lendOut(ao.find(Device.class)[1], "user1", "user2", "purpose", "comment", new Date());
        ao.flushAll();

        assertEquals(2, lendingService.currentlyLentOutDevices(ao.find(HardwareModel.class)[0]).size());
        assertEquals(0, lendingService.currentlyLentOutDevices(ao.find(HardwareModel.class)[1]).size());
    }

    @Test
    public void testAll() {
        assertEquals(0, lendingService.all().size());

        lendingService.lendOut(ao.find(Device.class)[0], "user1", "user2", "purpose", "comment", new Date());
        Lending lending = lendingService.lendOut(ao.find(Device.class)[1], "user1", "user2", "purpose", "comment", new Date());
        lendingService.bringBack(lending, null, null, new Date());
        ao.flushAll();

        assertEquals(2, lendingService.all().size());
    }

    @Test
    public void testSearchAllForUser() {
        assertEquals(0, lendingService.searchAllForUser("user1").size());
        assertEquals(0, lendingService.searchAllForUser("user2").size());

        lendingService.lendOut(ao.find(Device.class)[0], "user1", "user2", "purpose", "comment", new Date());
        lendingService.lendOut(ao.find(Device.class)[1], "user1", "user2", "purpose", "comment", new Date());

        assertEquals(2, lendingService.searchAllForUser("user1").size());
        assertEquals(2, lendingService.searchAllForUser("uSEr1").size());
        assertEquals(0, lendingService.searchAllForUser("user2").size());

        assertNull(lendingService.searchAllForUser(null));
        assertNull(lendingService.searchAllForUser(""));
        assertNull(lendingService.searchAllForUser("   "));
    }
}
