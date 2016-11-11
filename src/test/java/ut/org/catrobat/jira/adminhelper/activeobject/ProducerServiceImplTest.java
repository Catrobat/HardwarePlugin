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
import org.catrobat.jira.adminhelper.activeobject.Producer;
import org.catrobat.jira.adminhelper.activeobject.ProducerService;
import org.catrobat.jira.adminhelper.activeobject.ProducerServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;
import static org.junit.Assert.*;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(AdminHelperDatabaseUpdater.class)
public class ProducerServiceImplTest {

    @SuppressWarnings("UnusedDeclaration")
    private EntityManager entityManager;
    private ActiveObjects ao;
    private ProducerService producerService;

    @Before
    public void setUp() throws Exception {
        assertNotNull(entityManager);
        ao = new TestActiveObjects(entityManager);
        producerService = new ProducerServiceImpl(ao);
    }

    @Test
    public void testGetOrCreateProducer() {
        String xss = " <script>alert('xss');</script>   ";
        assertEquals(4, ao.find(Producer.class).length);

        Producer producer = producerService.getOrCreateProducer(AdminHelperDatabaseUpdater.PRODUCER_1);
        assertNotNull(producer);
        assertFalse(producer.getID() == 0);
        assertEquals(AdminHelperDatabaseUpdater.PRODUCER_1, producer.getProducerName());
        ao.flushAll();
        assertEquals(4, ao.find(Producer.class).length);

        producer = producerService.getOrCreateProducer("   " + AdminHelperDatabaseUpdater.PRODUCER_1 + " ");
        assertNotNull(producer);
        assertFalse(producer.getID() == 0);
        assertEquals(AdminHelperDatabaseUpdater.PRODUCER_1, producer.getProducerName());
        ao.flushAll();
        assertEquals(4, ao.find(Producer.class).length);

        producer = producerService.getOrCreateProducer(xss);
        assertNotNull(producer);
        assertFalse(producer.getID() == 0);
        assertEquals(escapeHtml4(xss.trim()), producer.getProducerName());
        ao.flushAll();
        assertEquals(5, ao.find(Producer.class).length);

        assertNull(producerService.getOrCreateProducer(null));
        assertNull(producerService.getOrCreateProducer(""));
        assertNull(producerService.getOrCreateProducer("    "));
    }

    @Test
    public void testSearchProducers() {
        assertEquals(0, producerService.searchProducers("blub").size());
        assertEquals(3, producerService.searchProducers("a").size());
        assertEquals(2, producerService.searchProducers("l").size());
        assertEquals(1, producerService.searchProducers("g").size());

        assertEquals(0, producerService.searchProducers(null).size());
        assertEquals(4, producerService.searchProducers("").size());
        assertEquals(1, producerService.searchProducers(" ").size());
    }

    @Test
    public void testCleanUnused() {
        assertEquals(4, ao.find(Producer.class).length);

        producerService.cleanUnused();
        ao.flushAll();
        assertEquals(3, ao.find(Producer.class).length);

        HardwareModel hardwareModel = ao.find(HardwareModel.class)[2];
        hardwareModel.setProducer(null);
        hardwareModel.save();
        ao.flushAll();

        producerService.cleanUnused();
        ao.flushAll();

        assertEquals(2, ao.find(Producer.class).length);
    }
}
