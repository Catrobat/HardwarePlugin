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
public class DeviceServiceImplTest {

    @SuppressWarnings("UnusedDeclaration")
    private EntityManager entityManager;
    private ActiveObjects ao;
    private DeviceService deviceService;
    private LendingService lendingService;
    private HardwareModelService hardwareModelService;

    @Before
    public void setUp() throws Exception {
        assertNotNull(entityManager);
        ao = new TestActiveObjects(entityManager);
        deviceService = new DeviceServiceImpl(ao);
        lendingService = new LendingServiceImpl(ao);
        hardwareModelService = new HardwareModelServiceImpl(ao, new TypeOfDeviceServiceImpl(ao),
                new ProducerServiceImpl(ao), new OperatingSystemServiceImpl(ao));
    }

    @Test
    public void testGetDeviceById() {
        Device device = ao.find(Device.class)[0];
        Device foundDevice = deviceService.getDeviceById(device.getID());
        assertEquals(device.getImei(), foundDevice.getImei());
        assertEquals(device.getSerialNumber(), foundDevice.getSerialNumber());
        assertEquals(device.getInventoryNumber(), foundDevice.getInventoryNumber());
    }

    @Test
    public void testAdd() {
        HardwareModel hardwareModel = ao.find(HardwareModel.class)[0];
        int numberOfDevices = ao.find(Device.class).length;

        // normal add
        Device device = deviceService.add(hardwareModel, "imei", "serial", "inventory", "received", new Date(0L), "useful");
        assertNotNull(device);
        assertFalse(device.getID() == 0);

        ao.flushAll();

        assertEquals(numberOfDevices + 1, ao.find(Device.class).length);
        device = deviceService.getDeviceById(device.getID());
        assertEquals(hardwareModel, device.getHardwareModel());
        assertEquals("imei", device.getImei());
        assertEquals("serial", device.getSerialNumber());
        assertEquals("inventory", device.getInventoryNumber());
        assertEquals("received", device.getReceivedFrom());
        assertEquals(new Date(0L), device.getReceivedDate());
        assertEquals("useful", device.getUsefulLifeOfAsset());

        // add with existing serial
        device = deviceService.add(hardwareModel, "imei123", "serial", "inventory123", "received", new Date(0L), "useful");
        assertNull(device);

        // add with existing imei
        device = deviceService.add(hardwareModel, "imei", "serial123", "inventory123", "received", new Date(0L), "useful");
        assertNull(device);

        // add with existing inventory
        device = deviceService.add(hardwareModel, "imei123", "serial123", "inventory", "received", new Date(0L), "useful");
        assertNull(device);

        ao.flushAll();
        assertEquals(numberOfDevices + 1, ao.find(Device.class).length);

        assertNull(deviceService.add(null, null, null, null, null, null, null));
        assertNull(deviceService.add(hardwareModel, null, null, null, null, null, null));
        assertNull(deviceService.add(hardwareModel, "  ", " ", "   ", null, null, null));

        assertNotNull(deviceService.add(hardwareModel, "a", null, null, null, null, null));
        assertNull(deviceService.add(hardwareModel, "  a ", null, null, null, null, null));
        assertNotNull(deviceService.add(hardwareModel, null, "a", null, null, null, null));
        assertNull(deviceService.add(hardwareModel, null, "  a ", null, null, null, null));
        assertNotNull(deviceService.add(hardwareModel, null, null, "a", null, null, null));
        assertNull(deviceService.add(hardwareModel, null, null, "  a ", null, null, null));
    }

    @Test
    public void testEdit() {
        HardwareModel hardwareModel = ao.find(HardwareModel.class)[1];
        Device device = ao.find(Device.class)[0];
        assertNotSame(device.getHardwareModel().getID(), hardwareModel.getID());

        String imei = " <script>alert('imei');</script>    ";
        String serialNumber = " <script>alert('serial');</script>    ";
        String inventoryNumber = " <script>alert('inventory');</script>    ";
        String receivedFrom = " <script>alert('from');</script>    ";
        Date receivedDate = new Date();
        String usefulLifeOfAsset = " <script>alert('life');</script>    ";
        String sortedOutComment = " <script>alert('comment');</script>    ";
        Date sortedOutDate = new Date();

        deviceService.edit(device, hardwareModel, imei, serialNumber, inventoryNumber, receivedFrom, receivedDate, usefulLifeOfAsset, sortedOutDate, sortedOutComment);
        ao.flushAll();
        device = ao.find(Device.class)[0];
        assertEquals(hardwareModel.getID(), device.getHardwareModel().getID());
        assertEquals(escapeHtml4(imei.trim()), device.getImei());
        assertEquals(escapeHtml4(serialNumber.trim()), device.getSerialNumber());
        assertEquals(escapeHtml4(inventoryNumber.trim()), device.getInventoryNumber());
        assertEquals(escapeHtml4(receivedFrom.trim()), device.getReceivedFrom());
        assertEquals(receivedDate, device.getReceivedDate());
        assertEquals(escapeHtml4(usefulLifeOfAsset.trim()), device.getUsefulLifeOfAsset());
        assertEquals(sortedOutDate, device.getSortedOutDate());
        assertEquals(escapeHtml4(sortedOutComment.trim()), device.getSortedOutComment());

        assertNull(deviceService.edit(null, null, null, null, null, null, null, null, null, null));
        assertNull(deviceService.edit(device, null, null, null, null, null, null, null, null, null));
        assertNull(deviceService.edit(null, hardwareModel, null, null, null, null, null, null, null, null));
        assertNull(deviceService.edit(device, hardwareModel, null, null, null, null, null, null, null, null));

        Device addedDevice = deviceService.add(hardwareModel, "a", null, null, null, null, null);
        assertNull(deviceService.edit(device, hardwareModel, " a  ", null, null, null, null, null, null, null));
        assertNotNull(deviceService.edit(addedDevice, hardwareModel, "a", "b", null, null, null, null, null, null));
    }

