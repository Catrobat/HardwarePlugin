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

import static com.thoughtworks.selenium.SeleneseTestBase.fail;

public class AllDevicesTabTest extends SeleniumTestBase {

    @Test
    public void testAllDevicesTab() throws Exception {
        selenium.waitForPageToLoad("30000");
        selenium.click("id=aui-uid-4");
        verifyEquals("All Devices", selenium.getText("css=#tabs-all-devices > div.left-from-search-field > h3"));
        verifyEquals("Nexus 4 (8 GB)", selenium.getText("css=#table-all-devices > tr > td.name"));
        verifyEquals("111", selenium.getText("css=#table-all-devices > tr > td.serial"));
        verifyEquals("111", selenium.getText("css=#table-all-devices > tr > td.imei"));
        verifyEquals("111", selenium.getText("css=#table-all-devices > tr > td.inventory"));
        verifyEquals("Lending out", selenium.getText("xpath=(//a[contains(text(),'Lending out')])[6]"));
        verifyEquals("Sort out", selenium.getText("xpath=(//a[contains(text(),'Sort out')])[4]"));
        verifyEquals("Nexus 4 (8 GB)", selenium.getText("//tbody[@id='table-all-devices']/tr[2]/td"));
        verifyEquals("222", selenium.getText("//tbody[@id='table-all-devices']/tr[2]/td[2]"));
        verifyEquals("222", selenium.getText("//tbody[@id='table-all-devices']/tr[2]/td[3]"));
        verifyEquals("222", selenium.getText("//tbody[@id='table-all-devices']/tr[2]/td[4]"));
        verifyEquals("", selenium.getText("//tbody[@id='table-all-devices']/tr[2]/td[7]"));
        verifyEquals("2014-09-23", selenium.getText("//tbody[@id='table-all-devices']/tr[2]/td[8]"));
        verifyEquals("Nexus 4 (8 GB)", selenium.getText("//tbody[@id='table-all-devices']/tr[3]/td"));
        verifyEquals("333", selenium.getText("//tbody[@id='table-all-devices']/tr[3]/td[2]"));
        verifyEquals("", selenium.getText("//tbody[@id='table-all-devices']/tr[3]/td[3]"));
        verifyEquals("", selenium.getText("//tbody[@id='table-all-devices']/tr[3]/td[4]"));
        verifyEquals("Google Inc.", selenium.getText("//tbody[@id='table-all-devices']/tr[3]/td[5]"));
        verifyEquals("2014-09-23", selenium.getText("//tbody[@id='table-all-devices']/tr[3]/td[6]"));
        verifyEquals("stephanfellhofer", selenium.getText("//tbody[@id='table-all-devices']/tr[3]/td[7]"));
        verifyEquals("Return device", selenium.getText("xpath=(//a[contains(text(),'Return device')])[3]"));
        verifyEquals("Nexus 4 (8 GB)", selenium.getText("//tbody[@id='table-all-devices']/tr[4]/td"));
        verifyEquals("789", selenium.getText("//tbody[@id='table-all-devices']/tr[4]/td[2]"));
        verifyEquals("890", selenium.getText("//tbody[@id='table-all-devices']/tr[4]/td[3]"));
        verifyEquals("901", selenium.getText("//tbody[@id='table-all-devices']/tr[4]/td[4]"));
        verifyEquals("Lending out", selenium.getText("xpath=(//a[contains(text(),'Lending out')])[7]"));
        verifyEquals("Sort out", selenium.getText("xpath=(//a[contains(text(),'Sort out')])[5]"));
        verifyEquals("Nexus 7 (8 GB)", selenium.getText("//tbody[@id='table-all-devices']/tr[5]/td"));
        verifyEquals("444", selenium.getText("//tbody[@id='table-all-devices']/tr[5]/td[2]"));
        verifyEquals("", selenium.getText("//tbody[@id='table-all-devices']/tr[5]/td[3]"));
        verifyEquals("", selenium.getText("//tbody[@id='table-all-devices']/tr[5]/td[4]"));
        verifyEquals("Lending out", selenium.getText("xpath=(//a[contains(text(),'Lending out')])[8]"));
        verifyEquals("Sort out", selenium.getText("xpath=(//a[contains(text(),'Sort out')])[6]"));
        verifyEquals("Nexus 7 (8 GB)", selenium.getText("//tbody[@id='table-all-devices']/tr[6]/td"));
        verifyEquals("555", selenium.getText("//tbody[@id='table-all-devices']/tr[6]/td[2]"));
        verifyEquals("", selenium.getText("//tbody[@id='table-all-devices']/tr[6]/td[3]"));
        verifyEquals("", selenium.getText("//tbody[@id='table-all-devices']/tr[6]/td[4]"));
        verifyEquals("", selenium.getText("//tbody[@id='table-all-devices']/tr[6]/td[7]"));
        verifyEquals("2014-09-23", selenium.getText("//tbody[@id='table-all-devices']/tr[6]/td[8]"));
        verifyEquals("Nexus 7 (8 GB)", selenium.getText("//tbody[@id='table-all-devices']/tr[7]/td"));
        verifyEquals("666", selenium.getText("//tbody[@id='table-all-devices']/tr[7]/td[2]"));
        verifyEquals("", selenium.getText("//tbody[@id='table-all-devices']/tr[7]/td[3]"));
        verifyEquals("", selenium.getText("//tbody[@id='table-all-devices']/tr[7]/td[4]"));
        verifyEquals("", selenium.getText("//tbody[@id='table-all-devices']/tr[7]/td[5]"));
        verifyEquals("2014-09-23", selenium.getText("//tbody[@id='table-all-devices']/tr[7]/td[6]"));
        verifyEquals("admin", selenium.getText("//tbody[@id='table-all-devices']/tr[7]/td[7]"));
        verifyEquals("Return device", selenium.getText("xpath=(//a[contains(text(),'Return device')])[4]"));
        selenium.click("xpath=(//a[contains(text(),'Lending out')])[6]");
        for (int second = 0; ; second++) {
            if (second >= 60) fail("timeout");
            try {
                if (selenium.isElementPresent("css=#lend-out-dialog")) break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        verifyEquals("Lending Out - Nexus 4 (8 GB)", selenium.getText("css=#lend-out-dialog > div.dialog-components > h2.dialog-title"));
        verifyEquals("111", selenium.getValue("css=#serial"));
        verifyEquals("111", selenium.getValue("css=#imei"));
        verifyEquals("111", selenium.getValue("css=#inventory"));
        selenium.click("link=Cancel");
        for (int second = 0; ; second++) {
            if (second >= 60) fail("timeout");
            try {
                if (!selenium.isElementPresent("css=#lend-out-dialog")) break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }
        selenium.click("xpath=(//a[contains(text(),'Sort out')])[4]");
        verifyEquals("Sort Out Device", selenium.getText("css=#sort-out-dialog > div.dialog-components > h2.dialog-title"));
        selenium.click("link=Cancel");
        selenium.click("xpath=(//a[contains(text(),'Details')])[10]");
        for (int second = 0; ; second++) {
            if (second >= 60) fail("timeout");
            try {
                if (selenium.isElementPresent("css=#device-details-dialog")) break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        verifyEquals("Device Details", selenium.getText("css=h2.dialog-title"));
        verifyEquals("Nexus 4", selenium.getText("//div[@id='device-details-dialog']/div/div/div/table/tbody/tr/td[2]"));
        selenium.click("css=button.button-panel-button");
        selenium.click("xpath=(//a[contains(text(),'Return device')])[3]");
        for (int second = 0; ; second++) {
            if (second >= 60) fail("timeout");
            try {
                if (selenium.isElementPresent("css=#returning-device-dialog")) break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        verifyEquals("Returning Device", selenium.getText("css=h2.dialog-title"));
        selenium.click("link=Cancel");
        for (int second = 0; ; second++) {
            if (second >= 60) fail("timeout");
            try {
                if (!selenium.isElementPresent("css=#returning-device-dialog")) break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        selenium.click("xpath=(//a[contains(text(),'Details')])[12]");
        for (int second = 0; ; second++) {
            if (second >= 60) fail("timeout");
            try {
                if (selenium.isElementPresent("css=#device-details-dialog")) break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        verifyEquals("Device Details", selenium.getText("css=h2.dialog-title"));
        verifyEquals("Nexus 4", selenium.getText("//div[@id='device-details-dialog']/div/div/div/table/tbody/tr/td[2]"));
        selenium.click("css=button.button-panel-button");
        for (int second = 0; ; second++) {
            if (second >= 60) fail("timeout");
            try {
                if (!selenium.isElementPresent("css=#device-details-dialog")) break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        selenium.click("xpath=(//a[contains(text(),'Lending out')])[8]");
        for (int second = 0; ; second++) {
            if (second >= 60) fail("timeout");
            try {
                if (selenium.isElementPresent("css=#lend-out-dialog")) break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        verifyEquals("Lending Out - Nexus 7 (8 GB)", selenium.getText("css=#lend-out-dialog > div.dialog-components > h2.dialog-title"));
        verifyEquals("444", selenium.getValue("css=#serial"));
        selenium.click("link=Cancel");
        selenium.click("xpath=(//a[contains(text(),'Sort out')])[6]");
        verifyEquals("Sort Out Device", selenium.getText("css=h2.dialog-title"));
        selenium.click("link=Cancel");
        selenium.click("xpath=(//a[contains(text(),'Details')])[14]");
        for (int second = 0; ; second++) {
            if (second >= 60) fail("timeout");
            try {
                if (selenium.isElementPresent("css=#device-details-dialog")) break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        verifyEquals("Device Details", selenium.getText("css=h2.dialog-title"));
        verifyEquals("Nexus 7", selenium.getText("//div[@id='device-details-dialog']/div/div/div/table/tbody/tr/td[2]"));
        selenium.click("css=button.button-panel-button");
        selenium.click("xpath=(//a[contains(text(),'Return device')])[4]");
        for (int second = 0; ; second++) {
            if (second >= 60) fail("timeout");
            try {
                if (selenium.isElementPresent("css=#returning-device-dialog")) break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        verifyEquals("Returning Device", selenium.getText("css=h2.dialog-title"));
        selenium.click("link=Cancel");
        selenium.click("xpath=(//a[contains(text(),'Details')])[16]");
        for (int second = 0; ; second++) {
            if (second >= 60) fail("timeout");
            try {
                if (selenium.isElementPresent("css=#device-details-dialog")) break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        verifyEquals("Device Details", selenium.getText("css=h2.dialog-title"));
        verifyEquals("Nexus 7", selenium.getText("//div[@id='device-details-dialog']/div/div/div/table/tbody/tr/td[2]"));
        selenium.click("css=button.button-panel-button");
    }

}
