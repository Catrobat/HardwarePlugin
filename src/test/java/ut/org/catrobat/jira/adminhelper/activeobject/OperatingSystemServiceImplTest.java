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
import org.catrobat.jira.adminhelper.activeobject.HardwareModel;
import org.catrobat.jira.adminhelper.activeobject.OperatingSystem;
import org.catrobat.jira.adminhelper.activeobject.OperatingSystemService;
import org.catrobat.jira.adminhelper.activeobject.OperatingSystemServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;
import static org.junit.Assert.*;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(AdminHelperDatabaseUpdater.class)
public class OperatingSystemServiceImplTest {

    @SuppressWarnings("UnusedDeclaration")
    private EntityManager entityManager;
    private ActiveObjects ao;
    private OperatingSystemService operatingSystemService;

    @Before
    public void setUp() throws Exception {
        assertNotNull(entityManager);
        ao = new TestActiveObjects(entityManager);
        operatingSystemService = new OperatingSystemServiceImpl(ao);
    }

    @Test
    public void testGetOrCreateOperatingSystem() {
        String xss = " <script>alert('xss');</script>   ";
        assertEquals(3, ao.find(OperatingSystem.class).length);

        OperatingSystem os = operatingSystemService.getOrCreateOperatingSystem(AdminHelperDatabaseUpdater.OPERATING_SYSTEM_1);
        assertNotNull(os);
        assertFalse(os.getID() == 0);
        assertEquals(AdminHelperDatabaseUpdater.OPERATING_SYSTEM_1, os.getOperatingSystemName());
        ao.flushAll();
        assertEquals(3, ao.find(OperatingSystem.class).length);

        os = operatingSystemService.getOrCreateOperatingSystem("   " + AdminHelperDatabaseUpdater.OPERATING_SYSTEM_1 + " ");
        assertNotNull(os);
        assertFalse(os.getID() == 0);
        assertEquals(AdminHelperDatabaseUpdater.OPERATING_SYSTEM_1, os.getOperatingSystemName());
        ao.flushAll();
        assertEquals(3, ao.find(OperatingSystem.class).length);

        os = operatingSystemService.getOrCreateOperatingSystem(xss);
        assertNotNull(os);
        assertFalse(os.getID() == 0);
        assertEquals(escapeHtml4(xss.trim()), os.getOperatingSystemName());
        ao.flushAll();
        assertEquals(4, ao.find(OperatingSystem.class).length);

        assertNull(operatingSystemService.getOrCreateOperatingSystem(null));
        assertNull(operatingSystemService.getOrCreateOperatingSystem(""));
        assertNull(operatingSystemService.getOrCreateOperatingSystem("    "));
    }

    @Test
    public void testSearchOperatingSystems() {
        assertEquals(0, operatingSystemService.searchOperatingSystems("blub").size());
        assertEquals(3, operatingSystemService.searchOperatingSystems("o").size());
        assertEquals(2, operatingSystemService.searchOperatingSystems("n").size());
        assertEquals(1, operatingSystemService.searchOperatingSystems("w").size());

        assertEquals(0, operatingSystemService.searchOperatingSystems(null).size());
        assertEquals(3, operatingSystemService.searchOperatingSystems("").size());
        assertEquals(1, operatingSystemService.searchOperatingSystems(" ").size());
    }

    @Test
    public void testCleanUnused() {
        assertEquals(3, ao.find(OperatingSystem.class).length);

        operatingSystemService.cleanUnused();
        ao.flushAll();
        assertEquals(3, ao.find(OperatingSystem.class).length);

        HardwareModel hardwareModel = ao.find(HardwareModel.class)[0];
        hardwareModel.setOperatingSystem(null);
        hardwareModel.save();
        ao.flushAll();

        operatingSystemService.cleanUnused();
        ao.flushAll();

        assertEquals(2, ao.find(OperatingSystem.class).length);
    }
}