    @Test
    public void testEscaping() {
        HardwareModel hardwareModel = ao.find(HardwareModel.class)[0];
        String xss = " <script>alert('xss');</script>    ";

        Device device = deviceService.add(hardwareModel, xss, xss, xss, xss, null, xss);
        assertFalse(device.getID() == 0);
        ao.flushAll();

        device = deviceService.getDeviceById(device.getID());
        assertEquals("Not escaped", escapeHtml4(xss.trim()), device.getImei());
        assertEquals("Not escaped", escapeHtml4(xss.trim()), device.getInventoryNumber());
        assertEquals("Not escaped", escapeHtml4(xss.trim()), device.getReceivedFrom());
        assertEquals("Not escaped", escapeHtml4(xss.trim()), device.getSerialNumber());
        assertEquals("Not escaped", escapeHtml4(xss.trim()), device.getUsefulLifeOfAsset());
    }

    @Test
    public void testAll() {
        int expectedSize = ao.find(Device.class).length;
        assertEquals(expectedSize, deviceService.all().size());

        HardwareModel hardwareModel = ao.find(HardwareModel.class)[0];
        deviceService.add(hardwareModel, "imei1", null, null, "received", new Date(0L), "useful");
        deviceService.add(hardwareModel, "imei2", null, null, "received", new Date(0L), "useful");
        deviceService.add(hardwareModel, "imei3", null, null, "received", new Date(0L), "useful");

        assertEquals(expectedSize + 3, deviceService.all().size());
    }

    @Test
    public void testSortOutDevice() {
        String xss = " <script>alert('xss');</script>    ";
        Date date = new Date();
        deviceService.sortOutDevice(1, date, xss);

        ao.flushAll();

        Device device = deviceService.getDeviceById(1);
        assertEquals(1, device.getID());
        assertEquals(escapeHtml4(xss.trim()), device.getSortedOutComment());
        assertEquals(date, device.getSortedOutDate());
    }

    @Test
    public void testGetCurrentlyAvailableDevices() {
        int numberOfDevices = ao.find(Device.class).length;
        assertEquals(numberOfDevices, deviceService.getCurrentlyAvailableDevices(1).size());

        deviceService.sortOutDevice(1, new Date(), "comment");
        ao.flushAll();
        assertEquals(numberOfDevices - 1, deviceService.getCurrentlyAvailableDevices(1).size());

        lendingService.lendOut(ao.find(Device.class)[1], "user1", "user2", "use", "comment", new Date());
        ao.flushAll();
        assertEquals(numberOfDevices - 2, deviceService.getCurrentlyAvailableDevices(1).size());
    }

    @Test
    public void testGetSortedOutDevices() {
        assertEquals(0, deviceService.getSortedOutDevices().size());

        deviceService.sortOutDevice(1, new Date(), "comment");
        ao.flushAll();
        assertEquals(1, deviceService.getSortedOutDevices().size());

        assertNull(deviceService.sortOutDevice(0, new Date(), "comment"));
        assertNull(deviceService.sortOutDevice(1, null, "comment"));
        assertNull(deviceService.sortOutDevice(1, new Date(), null));

        assertNotNull(deviceService.sortOutDevice(2, new Date(), null));
        Device device = ao.find(Device.class)[1];
        device.setSortedOutDate(null);
        device.save();
        ao.flushAll();
        assertNotNull(deviceService.sortOutDevice(2, new Date(), "   "));
    }

    @Test
    public void testGetSortedOutDevicesForHardware() {
        assertEquals(0, deviceService.getSortedOutDevices().size());

        deviceService.sortOutDevice(1, new Date(), "comment");
        ao.flushAll();
        assertEquals(1, deviceService.getSortedOutDevicesForHardware(hardwareModelService.get(1)).size());
        assertEquals(0, deviceService.getSortedOutDevicesForHardware(hardwareModelService.get(2)).size());
    }

}
