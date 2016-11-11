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

public class IndividualRelatedLendingTabTest extends SeleniumTestBase {

    @Test
    public void testBacked() throws Exception {
        selenium.waitForPageToLoad("30000");
        selenium.click("id=aui-uid-6");
        verifyEquals("Please use above input mask to search for an user.", selenium.getText("css=#table-individual > tr > td"));
        verifyEquals("Search for user", selenium.getText("css=span.select2-chosen"));
        selenium.click("css=span.select2-chosen");
        selenium.typeKeys("css=input.select2-input.select2-focused", "admin");
        verifyEquals("admin", selenium.getValue("css=input.select2-input.select2-focused"));
        for (int second = 0; ; second++) {
            if (second >= 60) fail("timeout");
            try {
                if ("admin".equals(selenium.getText("css=li.select2-highlighted"))) break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        verifyEquals("admin", selenium.getText("css=li.select2-highlighted"));
        selenium.keyPress("css=#select2-drop input.select2-input", "\\13");
        verifyEquals("Nexus 4 (8 GB)", selenium.getText("css=#table-individual > tr > td.name"));
        verifyEquals("111", selenium.getText("css=#table-individual > tr > td.serial"));
        verifyEquals("111", selenium.getText("css=#table-individual > tr > td.imei"));
        verifyEquals("111", selenium.getText("css=#table-individual > tr > td.inventory"));
        verifyEquals("admin", selenium.getText("css=td.issuer"));
        verifyEquals("2014-09-23", selenium.getText("css=td.begin"));
        verifyEquals("2014-09-23", selenium.getText("css=td.end"));
        verifyEquals("just4fun", selenium.getText("css=td.purpose"));
        verifyEquals("<script>alert('xss');</script>", selenium.getText("css=td.comment"));
        verifyEquals("Nexus 7 (8 GB)", selenium.getText("//tbody[@id='table-individual']/tr[2]/td"));
        verifyEquals("666", selenium.getText("//tbody[@id='table-individual']/tr[2]/td[2]"));
        verifyEquals("", selenium.getText("//tbody[@id='table-individual']/tr[2]/td[3]"));
        verifyEquals("", selenium.getText("//tbody[@id='table-individual']/tr[2]/td[4]"));
        verifyEquals("admin", selenium.getText("//tbody[@id='table-individual']/tr[2]/td[5]"));
        verifyEquals("2014-09-23", selenium.getText("//tbody[@id='table-individual']/tr[2]/td[6]"));
        selenium.click("xpath=(//a[contains(text(),'Details')])[17]");
        selenium.click("css=button.item-button");
        verifyEquals("Device Details", selenium.getText("css=h2.dialog-title"));
        verifyEquals("Nexus 4", selenium.getText("//div[@id='device-details-dialog']/div/div/div/table/tbody/tr/td[2]"));
        selenium.click("css=button.button-panel-button");
        selenium.click("xpath=(//a[contains(text(),'Details')])[18]");
        selenium.click("css=button.item-button");
        verifyEquals("Device Details", selenium.getText("css=h2.dialog-title"));
        verifyEquals("Nexus 7", selenium.getText("//div[@id='device-details-dialog']/div/div/div/table/tbody/tr/td[2]"));
        selenium.click("css=button.button-panel-button");
    }
}
