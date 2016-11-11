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

package it.org.catrobat.jira.adminhelper;

import com.atlassian.jira.pageobjects.JiraTestedProduct;
import com.atlassian.jira.pageobjects.config.RestoreJiraData;
import com.atlassian.jira.pageobjects.config.RestoreJiraDataFromBackdoor;

public class DataRestorer {

    private RestoreJiraData restoreData;

    public DataRestorer(JiraTestedProduct jiraTestedProduct) {
        restoreData = new RestoreJiraDataFromBackdoor(jiraTestedProduct);
    }

    public void doRestore() {
        doRestore("selenium.zip");
    }

    public void doRestore(String resourcePath) {
        restoreData.restore(resourcePath);
    }
}
