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

package org.catrobat.jira.adminhelper.activeobject;

import com.atlassian.activeobjects.external.ActiveObjects;
import net.java.ao.Query;

import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

public class HardwareModelServiceImpl implements HardwareModelService {

    private final ActiveObjects ao;
    private final TypeOfDeviceService typeOfDeviceService;
    private final ProducerService producerService;
    private final OperatingSystemService operatingSystemService;

    public HardwareModelServiceImpl(ActiveObjects ao, TypeOfDeviceService typeOfDeviceService, ProducerService producerService, OperatingSystemService operatingSystemService) {
        this.ao = checkNotNull(ao);
        this.typeOfDeviceService = checkNotNull(typeOfDeviceService);
        this.producerService = checkNotNull(producerService);
        this.operatingSystemService = checkNotNull(operatingSystemService);
    }

    @Override
    public HardwareModel add(String name, String typeOfDeviceName, String version, String price, String producerName, String operationSystemName, String articleNumber) {
        TypeOfDevice typeOfDevice = typeOfDeviceService.getOrCreateTypeOfDevice(typeOfDeviceName);
        Producer producer = producerService.getOrCreateProducer(producerName);
        OperatingSystem operatingSystem = operatingSystemService.getOrCreateOperatingSystem(operationSystemName);

        return add(name, typeOfDevice, version, price, producer, operatingSystem, articleNumber);
    }

    @Override
    public HardwareModel add(String name, TypeOfDevice typeOfDevice, String version, String price, Producer producer,
                             OperatingSystem operationSystem, String articleNumber) {
        if (name == null || name.trim().length() == 0) {
            return null;
        }
        name = escapeHtml4(name.trim());

        if (version != null && version.trim().length() != 0) {
            version = escapeHtml4(version.trim());
        } else {
            version = null;
        }

        if (price != null && price.trim().length() != 0) {
            price = escapeHtml4(price.trim());
        } else {
            price = null;
        }

        if (articleNumber != null && articleNumber.trim().length() != 0) {
            articleNumber = escapeHtml4(articleNumber.trim());
        } else {
            articleNumber = null;
        }

        HardwareModel[] hardwareModels = ao.find(HardwareModel.class, Query.select().where("upper(\"NAME\") = upper(?)", name));
        for (HardwareModel hardwareModel : hardwareModels) {
            if ((hardwareModel.getVersion() != null && version != null &&
                    hardwareModel.getVersion().toUpperCase().equals(version.toUpperCase())) ||
                    (hardwareModel.getVersion() == null && version == null)) {
                typeOfDeviceService.cleanUnused();
                producerService.cleanUnused();
                operatingSystemService.cleanUnused();

                return null;
            }
        }

        final HardwareModel hardwareModel = ao.create(HardwareModel.class);
        hardwareModel.setName(name);
        hardwareModel.setTypeOfDevice(typeOfDevice);
        hardwareModel.setVersion(version);
        hardwareModel.setPrice(price);
        hardwareModel.setProducer(producer);
        hardwareModel.setOperatingSystem(operationSystem);
        hardwareModel.setArticleNumber(articleNumber);
        hardwareModel.save();
        return hardwareModel;
    }

    @Override
    public HardwareModel get(int id) {
        HardwareModel[] hardwareModels = ao.find(HardwareModel.class, Query.select().where("\"ID\" = ?", id));
        if (hardwareModels.length == 1)
            return hardwareModels[0];
        else
            return null;
    }

    @Override
    public HardwareModel edit(int id, String name, String typeOfDeviceName, String version, String price,
                              String producer, String operationSystem, String articleNumber) {
        HardwareModel hardwareModel = get(id);
        if (hardwareModel == null)
            return null;

        if (name == null || name.trim().length() == 0) {
            return null;
        }
        name = escapeHtml4(name.trim());

        if (version != null && version.trim().length() != 0) {
            version = escapeHtml4(version.trim());
        } else {
            version = null;
        }

        if (price != null && price.trim().length() != 0) {
            price = escapeHtml4(price.trim());
        } else {
            price = null;
        }

        if (articleNumber != null && articleNumber.trim().length() != 0) {
            articleNumber = escapeHtml4(articleNumber.trim());
        } else {
            articleNumber = null;
        }

        HardwareModel[] hardwareModels = ao.find(HardwareModel.class, Query.select().where("upper(\"NAME\") = upper(?)", name));
        for (HardwareModel hardwareModelToCheck : hardwareModels) {
            if (hardwareModelToCheck.getVersion() == null && version == null) {
                return null;
            } else if (hardwareModelToCheck.getVersion() != null && version != null && hardwareModelToCheck.getVersion().toUpperCase().equals(version.toUpperCase())) {
                return null;
            }
        }

        hardwareModel.setName(name);
        hardwareModel.setVersion(version);
        hardwareModel.setPrice(price);
        hardwareModel.setArticleNumber(articleNumber);

        hardwareModel.setTypeOfDevice(typeOfDeviceService.getOrCreateTypeOfDevice(typeOfDeviceName));
        hardwareModel.setProducer(producerService.getOrCreateProducer(producer));
        hardwareModel.setOperatingSystem(operatingSystemService.getOrCreateOperatingSystem(operationSystem));

        hardwareModel.save();

        typeOfDeviceService.cleanUnused();
        producerService.cleanUnused();
        operatingSystemService.cleanUnused();

        return hardwareModel;
    }

    @Override
    public List<HardwareModel> all() {
        return Arrays.asList(ao.find(HardwareModel.class, Query.select().order("upper(\"NAME\"), upper(\"VERSION\")")));
    }

    @Override
    public void moveDevices(HardwareModel from, HardwareModel to) {
        if (from == null || to == null) {
            return;
        }

        for (Device device : from.getDevices()) {
            device.setHardwareModel(to);
            device.save();
        }
    }

    @Override
    public void delete(HardwareModel toDelete) {
        ao.delete(toDelete);
    }
}
