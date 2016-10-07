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

public class OperatingSystemServiceImpl implements OperatingSystemService {

    private final ActiveObjects ao;

    public OperatingSystemServiceImpl(ActiveObjects ao) {
        this.ao = checkNotNull(ao);
    }

    @Override
    public OperatingSystem getOrCreateOperatingSystem(String operatingSystemName) {
        if (operatingSystemName == null || operatingSystemName.trim().length() == 0) {
            return null;
        }

        operatingSystemName = escapeHtml4(operatingSystemName.trim());

        OperatingSystem[] operatingSystems = ao.find(OperatingSystem.class, Query.select()
                .where("upper(\"OPERATING_SYSTEM_NAME\") = upper(?)", operatingSystemName));
        if (operatingSystems.length == 1) {
            return operatingSystems[0];
        } else if (operatingSystems.length > 1) {
            throw new RuntimeException("Should be unique - this should never happen!");
        }

        final OperatingSystem createdOperatingSystem = ao.create(OperatingSystem.class);
        createdOperatingSystem.setOperatingSystemName(operatingSystemName);
        createdOperatingSystem.save();
        return createdOperatingSystem;
    }

    @Override
    public List<OperatingSystem> searchOperatingSystems(String query) {
        return Arrays.asList(ao.find(OperatingSystem.class, Query.select()
                .where("upper(\"OPERATING_SYSTEM_NAME\") LIKE upper(?)", "%" + query + "%")));
    }

    @Override
    public void cleanUnused() {
        List<OperatingSystem> operatingSystemList = Arrays.asList(ao.find(OperatingSystem.class));
        for (OperatingSystem operatingSystem : operatingSystemList) {
            if (operatingSystem.getHardwareModels().length == 0) {
                ao.delete(operatingSystem);
            }
        }
    }

}
