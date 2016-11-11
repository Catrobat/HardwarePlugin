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

public class SortedOutTabTest extends SeleniumTestBase {

    @Test
    public void testSortedOutTab() throws Exception {
        selenium.waitForPageToLoad("30000");
        selenium.click("css=#aui-uid-3 > strong");
        verifyEquals("Nexus 4 (8 GB)", selenium.getText("css=#table-sorted-out > tr > td.name"));
        verifyEquals("222", selenium.getText("css=#table-sorted-out > tr > td.serial"));
        verifyEquals("222", selenium.getText("css=#table-sorted-out > tr > td.imei"));
        verifyEquals("222", selenium.getText("css=#table-sorted-out > tr > td.inventory"));
        verifyEquals("2014-09-23", selenium.getText("css=td.date"));
        selenium.click("xpath=(//a[contains(text(),'Details')])[8]");
        selenium.click("css=button.item-button");
        verifyEquals("Device Details", selenium.getText("css=h2.dialog-title"));
        verifyEquals("Nexus 4", selenium.getText("//div[@id='device-details-dialog']/div/div/div/table/tbody/tr/td[2]"));
        selenium.click("css=button.button-panel-button");
        selenium.click("id=5");
        selenium.click("css=button.item-button");
        verifyEquals("Device Details", selenium.getText("css=h2.dialog-title"));
        verifyEquals("Nexus 7", selenium.getText("//div[@id='device-details-dialog']/div/div/div/table/tbody/tr/td[2]"));
        selenium.click("css=button.button-panel-button");
    }

}
