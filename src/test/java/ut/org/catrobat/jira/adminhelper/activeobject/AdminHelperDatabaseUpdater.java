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

import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.DatabaseUpdater;
import org.catrobat.jira.adminhelper.activeobject.*;

import java.util.Date;

public class AdminHelperDatabaseUpdater implements DatabaseUpdater {
    public static final String HARDWARE_NAME_1 = "Nexus 4";
    public static final String HARDWARE_VERSION_1 = "16 GB";
    public static final String HARDWARE_PRICE_1 = "250 €";
    public static final String HARDWARE_ARTICLE_NUMBER_1 = "8808992070306";
    public static final String HARDWARE_NAME_2 = "Nexus 4";
    public static final String HARDWARE_VERSION_2 = "8 GB";
    public static final String HARDWARE_PRICE_2 = "200 €";
    public static final String HARDWARE_ARTICLE_NUMBER_2 = "8808992070429";
    public static final String HARDWARE_NAME_3 = "iPhone 4";
    public static final String HARDWARE_VERSION_3 = "16 GB";
    public static final String HARDWARE_PRICE_3 = "600 €";
    public static final String HARDWARE_ARTICLE_NUMBER_3 = "B003U6628A";
    public static final String HARDWARE_NAME_4 = "iPhone 5s";
    public static final String HARDWARE_VERSION_4 = "64 GB";
    public static final String HARDWARE_PRICE_4 = "1000 €";
    public static final String HARDWARE_ARTICLE_NUMBER_4 = "B00F8JHGXM";

    public static final String TYPE_OF_DEVICE_1 = "Smartphone";
    public static final String TYPE_OF_DEVICE_2 = "Tablet";
    public static final String TYPE_OF_DEVICE_3 = "Ardu Ino";

    public static final String PRODUCER_1 = "LG";
    public static final String PRODUCER_2 = "Apple";
    public static final String PRODUCER_3 = "Asus";
    public static final String PRODUCER_4 = "Nokia Nokia";

    public static final String OPERATING_SYSTEM_1 = "Android";
    public static final String OPERATING_SYSTEM_2 = "iOS";
    public static final String OPERATING_SYSTEM_3 = "Windows Phone";

    public static final String DEVICE_SERIAL_1 = "serial 1111";
    public static final String DEVICE_IMEI_1 = "imei 1111";
    public static final String DEVICE_INVENTORY_1 = "inventory 1111";
    public static final Date DEVICE_RECEIVED_DATE_1 = new Date(1222812000000L);
    public static final String DEVICE_RECEIVED_FROM_1 = "received 1111";
    public static final String DEVICE_USEFUL_LIFE_1 = "useful 1111";
    public static final String DEVICE_SERIAL_2 = "serial 2222";
    public static final String DEVICE_IMEI_2 = "imei 2222";
    public static final String DEVICE_INVENTORY_2 = "inventory 2222";
    public static final Date DEVICE_RECEIVED_DATE_2 = new Date(1222812000001L);
    public static final String DEVICE_RECEIVED_FROM_2 = "received 2222";
    public static final String DEVICE_USEFUL_LIFE_2 = "useful 2222";

