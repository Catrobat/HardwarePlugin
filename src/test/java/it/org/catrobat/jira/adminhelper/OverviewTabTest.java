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

public class OverviewTabTest extends SeleniumTestBase {

    @Test
    public void testOverviewTab() throws Exception {
        selenium.waitForPageToLoad("30000");
        selenium.selectWindow("null");
        verifyEquals("Overview", selenium.getText("css=h3"));
        verifyEquals("Nexus 4", selenium.getText("css=td.name"));
        verifyEquals("8 GB", selenium.getText("css=td.version"));
        verifyEquals("Smartphone", selenium.getText("css=td.type"));
        verifyEquals("Android", selenium.getText("css=td.os"));
        verifyEquals("2/3", selenium.getText("css=td.available"));
        verifyEquals("Lending out", selenium.getText("id=1"));
        verifyEquals("Nexus 7", selenium.getText("//tbody[@id='table-overview-body']/tr[2]/td"));
        verifyEquals("8 GB", selenium.getText("//tbody[@id='table-overview-body']/tr[2]/td[2]"));
        verifyEquals("Smartphone", selenium.getText("//tbody[@id='table-overview-body']/tr[2]/td[3]"));
        verifyEquals("Android", selenium.getText("//tbody[@id='table-overview-body']/tr[2]/td[4]"));
        verifyEquals("1/2", selenium.getText("//tbody[@id='table-overview-body']/tr[2]/td[5]"));
        verifyEquals("Lending out", selenium.getText("id=2"));
        selenium.click("id=1");
        for (int second = 0; ; second++) {
            if (second >= 60) fail("timeout");
            try {
                if (selenium.isElementPresent("css=h2.dialog-title")) break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        verifyEquals("Lending Out - Nexus 4 (8 GB)", selenium.getText("css=h2.dialog-title"));
        verifyEquals("111", selenium.getText("css=#lend-out-dialog #table-lent-out > tr > td.serial"));
        verifyEquals("111", selenium.getText("css=#lend-out-dialog #table-lent-out > tr > td.imei"));
        verifyEquals("111", selenium.getText("css=#lend-out-dialog #table-lent-out > tr > td.inventory"));
        verifyEquals("789", selenium.getText("css=#lend-out-dialog #table-lent-out > tr:nth-child(2) > td.serial"));
        verifyEquals("890", selenium.getText("css=#lend-out-dialog #table-lent-out > tr:nth-child(2) > td.imei"));
        verifyEquals("901", selenium.getText("css=#lend-out-dialog #table-lent-out > tr:nth-child(2) > td.inventory"));
        selenium.click("link=Cancel");
        selenium.click("id=2");
        for (int second = 0; ; second++) {
            if (second >= 60) fail("timeout");
            try {
                if (selenium.isElementPresent("css=h2.dialog-title")) break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        verifyEquals("Lending Out - Nexus 7 (8 GB)", selenium.getText("css=h2.dialog-title"));
        verifyEquals("444", selenium.getText("css=#lend-out-dialog #table-lent-out > tr > td.serial"));
        verifyEquals("", selenium.getText("css=#lend-out-dialog #table-lent-out > tr > td.imei"));
        verifyEquals("", selenium.getText("css=#lend-out-dialog #table-lent-out > tr > td.inventory"));
        selenium.click("link=Cancel");
    }

}
