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

import com.thoughtworks.selenium.SeleneseTestBase;
import org.junit.Assert;
import org.junit.Test;

public class ActiveDevicesTabTest extends SeleniumTestBase {

    @Test
    public void testActiveDevicesTab() throws Exception {
        selenium.waitForPageToLoad("30000");
        selenium.click("css=#aui-uid-2 > strong");
        verifyEquals("Active Devices", selenium.getText("css=#tabs-active-devices > div.left-from-search-field > h3"));
        verifyEquals("Nexus 4 (8 GB)", selenium.getText("css=#table-active-devices > tr > td.name"));
        verifyEquals("111", selenium.getText("css=#table-active-devices > tr > td.serial"));
        verifyEquals("111", selenium.getText("css=#table-active-devices > tr > td.imei"));
        verifyEquals("111", selenium.getText("css=#table-active-devices > tr > td.inventory"));
        verifyEquals("Lending out", selenium.getText("id=1,1"));
        verifyEquals("Sort out", selenium.getText("link=Sort out"));
        selenium.selectWindow("null");
        verifyEquals("Details", selenium.getText("xpath=(//a[contains(text(),'Details')])[3]"));
        verifyEquals("Nexus 4 (8 GB)", selenium.getText("//tbody[@id='table-active-devices']/tr[2]/td"));
        verifyEquals("333", selenium.getText("//tbody[@id='table-active-devices']/tr[2]/td[2]"));
        verifyEquals("", selenium.getText("//tbody[@id='table-active-devices']/tr[2]/td[3]"));
        verifyEquals("", selenium.getText("//tbody[@id='table-active-devices']/tr[2]/td[4]"));
        verifyEquals("Google Inc.", selenium.getText("//tbody[@id='table-active-devices']/tr[2]/td[5]"));
        verifyEquals("2014-09-23", selenium.getText("//tbody[@id='table-active-devices']/tr[2]/td[6]"));
        verifyEquals("stephanfellhofer", selenium.getText("//tbody[@id='table-active-devices']/tr[2]/td[7]"));
        verifyEquals("Return device", selenium.getText("link=Return device"));
        verifyEquals("Details", selenium.getText("xpath=(//a[contains(text(),'Details')])[4]"));
        verifyEquals("Nexus 4 (8 GB)", selenium.getText("//tbody[@id='table-active-devices']/tr[3]/td"));
        verifyEquals("789", selenium.getText("//tbody[@id='table-active-devices']/tr[3]/td[2]"));
        verifyEquals("890", selenium.getText("//tbody[@id='table-active-devices']/tr[3]/td[3]"));
        verifyEquals("901", selenium.getText("//tbody[@id='table-active-devices']/tr[3]/td[4]"));
        verifyEquals("Lending out", selenium.getText("id=1,7"));
        verifyEquals("Sort out", selenium.getText("id=7"));
        verifyEquals("Details", selenium.getText("xpath=(//a[contains(text(),'Details')])[5]"));
        verifyEquals("Nexus 7 (8 GB)", selenium.getText("//tbody[@id='table-active-devices']/tr[4]/td"));
        verifyEquals("444", selenium.getText("//tbody[@id='table-active-devices']/tr[4]/td[2]"));
        verifyEquals("", selenium.getText("//tbody[@id='table-active-devices']/tr[4]/td[3]"));
        verifyEquals("", selenium.getText("//tbody[@id='table-active-devices']/tr[4]/td[4]"));
        verifyEquals("Lending out", selenium.getText("id=2,4"));
        verifyEquals("Sort out", selenium.getText("id=4"));
        verifyEquals("Details", selenium.getText("xpath=(//a[contains(text(),'Details')])[6]"));
        verifyEquals("Nexus 7 (8 GB)", selenium.getText("//tbody[@id='table-active-devices']/tr[5]/td"));
        verifyEquals("666", selenium.getText("//tbody[@id='table-active-devices']/tr[5]/td[2]"));
        verifyEquals("", selenium.getText("//tbody[@id='table-active-devices']/tr[5]/td[3]"));
        verifyEquals("", selenium.getText("//tbody[@id='table-active-devices']/tr[5]/td[4]"));
        verifyEquals("", selenium.getText("//tbody[@id='table-active-devices']/tr[5]/td[5]"));
        verifyEquals("2014-09-23", selenium.getText("//tbody[@id='table-active-devices']/tr[5]/td[6]"));
        verifyEquals("admin", selenium.getText("//tbody[@id='table-active-devices']/tr[5]/td[7]"));
        verifyEquals("Return device", selenium.getText("xpath=(//a[contains(text(),'Return device')])[2]"));
        verifyEquals("Details", selenium.getText("xpath=(//a[contains(text(),'Details')])[7]"));
        selenium.click("id=1,1");
        verifyEquals("Lending Out - Nexus 4 (8 GB)", selenium.getText("css=#lend-out-dialog > div.dialog-components > h2.dialog-title"));
        verifyEquals("111", selenium.getValue("css=#serial"));
        verifyEquals("111", selenium.getValue("css=#imei"));
        verifyEquals("111", selenium.getValue("css=#inventory"));
        selenium.click("link=Cancel");
        selenium.click("link=Sort out");
        verifyEquals("Sort Out Device", selenium.getText("css=#sort-out-dialog > div.dialog-components > h2.dialog-title"));
        selenium.click("link=Cancel");
        selenium.click("xpath=(//a[contains(text(),'Details')])[3]");
        for (int second = 0; ; second++) {
            if (second >= 60) Assert.fail("timeout");
            try {
                if (selenium.isElementPresent("css=h2.dialog-title")) break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        verifyEquals("Device Details", selenium.getText("css=h2.dialog-title"));
        verifyEquals("Nexus 4", selenium.getText("css=div.dialog-panel-body:nth-child(1) > table:nth-child(1) > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(2)"));
        selenium.selectWindow("null");
        selenium.click("css=button.button-panel-button");
        selenium.click("link=Return device");
        verifyEquals("Returning Device", selenium.getText("css=#returning-device-dialog > div.dialog-components > h2.dialog-title"));
        selenium.click("link=Cancel");
        selenium.click("xpath=(//a[contains(text(),'Details')])[4]");
        verifyEquals("Device Details", selenium.getText("css=h2.dialog-title"));
        verifyEquals("Nexus 4", selenium.getText("//div[@id='device-details-dialog']/div/div/div/table/tbody/tr/td[2]"));
        selenium.click("css=button.button-panel-button");
        selenium.click("id=2,4");
        verifyEquals("Lending Out - Nexus 7 (8 GB)", selenium.getText("css=#lend-out-dialog > div.dialog-components > h2.dialog-title"));
        verifyEquals("444", selenium.getValue("css=#serial"));
        selenium.click("link=Cancel");
        selenium.click("id=4");
        verifyEquals("Sort Out Device", selenium.getText("css=#sort-out-dialog > div.dialog-components > h2.dialog-title"));
        selenium.click("link=Cancel");
        selenium.click("xpath=(//a[contains(text(),'Details')])[6]");
        verifyEquals("Device Details", selenium.getText("css=h2.dialog-title"));
        verifyEquals("Nexus 7", selenium.getText("//div[@id='device-details-dialog']/div/div/div/table/tbody/tr/td[2]"));
        selenium.click("css=button.button-panel-button");
        selenium.click("xpath=(//a[contains(text(),'Return device')])[2]");
        verifyEquals("Returning Device", selenium.getText("css=#returning-device-dialog > div.dialog-components > h2.dialog-title"));
        selenium.click("link=Cancel");
        selenium.click("xpath=(//a[contains(text(),'Details')])[7]");
        verifyEquals("Device Details", selenium.getText("css=h2.dialog-title"));
        verifyEquals("Nexus 7", selenium.getText("//div[@id='device-details-dialog']/div/div/div/table/tbody/tr/td[2]"));
        selenium.click("css=button.button-panel-button");
    }

}
