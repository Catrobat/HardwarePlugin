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

public class SortOutDialogTest extends SeleniumTestBase {

    @Test
    public void testSortOutDialog() throws Exception {
        selenium.waitForPageToLoad("30000");
        selenium.click("css=#aui-uid-3 > strong");
        verifyEquals("Nexus 4 (8 GB)", selenium.getText("css=#table-sorted-out > tr > td.name"));
        verifyEquals("222", selenium.getText("css=#table-sorted-out > tr > td.serial"));
        verifyEquals("222", selenium.getText("css=#table-sorted-out > tr > td.imei"));
        verifyEquals("222", selenium.getText("css=#table-sorted-out > tr > td.inventory"));
        verifyEquals("2014-09-23", selenium.getText("css=td.date"));
        verifyEquals("Nexus 7 (8 GB)", selenium.getText("//tbody[@id='table-sorted-out']/tr[2]/td"));
        verifyEquals("555", selenium.getText("//tbody[@id='table-sorted-out']/tr[2]/td[2]"));
        verifyEquals("", selenium.getText("//tbody[@id='table-sorted-out']/tr[2]/td[3]"));
        verifyEquals("", selenium.getText("//tbody[@id='table-sorted-out']/tr[2]/td[4]"));
        verifyEquals("", selenium.getText("//tbody[@id='table-sorted-out']/tr[2]/td[5]"));
        verifyEquals("2014-09-23", selenium.getText("//tbody[@id='table-sorted-out']/tr[2]/td[6]"));
        selenium.click("css=#aui-uid-2 > strong");
        verifyEquals("Nexus 4 (8 GB)", selenium.getText("css=#table-active-devices > tr > td.name"));
        verifyEquals("111", selenium.getText("css=#table-active-devices > tr > td.serial"));
        verifyEquals("111", selenium.getText("css=#table-active-devices > tr > td.imei"));
        verifyEquals("111", selenium.getText("css=#table-active-devices > tr > td.inventory"));
        verifyEquals("", selenium.getText("css=#table-active-devices > tr > td.lent-out-since"));
        verifyEquals("", selenium.getText("css=#table-active-devices > tr > td.lent-out-by"));
        selenium.click("link=Sort out");
        verifyEquals("Sort Out Device", selenium.getText("css=h2.dialog-title"));
        verifyEquals(today, selenium.getValue("id=sort-out-date"));
        selenium.type("id=sort-out-comment", "selenium sort out comment");
        selenium.click("css=button.button-panel-button");
        verifyEquals("Success!", selenium.getText("css=p.title > strong"));
        verifyEquals("Success!\nDevice sorted out successfully", selenium.getText("//div[@id='aui-message-bar']/div"));
        verifyEquals("Nexus 4 (8 GB)", selenium.getText("css=#table-active-devices > tr > td.name"));
        verifyEquals("333", selenium.getText("css=#table-active-devices > tr > td.serial"));
        verifyEquals("", selenium.getText("css=#table-active-devices > tr > td.imei"));
        verifyEquals("", selenium.getText("css=#table-active-devices > tr > td.inventory"));
        verifyEquals("2014-09-23", selenium.getText("css=#table-active-devices > tr > td.lent-out-since"));
        verifyEquals("stephanfellhofer", selenium.getText("css=#table-active-devices > tr > td.lent-out-by"));
        selenium.click("css=#aui-uid-3 > strong");
        verifyEquals("Nexus 4 (8 GB)", selenium.getText("css=#table-sorted-out > tr > td.name"));
        verifyEquals("111", selenium.getText("css=#table-sorted-out > tr > td.serial"));
        verifyEquals("111", selenium.getText("css=#table-sorted-out > tr > td.imei"));
        verifyEquals("111", selenium.getText("css=#table-sorted-out > tr > td.inventory"));
        verifyEquals(today, selenium.getText("css=td.date"));
        verifyEquals("Nexus 4 (8 GB)", selenium.getText("//tbody[@id='table-sorted-out']/tr[2]/td"));
        verifyEquals("222", selenium.getText("//tbody[@id='table-sorted-out']/tr[2]/td[2]"));
        verifyEquals("222", selenium.getText("//tbody[@id='table-sorted-out']/tr[2]/td[3]"));
        verifyEquals("222", selenium.getText("//tbody[@id='table-sorted-out']/tr[2]/td[4]"));
        verifyEquals("Google Inc.", selenium.getText("//tbody[@id='table-sorted-out']/tr[2]/td[5]"));
        verifyEquals("2014-09-23", selenium.getText("//tbody[@id='table-sorted-out']/tr[2]/td[6]"));
        verifyEquals("Nexus 7 (8 GB)", selenium.getText("//tbody[@id='table-sorted-out']/tr[3]/td"));
        verifyEquals("555", selenium.getText("//tbody[@id='table-sorted-out']/tr[3]/td[2]"));
        verifyEquals("", selenium.getText("//tbody[@id='table-sorted-out']/tr[3]/td[3]"));
        verifyEquals("", selenium.getText("//tbody[@id='table-sorted-out']/tr[3]/td[4]"));
        verifyEquals("", selenium.getText("//tbody[@id='table-sorted-out']/tr[3]/td[5]"));
        verifyEquals("2014-09-23", selenium.getText("//tbody[@id='table-sorted-out']/tr[3]/td[6]"));
        selenium.click("xpath=(//a[contains(text(),'Details')])[7]");
        selenium.click("//div[@id='device-details-dialog']/div/ul/li[2]/button");
        verifyEquals(today, selenium.getText("//div[@id='device-details-dialog']/div/div/div[2]/table/tbody/tr[7]/td[2]"));
        verifyEquals("selenium sort out comment", selenium.getText("//div[@id='device-details-dialog']/div/div/div[2]/table/tbody/tr[8]/td[2]"));
        selenium.click("css=button.button-panel-button");
    }

}
