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
import org.catrobat.jira.adminhelper.activeobject.TypeOfDevice;
import org.catrobat.jira.adminhelper.activeobject.TypeOfDeviceService;
import org.catrobat.jira.adminhelper.activeobject.TypeOfDeviceServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;
import static org.junit.Assert.*;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(AdminHelperDatabaseUpdater.class)
public class TypeOfDeviceServiceImplTest {

    @SuppressWarnings("UnusedDeclaration")
    private EntityManager entityManager;
    private ActiveObjects ao;
    private TypeOfDeviceService typeOfDeviceService;

    @Before
    public void setUp() throws Exception {
        assertNotNull(entityManager);
        ao = new TestActiveObjects(entityManager);
        typeOfDeviceService = new TypeOfDeviceServiceImpl(ao);
    }

    @Test
    public void testGetOrCreateTypeOfDevice() {
        String xss = " <script>alert('xss');</script>   ";
        assertEquals(3, ao.find(TypeOfDevice.class).length);

        TypeOfDevice typeOfDevice = typeOfDeviceService.getOrCreateTypeOfDevice(AdminHelperDatabaseUpdater.TYPE_OF_DEVICE_1);
        assertNotNull(typeOfDevice);
        assertFalse(typeOfDevice.getID() == 0);
        assertEquals(AdminHelperDatabaseUpdater.TYPE_OF_DEVICE_1, typeOfDevice.getTypeOfDeviceName());
        ao.flushAll();
        assertEquals(3, ao.find(TypeOfDevice.class).length);

        typeOfDevice = typeOfDeviceService.getOrCreateTypeOfDevice("   " + AdminHelperDatabaseUpdater.TYPE_OF_DEVICE_1 + " ");
        assertNotNull(typeOfDevice);
        assertFalse(typeOfDevice.getID() == 0);
        assertEquals(AdminHelperDatabaseUpdater.TYPE_OF_DEVICE_1, typeOfDevice.getTypeOfDeviceName());
        ao.flushAll();
        assertEquals(3, ao.find(TypeOfDevice.class).length);

        typeOfDevice = typeOfDeviceService.getOrCreateTypeOfDevice(xss);
        assertNotNull(typeOfDevice);
        assertFalse(typeOfDevice.getID() == 0);
        assertEquals(escapeHtml4(xss.trim()), typeOfDevice.getTypeOfDeviceName());
        ao.flushAll();
        assertEquals(4, ao.find(TypeOfDevice.class).length);

        assertNull(typeOfDeviceService.getOrCreateTypeOfDevice(null));
        assertNull(typeOfDeviceService.getOrCreateTypeOfDevice(""));
        assertNull(typeOfDeviceService.getOrCreateTypeOfDevice("    "));
    }

    @Test
    public void testSearchTypeOfDevice() {
        assertEquals(0, typeOfDeviceService.searchTypeOfDevice("blub").size());
        assertEquals(3, typeOfDeviceService.searchTypeOfDevice("a").size());
        assertEquals(2, typeOfDeviceService.searchTypeOfDevice("t").size());
        assertEquals(1, typeOfDeviceService.searchTypeOfDevice("d").size());

        assertEquals(0, typeOfDeviceService.searchTypeOfDevice(null).size());
        assertEquals(3, typeOfDeviceService.searchTypeOfDevice("").size());
        assertEquals(1, typeOfDeviceService.searchTypeOfDevice(" ").size());
    }

    @Test
    public void testCleanUnused() {
        assertEquals(3, ao.find(TypeOfDevice.class).length);

        typeOfDeviceService.cleanUnused();
        ao.flushAll();
        assertEquals(3, ao.find(TypeOfDevice.class).length);

        HardwareModel hardwareModel = ao.find(HardwareModel.class)[2];
        hardwareModel.setTypeOfDevice(null);
        hardwareModel.save();
        ao.flushAll();

        typeOfDeviceService.cleanUnused();
        ao.flushAll();

        assertEquals(2, ao.find(TypeOfDevice.class).length);
    }

}
