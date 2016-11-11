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

import java.util.Calendar;

import static com.thoughtworks.selenium.SeleneseTestBase.fail;

public class ReturnDialogTest extends SeleniumTestBase {

    @Test
    public void testReturnDialog() throws Exception {
        selenium.waitForPageToLoad("30000");
        verifyEquals("1/2", selenium.getText("//tbody[@id='table-overview-body']/tr[2]/td[5]"));
        selenium.click("css=#aui-uid-1 > strong");
        verifyEquals("Nexus 7 (8 GB)", selenium.getText("css=#table-lent-out > tr > td.name"));
        verifyEquals("666", selenium.getText("css=td.serial"));
        verifyEquals("", selenium.getText("css=td.imei"));
        verifyEquals("", selenium.getText("css=td.inventory"));
        verifyEquals("admin", selenium.getText("css=td.lent-out-by"));
        verifyEquals("2014-09-23", selenium.getText("css=td.lent-out-since"));
        verifyEquals("Nexus 4 (8 GB)", selenium.getText("//tbody[@id='table-lent-out']/tr[2]/td"));
        verifyEquals("333", selenium.getText("//tbody[@id='table-lent-out']/tr[2]/td[2]"));
        verifyEquals("", selenium.getText("//tbody[@id='table-lent-out']/tr[2]/td[3]"));
        verifyEquals("", selenium.getText("//tbody[@id='table-lent-out']/tr[2]/td[4]"));
        verifyEquals("stephanfellhofer", selenium.getText("//tbody[@id='table-lent-out']/tr[2]/td[5]"));
        verifyEquals("2014-09-23", selenium.getText("//tbody[@id='table-lent-out']/tr[2]/td[6]"));
        selenium.click("link=Return");
        selenium.type("id=device-comment", "selenium device comment");
        selenium.type("id=lending-purpose", "selenium purpose");
        selenium.type("id=lending-comment", "selenium lending comment");
        selenium.click("css=button.button-panel-button");
        verifyEquals("Nexus 4 (8 GB)", selenium.getText("css=#table-lent-out > tr > td.name"));
        verifyEquals("333", selenium.getText("css=td.serial"));
        verifyEquals("", selenium.getText("css=td.imei"));
        verifyEquals("", selenium.getText("css=td.inventory"));
        verifyEquals("stephanfellhofer", selenium.getText("css=td.lent-out-by"));
        verifyEquals("2014-09-23", selenium.getText("css=td.lent-out-since"));
        verifyEquals("Success!", selenium.getText("css=p.title > strong"));
        verifyEquals("Success!\nDevice brought back successfully", selenium.getText("//div[@id='aui-message-bar']/div"));
        selenium.click("css=#aui-uid-0 > strong");
        verifyEquals("2/2", selenium.getText("//tbody[@id='table-overview-body']/tr[2]/td[5]"));
        selenium.click("css=#aui-uid-4 > strong");
        selenium.click("xpath=(//a[contains(text(),'Details')])[15]");
        selenium.click("//div[@id='device-details-dialog']/div/ul/li[3]/button");
        verifyEquals("admin", selenium.getText("//div[@id='device-details-dialog']/div/div/div[3]/table/tbody/tr/td"));
        verifyEquals("2014-09-23", selenium.getText("css=nobr"));
        verifyEquals("lending out comment in the device comment section :D", selenium.getText("//div[@id='device-details-dialog']/div/div/div[3]/table/tbody/tr/td[3]"));
        verifyEquals("admin", selenium.getText("//div[@id='device-details-dialog']/div/div/div[3]/table/tbody/tr[2]/td"));
        verifyEquals(today, selenium.getText("//div[@id='device-details-dialog']/div/div/div[3]/table/tbody/tr[2]/td[2]/nobr"));
        verifyEquals("selenium device comment", selenium.getText("//div[@id='device-details-dialog']/div/div/div[3]/table/tbody/tr[2]/td[3]"));
        selenium.click("//div[@id='device-details-dialog']/div/ul/li[4]/button");
        verifyEquals("admin", selenium.getText("//div[@id='device-details-dialog']/div/div/div[4]/table/tbody/tr/td"));
        verifyEquals("admin", selenium.getText("//div[@id='device-details-dialog']/div/div/div[4]/table/tbody/tr/td[2]"));
        verifyEquals("2014-09-23", selenium.getText("//div[@id='device-details-dialog']/div/div/div[4]/table/tbody/tr/td[3]/nobr"));
        verifyEquals(today, selenium.getText("//div[@id='device-details-dialog']/div/div/div[4]/table/tbody/tr/td[4]/nobr"));
        verifyEquals("selenium purpose", selenium.getText("//div[@id='device-details-dialog']/div/div/div[4]/table/tbody/tr/td[5]"));
        verifyEquals("selenium lending comment", selenium.getText("//div[@id='device-details-dialog']/div/div/div[4]/table/tbody/tr/td[6]"));
        selenium.click("css=button.button-panel-button");
    }

}