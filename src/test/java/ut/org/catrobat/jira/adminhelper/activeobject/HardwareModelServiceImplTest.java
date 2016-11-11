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

import java.util.List;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;
import static org.junit.Assert.*;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(AdminHelperDatabaseUpdater.class)
public class HardwareModelServiceImplTest {

    @SuppressWarnings("UnusedDeclaration")
    private EntityManager entityManager;
    private ActiveObjects ao;
    private HardwareModelServiceImpl hardwareModelService;

    @Before
    public void setUp() throws Exception {
        assertNotNull(entityManager);
        ao = new TestActiveObjects(entityManager);
        hardwareModelService = new HardwareModelServiceImpl(ao, new TypeOfDeviceServiceImpl(ao),
                new ProducerServiceImpl(ao), new OperatingSystemServiceImpl(ao));
    }

    @Test
    public void testEscaping() {
        String xss = " <script>alert('xss');</script>   ";

        HardwareModel model = hardwareModelService.add(xss, xss, xss, xss, xss, xss, xss);
        assertFalse(model.getID() == 0);
        ao.flushAll();

        model = hardwareModelService.get(model.getID());
        assertEquals("Not escaped", escapeHtml4(xss.trim()), model.getName());
        assertEquals("Not escaped", escapeHtml4(xss.trim()), model.getPrice());
        assertEquals("Not escaped", escapeHtml4(xss.trim()), model.getVersion());
        assertEquals("Not escaped", escapeHtml4(xss.trim()), model.getArticleNumber());
        assertNotNull(model.getOperatingSystem());
        assertEquals("Not escaped", escapeHtml4(xss.trim()), model.getOperatingSystem().getOperatingSystemName());
        assertNotNull(model.getTypeOfDevice());
        assertEquals("Not escaped", escapeHtml4(xss.trim()), model.getTypeOfDevice().getTypeOfDeviceName());
        assertNotNull(model.getProducer());
        assertEquals("Not escaped", escapeHtml4(xss.trim()), model.getProducer().getProducerName());

        model = hardwareModelService.add(xss + "#2", (TypeOfDevice) null, xss, xss, null, null, xss);
        assertFalse(model.getID() == 0);
        ao.flushAll();

        model = hardwareModelService.get(model.getID());
        assertEquals("Not escaped", escapeHtml4((xss + "#2").trim()), model.getName());
        assertEquals("Not escaped", escapeHtml4(xss.trim()), model.getPrice());
        assertEquals("Not escaped", escapeHtml4(xss.trim()), model.getVersion());
        assertEquals("Not escaped", escapeHtml4(xss.trim()), model.getArticleNumber());
    }

    @Test
    public void testGet() {
        HardwareModel model = ao.find(HardwareModel.class)[0];
        HardwareModel modelGet = hardwareModelService.get(model.getID());

        assertEquals(model.getID(), modelGet.getID());
        assertEquals(model.getName(), modelGet.getName());
        assertEquals(model.getVersion(), modelGet.getVersion());

        assertNull(hardwareModelService.get(0));
        assertNull(hardwareModelService.get(100));
    }

