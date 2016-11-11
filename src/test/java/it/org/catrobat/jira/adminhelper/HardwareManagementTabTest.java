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

package it.org.catrobat.jira.adminhelper;

import org.junit.Test;

public class HardwareManagementTabTest extends SeleniumTestBase {

    @Test
    public void testHardwareManagementTab() throws Exception {
        selenium.waitForPageToLoad("30000");
        selenium.click("css=#aui-uid-5 > strong");
        verifyEquals("Hardware Management", selenium.getText("css=#tabs-hardware-management > div.left-from-search-field > h3"));
        verifyEquals("Nexus 4", selenium.getText("css=#table-management > tr > td.name"));
        verifyEquals("8 GB", selenium.getText("css=#table-management > tr > td.version"));
        verifyEquals("Smartphone", selenium.getText("css=#table-management > tr > td.type"));
        verifyEquals("Android", selenium.getText("css=#table-management > tr > td.os"));
        verifyEquals("3", selenium.getText("//tbody[@id='table-management']/tr/td[5]"));
        verifyEquals("Nexus 7", selenium.getText("//tbody[@id='table-management']/tr[2]/td"));
        verifyEquals("8 GB", selenium.getText("//tbody[@id='table-management']/tr[2]/td[2]"));
        verifyEquals("Smartphone", selenium.getText("//tbody[@id='table-management']/tr[2]/td[3]"));
        verifyEquals("Android", selenium.getText("//tbody[@id='table-management']/tr[2]/td[4]"));
        verifyEquals("2", selenium.getText("//tbody[@id='table-management']/tr[2]/td[5]"));
        selenium.click("id=new_model");
        verifyEquals("Create/Edit Hardware Model", selenium.getText("css=h2.dialog-title"));
        selenium.click("link=Cancel");
        selenium.click("id=new_device");
        verifyEquals("Create New Device", selenium.getText("css=h2.dialog-title"));
        selenium.click("link=Cancel");
        selenium.click("link=Edit");
        verifyEquals("Create/Edit Hardware Model", selenium.getText("css=h2.dialog-title"));
        verifyEquals("Nexus 4", selenium.getValue("id=name"));
        selenium.click("link=Cancel");
        selenium.click("link=Remove");
        verifyEquals("Remove Hardware Model", selenium.getText("css=h2.dialog-title"));
        selenium.click("link=Cancel");
        selenium.click("xpath=(//a[contains(text(),'Edit')])[2]");
        verifyEquals("Create/Edit Hardware Model", selenium.getText("css=h2.dialog-title"));
        verifyEquals("Nexus 7", selenium.getValue("id=name"));
        selenium.click("link=Cancel");
        selenium.click("xpath=(//a[contains(text(),'Remove')])[2]");
        verifyEquals("Remove Hardware Model", selenium.getText("css=h2.dialog-title"));
        selenium.click("link=Cancel");
    }

}
