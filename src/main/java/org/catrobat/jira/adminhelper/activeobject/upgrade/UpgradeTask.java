/*
 * Copyright 2015 Stephan Fellhofer
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

package org.catrobat.jira.adminhelper.activeobject.upgrade;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask;
import com.atlassian.activeobjects.external.ModelVersion;
import org.catrobat.jira.adminhelper.activeobject.*;

public class UpgradeTask implements ActiveObjectsUpgradeTask {

    /* https://developer.atlassian.com/docs/atlassian-platform-common-components/active-objects/developing-your-plugin-with-active-objects/upgrading-your-plugin-and-handling-data-model-updates#Upgradingyourpluginandhandlingdatamodelupdates-Moduledefinition
    Model Version 1: added Table Resource and edited AdminHelperConfig - actually nothing to do
    Model Version 2: edited AdminHelperConfig to allow E-Mail configuration - actually nothing to do
     */

    @Override
    public ModelVersion getModelVersion() {
        return ModelVersion.valueOf("5");
    }

    @Override
    public void upgrade(ModelVersion modelVersion, ActiveObjects activeObjects) {
        /*
        WARNING
        If you are using a version of Active Objects earlier than 0.22.1, data belonging to any entities not listed in
        the migrate method call will be permanently deleted. If you are using Active Objects 0.22.1 or later versions,
        the data will not be deleted.
         */


        // adding Resource to Active Objects
        if (modelVersion.isOlderThan(ModelVersion.valueOf("5"))) {
            activeObjects.migrate(AdminHelperConfig.class, ApprovedGroup.class, ApprovedUser.class, Device.class,
                    DeviceComment.class, GithubTeam.class, Group.class, HardwareModel.class, Lending.class,
                    OperatingSystem.class, Producer.class, Resource.class, Team.class, TeamToGithubTeam.class,
                    TeamToGroup.class, TypeOfDevice.class, ReadOnlyHdwUser.class, ReadOnlyHdwGroup.class);
        }
    }
}