    @Test
    public void testEdit() {
        final String changedName = " <script>alert('name');</script>   ";
        final String changedTypeOfDevice = " <script>alert('changedTypeOfDevice');</script>   ";
        final String changedVersion = " <script>alert('changedVersion');</script>   ";
        final String changedPrice = " <script>alert('changedPrice');</script>   ";
        final String changedProducer = " <script>alert('changedProducer');</script>   ";
        final String changedOperationSystem = " <script>alert('changedOperationSystem');</script>   ";
        final String changedArticleNumber = " <script>alert('changedArticleNumber');</script>   ";
        hardwareModelService.edit(1, changedName, changedTypeOfDevice, changedVersion, changedPrice, changedProducer,
                changedOperationSystem, changedArticleNumber);
        ao.flushAll();

        HardwareModel model = hardwareModelService.get(1);
        assertEquals(escapeHtml4(changedName.trim()), model.getName());
        assertNotNull(model.getTypeOfDevice());
        assertEquals(escapeHtml4(changedTypeOfDevice.trim()), model.getTypeOfDevice().getTypeOfDeviceName());
        assertEquals(escapeHtml4(changedVersion.trim()), model.getVersion());
        assertEquals(escapeHtml4(changedPrice.trim()), model.getPrice());
        assertNotNull(model.getProducer());
        assertEquals(escapeHtml4(changedProducer.trim()), model.getProducer().getProducerName());
        assertNotNull(model.getOperatingSystem());
        assertEquals(escapeHtml4(changedOperationSystem.trim()), model.getOperatingSystem().getOperatingSystemName());
        assertEquals(escapeHtml4(changedArticleNumber.trim()), model.getArticleNumber());

        model = hardwareModelService.get(2);
        String originalName = model.getName();
        String originalVersion = model.getVersion();
        assertNull(hardwareModelService.edit(2, changedName, null, changedVersion, null, null, null, null));
        ao.flushAll();

        model = hardwareModelService.get(2);
        assertEquals(originalName, model.getName());
        assertEquals(originalVersion, model.getVersion());

        assertNotNull(hardwareModelService.edit(1, "  b ", null, null, null, null, null, null));
        assertNull(hardwareModelService.edit(1, "   ", null, null, null, null, null, null));
        assertNull(hardwareModelService.edit(0, "a", "b", "c", "d", "e", "f", "g"));
    }

    @Test
    public void testMoveDevices() {
        // test to move 2 devices
        HardwareModel from = hardwareModelService.get(1);
        HardwareModel to = hardwareModelService.get(2);
        assertEquals(2, from.getDevices().length);
        assertEquals(0, to.getDevices().length);

        hardwareModelService.moveDevices(from, to);
        ao.flushAll();

        from = hardwareModelService.get(1);
        to = hardwareModelService.get(2);
        assertEquals(0, from.getDevices().length);
        assertEquals(2, to.getDevices().length);

        // test empty hardware model
        from = hardwareModelService.get(1);
        to = hardwareModelService.get(3);
        assertEquals(0, from.getDevices().length);
        assertEquals(0, to.getDevices().length);

        hardwareModelService.moveDevices(from, to);
        ao.flushAll();

        from = hardwareModelService.get(1);
        to = hardwareModelService.get(3);
        assertEquals(0, from.getDevices().length);
        assertEquals(0, to.getDevices().length);

        from = hardwareModelService.get(2);
        assertEquals(2, from.getDevices().length);
        hardwareModelService.moveDevices(from, null);
        ao.flushAll();
        from = hardwareModelService.get(2);
        assertEquals(2, from.getDevices().length);
        hardwareModelService.moveDevices(null, from);

        ao.flushAll();
        from = hardwareModelService.get(2);
        assertEquals(2, from.getDevices().length);

        // assert no exception thrown
        hardwareModelService.moveDevices(null, null);
    }

    @Test
    public void testDelete() {
        HardwareModel model = hardwareModelService.get(1);
        assertNotNull(model);
        assertFalse(model.getID() == 0);

        hardwareModelService.delete(model);
        ao.flushAll();

        model = hardwareModelService.get(1);
        assertNull(model);
    }