    @SuppressWarnings("unchecked")
    @Override
    public void update(EntityManager em) throws Exception {
        em.migrate(AdminHelperConfig.class);
        em.migrate(ApprovedGroup.class);
        em.migrate(ApprovedUser.class);
        em.migrate(Device.class);
        em.migrate(DeviceComment.class);
        em.migrate(GithubTeam.class);
        em.migrate(Group.class);
        em.migrate(HardwareModel.class);
        em.migrate(Lending.class);
        em.migrate(OperatingSystem.class);
        em.migrate(Producer.class);
        em.migrate(Resource.class);
        em.migrate(Team.class);
        em.migrate(TeamToGithubTeam.class);
        em.migrate(TeamToGroup.class);
        em.migrate(TypeOfDevice.class);


        final TypeOfDevice typeOfDevice1 = em.create(TypeOfDevice.class);
        typeOfDevice1.setTypeOfDeviceName(TYPE_OF_DEVICE_1);
        typeOfDevice1.save();

        final TypeOfDevice typeOfDevice2 = em.create(TypeOfDevice.class);
        typeOfDevice2.setTypeOfDeviceName(TYPE_OF_DEVICE_2);
        typeOfDevice2.save();

        final TypeOfDevice typeOfDevice3 = em.create(TypeOfDevice.class);
        typeOfDevice3.setTypeOfDeviceName(TYPE_OF_DEVICE_3);
        typeOfDevice3.save();

        final Producer producer1 = em.create(Producer.class);
        producer1.setProducerName(PRODUCER_1);
        producer1.save();

        final Producer producer2 = em.create(Producer.class);
        producer2.setProducerName(PRODUCER_2);
        producer2.save();

        final Producer producer3 = em.create(Producer.class);
        producer3.setProducerName(PRODUCER_3);
        producer3.save();

        final Producer producer4 = em.create(Producer.class);
        producer4.setProducerName(PRODUCER_4);
        producer4.save();

        final OperatingSystem operatingSystem1 = em.create(OperatingSystem.class);
        operatingSystem1.setOperatingSystemName(OPERATING_SYSTEM_1);
        operatingSystem1.save();

        final OperatingSystem operatingSystem2 = em.create(OperatingSystem.class);
        operatingSystem2.setOperatingSystemName(OPERATING_SYSTEM_2);
        operatingSystem2.save();

        final OperatingSystem operatingSystem3 = em.create(OperatingSystem.class);
        operatingSystem3.setOperatingSystemName(OPERATING_SYSTEM_3);
        operatingSystem3.save();

        final HardwareModel hardwareModel1 = em.create(HardwareModel.class);
        hardwareModel1.setName(HARDWARE_NAME_1);
        hardwareModel1.setTypeOfDevice(typeOfDevice1);
        hardwareModel1.setVersion(HARDWARE_VERSION_1);
        hardwareModel1.setPrice(HARDWARE_PRICE_1);
        hardwareModel1.setProducer(producer1);
        hardwareModel1.setOperatingSystem(operatingSystem1);
        hardwareModel1.setArticleNumber(HARDWARE_ARTICLE_NUMBER_1);
        hardwareModel1.save();

        final HardwareModel hardwareModel2 = em.create(HardwareModel.class);
        hardwareModel2.setName(HARDWARE_NAME_2);
        hardwareModel2.setTypeOfDevice(typeOfDevice2);
        hardwareModel2.setVersion(HARDWARE_VERSION_2);
        hardwareModel2.setPrice(HARDWARE_PRICE_2);
        hardwareModel2.setProducer(producer1);
        hardwareModel2.setOperatingSystem(operatingSystem2);
        hardwareModel2.setArticleNumber(HARDWARE_ARTICLE_NUMBER_2);
        hardwareModel2.save();

        final HardwareModel hardwareModel3 = em.create(HardwareModel.class);
        hardwareModel3.setName(HARDWARE_NAME_3);
        hardwareModel3.setTypeOfDevice(typeOfDevice3);
        hardwareModel3.setVersion(HARDWARE_VERSION_3);
        hardwareModel3.setPrice(HARDWARE_PRICE_3);
        hardwareModel3.setProducer(producer2);
        hardwareModel3.setOperatingSystem(operatingSystem3);
        hardwareModel3.setArticleNumber(HARDWARE_ARTICLE_NUMBER_3);
        hardwareModel3.save();

        final HardwareModel hardwareModel4 = em.create(HardwareModel.class);
        hardwareModel4.setName(HARDWARE_NAME_4);
        hardwareModel4.setTypeOfDevice(typeOfDevice1);
        hardwareModel4.setVersion(HARDWARE_VERSION_4);
        hardwareModel4.setPrice(HARDWARE_PRICE_4);
        hardwareModel4.setProducer(producer3);
        hardwareModel4.setOperatingSystem(operatingSystem2);
        hardwareModel4.setArticleNumber(HARDWARE_ARTICLE_NUMBER_4);
        hardwareModel4.save();


        final Device device1 = em.create(Device.class);
        device1.setHardwareModel(hardwareModel1);
        device1.setSerialNumber(DEVICE_SERIAL_1);
        device1.setUsefulLifeOfAsset(DEVICE_USEFUL_LIFE_1);
        device1.setInventoryNumber(DEVICE_INVENTORY_1);
        device1.setImei(DEVICE_IMEI_1);
        device1.setReceivedDate(DEVICE_RECEIVED_DATE_1);
        device1.setReceivedFrom(DEVICE_RECEIVED_FROM_1);
        device1.save();

        final Device device2 = em.create(Device.class);
        device2.setHardwareModel(hardwareModel1);
        device2.setSerialNumber(DEVICE_SERIAL_2);
        device2.setUsefulLifeOfAsset(DEVICE_USEFUL_LIFE_2);
        device2.setInventoryNumber(DEVICE_INVENTORY_2);
        device2.setImei(DEVICE_IMEI_2);
        device2.setReceivedDate(DEVICE_RECEIVED_DATE_2);
        device2.setReceivedFrom(DEVICE_RECEIVED_FROM_2);
        device2.save();

//        {
//            final HardwareModel hardwareModel3 = em.create(HardwareModel.class);
//            hardwareModel3.setName(HARDWARE_NAME_3);
//            hardwareModel3.setTypeOfDevice(typeOfDevice1);
//            hardwareModel3.setVersion(HARDWARE_VERSION_3);
//            hardwareModel3.setPrice(HARDWARE_PRICE_3);
//            hardwareModel3.setProducer(producer2);
//            hardwareModel3.setOperatingSystem(operatingSystem2);
//            hardwareModel3.setArticleNumber(HARDWARE_ARTICLE_NUMBER_3);
//            hardwareModel3.save();
//        }
    }
}
