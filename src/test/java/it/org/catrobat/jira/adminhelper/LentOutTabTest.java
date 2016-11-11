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

public class LentOutTabTest extends SeleniumTestBase {

    @Test
    public void testLentOutTab() throws Exception {
        selenium.waitForPageToLoad("30000");
        selenium.click("css=#aui-uid-1 > strong");
        verifyEquals("Lent Out", selenium.getText("css=#tabs-lent-out > div.left-from-search-field > h3"));
        verifyEquals("Nexus 7 (8 GB)", selenium.getText("css=#table-lent-out > tr > td.name"));
        verifyEquals("666", selenium.getText("css=#table-lent-out > tr > td.serial"));
        verifyEquals("", selenium.getText("css=#table-lent-out > tr > td.imei"));
        verifyEquals("", selenium.getText("css=#table-lent-out > tr > td.inventory"));
        verifyEquals("admin", selenium.getText("css=#table-lent-out > tr > td.lent-out-by"));
        verifyEquals("2014-09-23", selenium.getText("css=#table-lent-out > tr > td.lent-out-since"));
        verifyEquals("", selenium.getText("css=#table-lent-out > tr > td.lent-out-purpose"));
        verifyEquals("Details", selenium.getText("id=6"));
        verifyEquals("Return", selenium.getText("link=Return"));
        verifyEquals("Nexus 4 (8 GB)", selenium.getText("css=#table-lent-out > tr:nth-child(2) > td.name"));
        verifyEquals("333", selenium.getText("css=#table-lent-out > tr:nth-child(2) > td.serial"));
        verifyEquals("", selenium.getText("css=#table-lent-out > tr:nth-child(2) > td.imei"));
        verifyEquals("", selenium.getText("css=#table-lent-out > tr:nth-child(2) > td.inventory"));
        verifyEquals("stephanfellhofer", selenium.getText("css=#table-lent-out > tr:nth-child(2) > td.lent-out-by"));
        verifyEquals("2014-09-23", selenium.getText("css=#table-lent-out > tr:nth-child(2) > td.lent-out-since"));
        verifyEquals("", selenium.getText("css=#table-lent-out > tr:nth-child(2) > td.lent-out-purpose"));
        verifyEquals("Details", selenium.getText("id=3"));
        verifyEquals("Return", selenium.getText("xpath=(//a[contains(text(),'Return')])[2]"));
        selenium.click("id=6");
        for (int second = 0; ; second++) {
            if (second >= 60) fail("timeout");
            try {
                if (selenium.isElementPresent("css=h2.dialog-title")) break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        verifyEquals("Nexus 7", selenium.getText("//div[@id='device-details-dialog']/div/div/div/table/tbody/tr/td[2]"));
        selenium.click("css=button.button-panel-button");
        selenium.click("link=Return");
        for (int second = 0; ; second++) {
            if (second >= 60) fail("timeout");
            try {
                if (selenium.isElementPresent("css=h2.dialog-title")) break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        verifyEquals("Returning Device", selenium.getText("css=#returning-device-dialog > div.dialog-components > h2.dialog-title"));
        selenium.click("link=Cancel");
        selenium.click("id=3");
        for (int second = 0; ; second++) {
            if (second >= 60) fail("timeout");
            try {
                if (selenium.isElementPresent("css=h2.dialog-title")) break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        verifyEquals("Nexus 4", selenium.getText("//div[@id='device-details-dialog']/div/div/div/table/tbody/tr/td[2]"));
        selenium.click("css=button.button-panel-button");
        selenium.click("xpath=(//a[contains(text(),'Return')])[2]");
        verifyEquals("Returning Device", selenium.getText("css=#returning-device-dialog > div.dialog-components > h2.dialog-title"));
        selenium.click("link=Cancel");
    }

}