    @Test
    public void testAdd() throws Exception {
        final String name = AdminHelperDatabaseUpdater.HARDWARE_NAME_1 + "#1";
        final String type = AdminHelperDatabaseUpdater.TYPE_OF_DEVICE_1 + "#1";
        final String model = AdminHelperDatabaseUpdater.HARDWARE_VERSION_1 + "#1";
        final String price = AdminHelperDatabaseUpdater.HARDWARE_PRICE_1 + "#1";
        final String producer = AdminHelperDatabaseUpdater.PRODUCER_1 + "#1";
        final String operatingSystem = AdminHelperDatabaseUpdater.OPERATING_SYSTEM_1 + "#1";
        final String articleNumber = AdminHelperDatabaseUpdater.HARDWARE_ARTICLE_NUMBER_1 + "#1";
        assertEquals(4, ao.find(HardwareModel.class).length);

        final HardwareModel add = hardwareModelService.add(name, type, model, price, producer, operatingSystem, articleNumber);
        assertFalse(add.getID() == 0);

        ao.flushAll();

        final HardwareModel[] hardwareModels = ao.find(HardwareModel.class);
        assertEquals(5, hardwareModels.length);
        assertEquals(AdminHelperDatabaseUpdater.HARDWARE_NAME_1, hardwareModels[0].getName());
        assertEquals(AdminHelperDatabaseUpdater.TYPE_OF_DEVICE_1, hardwareModels[0].getTypeOfDevice().getTypeOfDeviceName());
        assertEquals(AdminHelperDatabaseUpdater.HARDWARE_VERSION_1, hardwareModels[0].getVersion());
        assertEquals(AdminHelperDatabaseUpdater.HARDWARE_PRICE_1, hardwareModels[0].getPrice());
        assertEquals(AdminHelperDatabaseUpdater.PRODUCER_1, hardwareModels[0].getProducer().getProducerName());
        assertEquals(AdminHelperDatabaseUpdater.OPERATING_SYSTEM_1, hardwareModels[0].getOperatingSystem().getOperatingSystemName());
        assertEquals(AdminHelperDatabaseUpdater.HARDWARE_ARTICLE_NUMBER_1, hardwareModels[0].getArticleNumber());

        assertEquals(name, hardwareModels[4].getName());
        assertNotNull(hardwareModels[4].getTypeOfDevice());
        assertEquals(type, hardwareModels[4].getTypeOfDevice().getTypeOfDeviceName());
        assertEquals(model, hardwareModels[4].getVersion());
        assertEquals(escapeHtml4(price), hardwareModels[4].getPrice());
        assertNotNull(hardwareModels[4].getProducer());
        assertEquals(producer, hardwareModels[4].getProducer().getProducerName());
        assertNotNull(hardwareModels[4].getOperatingSystem());
        assertEquals(operatingSystem, hardwareModels[4].getOperatingSystem().getOperatingSystemName());
        assertEquals(articleNumber, hardwareModels[4].getArticleNumber());

        assertNull(hardwareModelService.add("  ", (String) null, null, null, null, null, null));
        assertNull(hardwareModelService.add("  ", (TypeOfDevice) null, null, null, null, null, null));

        assertNull(hardwareModelService.add(null, (String) null, null, null, null, null, null));
        assertNull(hardwareModelService.add(null, (TypeOfDevice) null, null, null, null, null, null));

        assertNotNull(hardwareModelService.add(" a ", (String) null, null, null, null, null, null));
        ao.flushAll();
        assertNotNull(hardwareModelService.add(" b ", (TypeOfDevice) null, null, null, null, null, null));
        ao.flushAll();

        assertNull(hardwareModelService.add("a", (String) null, null, null, null, null, null));
        assertNull(hardwareModelService.add("a", (TypeOfDevice) null, null, null, null, null, null));

        assertNotNull(hardwareModelService.add(" a ", (String) null, " version 1 ", null, null, null, null));
        ao.flushAll();
        assertNotNull(hardwareModelService.add(" a ", (TypeOfDevice) null, "version 2", null, null, null, null));
        ao.flushAll();

        assertNull(hardwareModelService.add("a", (String) null, "  ", null, null, null, null));
        assertNull(hardwareModelService.add("a", (TypeOfDevice) null, "   ", null, null, null, null));
    }

    @Test
    public void testAll() throws Exception {
        assertEquals(4, hardwareModelService.all().size());

        final HardwareModel hardwareModel = ao.create(HardwareModel.class);
        hardwareModel.setName("Some hardware model");
        hardwareModel.save();

        ao.flushAll();

        final List<HardwareModel> all = hardwareModelService.all();
        assertEquals(5, all.size());
        assertEquals(hardwareModel.getID(), all.get(4).getID());
    }
}