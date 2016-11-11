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

public class DeviceDetailsDialogTest extends SeleniumTestBase {

    @Test
    public void testBacked() throws Exception {
        selenium.waitForPageToLoad("30000");
        selenium.click("css=#aui-uid-4 > strong");
        selenium.click("xpath=(//a[contains(text(),'Details')])[10]");
        selenium.click("css=button.item-button");
        verifyEquals("Device Details", selenium.getText("css=h2.dialog-title"));
        verifyEquals("Model", selenium.getText("css=button.item-button"));
        verifyEquals("Device", selenium.getText("//div[@id='device-details-dialog']/div/ul/li[2]/button"));
        verifyEquals("Device Comments", selenium.getText("//div[@id='device-details-dialog']/div/ul/li[3]/button"));
        verifyEquals("Lending History", selenium.getText("//div[@id='device-details-dialog']/div/ul/li[4]/button"));
        verifyEquals("Name", selenium.getText("css=div.dialog-panel-body.panel-body > table.aui > tbody > tr > td"));
        verifyEquals("Nexus 4", selenium.getText("//div[@id='device-details-dialog']/div/div/div/table/tbody/tr/td[2]"));
        verifyEquals("Type", selenium.getText("//div[@id='device-details-dialog']/div/div/div/table/tbody/tr[2]/td"));
        verifyEquals("Smartphone", selenium.getText("//div[@id='device-details-dialog']/div/div/div/table/tbody/tr[2]/td[2]"));
        verifyEquals("Version", selenium.getText("//div[@id='device-details-dialog']/div/div/div/table/tbody/tr[3]/td"));
        verifyEquals("8 GB", selenium.getText("//div[@id='device-details-dialog']/div/div/div/table/tbody/tr[3]/td[2]"));
        verifyEquals("Price", selenium.getText("//div[@id='device-details-dialog']/div/div/div/table/tbody/tr[4]/td"));
        verifyEquals("Producer", selenium.getText("//div[@id='device-details-dialog']/div/div/div/table/tbody/tr[5]/td"));
        verifyEquals("LG", selenium.getText("//div[@id='device-details-dialog']/div/div/div/table/tbody/tr[5]/td[2]"));
        verifyEquals("OS", selenium.getText("//div[@id='device-details-dialog']/div/div/div/table/tbody/tr[6]/td"));
        verifyEquals("Android", selenium.getText("//div[@id='device-details-dialog']/div/div/div/table/tbody/tr[6]/td[2]"));
        verifyEquals("Article Number", selenium.getText("//div[@id='device-details-dialog']/div/div/div/table/tbody/tr[7]/td"));
        verifyEquals("123", selenium.getText("//div[@id='device-details-dialog']/div/div/div/table/tbody/tr[7]/td[2]"));
        selenium.click("//div[@id='device-details-dialog']/div/ul/li[2]/button");
        verifyEquals("Serial Number", selenium.getText("//div[@id='device-details-dialog']/div/div/div[2]/table/tbody/tr/td"));
        verifyEquals("111", selenium.getText("//div[@id='device-details-dialog']/div/div/div[2]/table/tbody/tr/td[2]"));
        verifyEquals("IMEI", selenium.getText("//div[@id='device-details-dialog']/div/div/div[2]/table/tbody/tr[2]/td"));
        verifyEquals("111", selenium.getText("//div[@id='device-details-dialog']/div/div/div[2]/table/tbody/tr[2]/td[2]"));
        verifyEquals("Inventory Number", selenium.getText("//div[@id='device-details-dialog']/div/div/div[2]/table/tbody/tr[3]/td"));
        verifyEquals("111", selenium.getText("//div[@id='device-details-dialog']/div/div/div[2]/table/tbody/tr[3]/td[2]"));
        verifyEquals("Received Date", selenium.getText("//div[@id='device-details-dialog']/div/div/div[2]/table/tbody/tr[4]/td"));
        verifyEquals("2014-09-23", selenium.getText("//div[@id='device-details-dialog']/div/div/div[2]/table/tbody/tr[4]/td[2]"));
        verifyEquals("Received From", selenium.getText("//div[@id='device-details-dialog']/div/div/div[2]/table/tbody/tr[5]/td"));
        verifyEquals("Google Inc.", selenium.getText("//div[@id='device-details-dialog']/div/div/div[2]/table/tbody/tr[5]/td[2]"));
        verifyEquals("Useful life of asset", selenium.getText("//div[@id='device-details-dialog']/div/div/div[2]/table/tbody/tr[6]/td"));
        verifyEquals("1 year", selenium.getText("//div[@id='device-details-dialog']/div/div/div[2]/table/tbody/tr[6]/td[2]"));
        verifyEquals("Sorted Out Date", selenium.getText("//div[@id='device-details-dialog']/div/div/div[2]/table/tbody/tr[7]/td"));
        verifyEquals("", selenium.getText("//div[@id='device-details-dialog']/div/div/div[2]/table/tbody/tr[7]/td[2]"));
        verifyEquals("Sorted Out Comment", selenium.getText("//div[@id='device-details-dialog']/div/div/div[2]/table/tbody/tr[8]/td"));
        verifyEquals("", selenium.getText("//div[@id='device-details-dialog']/div/div/div[2]/table/tbody/tr[8]/td[2]"));
        selenium.click("//div[@id='device-details-dialog']/div/ul/li[3]/button");
        verifyEquals("admin", selenium.getText("//div[@id='device-details-dialog']/div/div/div[3]/table/tbody/tr/td"));
        verifyEquals("2014-09-23", selenium.getText("css=nobr"));
        verifyEquals("1st device comment...", selenium.getText("//div[@id='device-details-dialog']/div/div/div[3]/table/tbody/tr/td[3]"));
        selenium.click("//div[@id='device-details-dialog']/div/ul/li[4]/button");
        verifyEquals("admin", selenium.getText("//div[@id='device-details-dialog']/div/div/div[4]/table/tbody/tr/td"));
        verifyEquals("admin", selenium.getText("//div[@id='device-details-dialog']/div/div/div[4]/table/tbody/tr/td[2]"));
        verifyEquals("2014-09-23", selenium.getText("//div[@id='device-details-dialog']/div/div/div[4]/table/tbody/tr/td[3]/nobr"));
        verifyEquals("2014-09-23", selenium.getText("//div[@id='device-details-dialog']/div/div/div[4]/table/tbody/tr/td[4]/nobr"));
        verifyEquals("just4fun", selenium.getText("//div[@id='device-details-dialog']/div/div/div[4]/table/tbody/tr/td[5]"));
        verifyEquals("<script>alert('xss');</script>", selenium.getText("//div[@id='device-details-dialog']/div/div/div[4]/table/tbody/tr/td[6]"));
        selenium.click("//div[@id='device-details-dialog']/div/ul/li[2]/button");
        verifyEquals("change Device details", selenium.getText("link=change Device details"));
        selenium.click("link=change Device details");
        verifyEquals("111", selenium.getValue("id=serial"));
        verifyEquals("111", selenium.getValue("id=imei"));
        verifyEquals("111", selenium.getValue("id=inventory"));
        selenium.click("link=Cancel");
        selenium.click("xpath=(//a[contains(text(),'Details')])[15]");
        selenium.click("//div[@id='device-details-dialog']/div/ul/li[2]/button");
        verifyEquals("555", selenium.getText("//div[@id='device-details-dialog']/div/div/div[2]/table/tbody/tr/td[2]"));
        selenium.click("link=change Device details");
        verifyEquals("555", selenium.getValue("id=serial"));
        selenium.click("link=Cancel");
    }

}
