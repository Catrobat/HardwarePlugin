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

import com.atlassian.activeobjects.tx.Transactional;

import java.util.List;

@Transactional
public interface HardwareModelService {
    HardwareModel add(String name, String typeOfDeviceName, String version, String price, String producer, String operationSystem, String articleNumber);

    HardwareModel add(String name, TypeOfDevice typeOfDevice, String version, String price, Producer producer, OperatingSystem operationSystem, String articleNumber);

    HardwareModel get(int id);

    HardwareModel edit(int id, String name, String typeOfDeviceName, String version, String price, String producer, String operationSystem, String articleNumber);

    List<HardwareModel> all();

    void moveDevices(HardwareModel from, HardwareModel to);

    void delete(HardwareModel toDelete);
}
