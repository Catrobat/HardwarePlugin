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

public class ProducerServiceImpl implements ProducerService {

    private final ActiveObjects ao;

    public ProducerServiceImpl(ActiveObjects ao) {
        this.ao = checkNotNull(ao);
    }

    @Override
    public Producer getOrCreateProducer(String producerName) {
        if (producerName == null || producerName.trim().length() == 0) {
            return null;
        }

        producerName = escapeHtml4(producerName.trim());

        Producer[] producers = ao.find(Producer.class, Query.select()
                .where("upper(\"PRODUCER_NAME\") = upper(?)", producerName));
        if (producers.length == 1) {
            return producers[0];
        } else if (producers.length > 1) {
            throw new RuntimeException("Should be unique - this should never happen!");
        }

        final Producer createdProducer = ao.create(Producer.class);
        createdProducer.setProducerName(producerName);
        createdProducer.save();
        return createdProducer;
    }

    @Override
    public List<Producer> searchProducers(String query) {
        return Arrays.asList(ao.find(Producer.class, Query.select().where("upper(\"PRODUCER_NAME\") LIKE upper(?)", "%" + query + "%")));
    }

    @Override
    public void cleanUnused() {
        List<Producer> producerList = Arrays.asList(ao.find(Producer.class));
        for (Producer producer : producerList) {
            if (producer.getHardwareModels().length == 0) {
                ao.delete(producer);
            }
        }
    }
}

