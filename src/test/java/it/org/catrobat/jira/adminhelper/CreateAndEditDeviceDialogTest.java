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
import org.junit.Test;

import java.util.Calendar;
import java.util.Locale;

import static org.junit.Assert.fail;

public class CreateAndEditDeviceDialogTest extends SeleniumTestBase {

    @Test
    public void testBacked() throws Exception {
        selenium.click("id=hardware_management_link");
        selenium.waitForPageToLoad("30000");
        selenium.click("css=#aui-uid-5 > strong");
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
        selenium.click("id=new_device");
        verifyEquals("Create New Device", selenium.getText("css=h2.dialog-title"));
        verifyEquals("Select a hardware model", selenium.getText("id=select_hardware_error"));
        verifyEquals("At least one unique identifier must be filled out (Serial/IMEI/Inventory)", selenium.getText("id=unique_id"));
        verifyEquals("Nexus 4 (8 GB)", selenium.getText("css=option[value=\"1\"]"));
        verifyEquals("Nexus 7 (8 GB)", selenium.getText("css=option[value=\"2\"]"));
        selenium.select("id=hardware_selection", "label=Nexus 7 (8 GB)");
        verifyNotEquals("Select a hardware model", selenium.getText("id=select_hardware_error"));
        selenium.type("id=serial", "sel-ser-111");
        verifyNotEquals("At least one unique identifier must be filled out (Serial/IMEI/Inventory)", selenium.getText("id=unique_id"));
        selenium.type("id=imei", "sel-imei-111");
        selenium.type("id=inventory", "sel-inv-111");
        verifyEquals(today, selenium.getValue("id=received_date"));
        selenium.click("css=div#s2id_received_from .select2-choice");
        for (int second = 0;; second++) {
            if (second >= 60) fail("timeout");
            try { if ("<none>".equals(selenium.getText("css=li.select2-highlighted"))) break; } catch (Exception e) {}
            Thread.sleep(1000);
        }

        verifyEquals("<none>", selenium.getText("css=li.select2-highlighted"));
        verifyEquals("Google Inc.", selenium.getText("css=li.select2-result:nth-child(2)"));
        selenium.typeKeys("css=#select2-drop input.select2-input", "SeleniumReceived");
        for (int second = 0; ; second++) {
            if (second >= 60) SeleneseTestBase.fail("timeout");
            try {
                if ("SeleniumReceived".equals(selenium.getText("css=li.select2-highlighted"))) break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }
        verifyEquals("SeleniumReceived", selenium.getText("css=li.select2-highlighted"));
        verifyEquals("<none>", selenium.getText("css=li.select2-result:nth-child(2)"));
        verifyEquals("Google Inc.", selenium.getText("css=li.select2-result:nth-child(3)"));
        selenium.keyPress("css=#select2-drop input.select2-input", "\\13");
        selenium.type("id=life_of_asset", "24");
        selenium.click("css=button.button-panel-button.dialog_submit_button");
        for (int second = 0; ; second++) {
            if (second >= 60) SeleneseTestBase.fail("timeout");
            try {
                if ("3".equals(selenium.getText("//tbody[@id='table-management']/tr[2]/td[5]"))) break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }
        verifyEquals("3", selenium.getText("//tbody[@id='table-management']/tr[2]/td[5]"));
        selenium.click("css=#aui-uid-4 > strong");
        verifyEquals("Nexus 7 (8 GB)", selenium.getText("//tbody[@id='table-all-devices']/tr[8]/td"));
        verifyEquals("sel-ser-111", selenium.getText("//tbody[@id='table-all-devices']/tr[8]/td[2]"));
        verifyEquals("sel-imei-111", selenium.getText("//tbody[@id='table-all-devices']/tr[8]/td[3]"));
        verifyEquals("sel-inv-111", selenium.getText("//tbody[@id='table-all-devices']/tr[8]/td[4]"));
        verifyEquals("SeleniumReceived", selenium.getText("//tbody[@id='table-all-devices']/tr[8]/td[5]"));
        verifyEquals("", selenium.getText("//tbody[@id='table-all-devices']/tr[8]/td[6]"));
        verifyEquals("", selenium.getText("//tbody[@id='table-all-devices']/tr[8]/td[7]"));
        verifyEquals("", selenium.getText("//tbody[@id='table-all-devices']/tr[8]/td[8]"));
        verifyEquals("Lending out / Sort out", selenium.getText("//tbody[@id='table-all-devices']/tr[8]/td[9]"));
        selenium.click("xpath=(//a[contains(text(),'Details')])[18]");
        selenium.click("//div[@id='device-details-dialog']/div/ul/li[2]/button");
        verifyEquals("sel-ser-111", selenium.getText("//div[@id='device-details-dialog']/div/div/div[2]/table/tbody/tr/td[2]"));
        verifyEquals("sel-imei-111", selenium.getText("//div[@id='device-details-dialog']/div/div/div[2]/table/tbody/tr[2]/td[2]"));
        verifyEquals("sel-inv-111", selenium.getText("//div[@id='device-details-dialog']/div/div/div[2]/table/tbody/tr[3]/td[2]"));
        verifyEquals(today, selenium.getText("//div[@id='device-details-dialog']/div/div/div[2]/table/tbody/tr[4]/td[2]"));
        verifyEquals("SeleniumReceived", selenium.getText("//div[@id='device-details-dialog']/div/div/div[2]/table/tbody/tr[5]/td[2]"));
        verifyEquals("24", selenium.getText("//div[@id='device-details-dialog']/div/div/div[2]/table/tbody/tr[6]/td[2]"));
        verifyEquals("", selenium.getText("//div[@id='device-details-dialog']/div/div/div[2]/table/tbody/tr[7]/td[2]"));
        verifyEquals("", selenium.getText("//div[@id='device-details-dialog']/div/div/div[2]/table/tbody/tr[8]/td[2]"));
        selenium.click("link=change Device details");
        verifyEquals("sel-ser-111", selenium.getValue("id=serial"));
        verifyEquals("sel-imei-111", selenium.getValue("id=imei"));
        verifyEquals("sel-inv-111", selenium.getValue("id=inventory"));
        verifyEquals(today, selenium.getValue("id=received_date"));
        verifyEquals("24", selenium.getValue("id=life_of_asset"));
        verifyEquals("", selenium.getValue("id=sort-out-comment"));
        verifyEquals("", selenium.getValue("id=sort-out-date"));
        selenium.select("id=hardware_selection", "label=Nexus 4 (8 GB)");
        selenium.type("id=serial", "sel-ser-222");
        selenium.type("id=imei", "sel-imei-222");
        selenium.type("id=inventory", "sel-inv-222");
        selenium.type("id=received_date", "1970-01-01");
        selenium.click("css=div#s2id_received_from .select2-choice");
        for (int second = 0;; second++) {
            if (second >= 60) fail("timeout");
            try { if ("<none>".equals(selenium.getText("css=li.select2-result"))) break; } catch (Exception e) {}
            Thread.sleep(1000);
        }

        verifyEquals("<none>", selenium.getText("css=li.select2-result"));
        verifyEquals("Google Inc.", selenium.getText("css=li.select2-result:nth-child(2)"));
        verifyEquals("SeleniumReceived", selenium.getText("css=li.select2-result:nth-child(3)"));
        selenium.click("css=li.select2-result:nth-child(2)");
        selenium.type("id=life_of_asset", "48");
        selenium.type("id=sort-out-comment", "selenium sort out comment");
        selenium.type("id=sort-out-date", "1974-01-01");
        selenium.click("css=button.button-panel-button.dialog_submit_button");
        verifyEquals("Nexus 4 (8 GB)", selenium.getText("//tbody[@id='table-all-devices']/tr[5]/td"));
        verifyEquals("sel-ser-222", selenium.getText("//tbody[@id='table-all-devices']/tr[5]/td[2]"));
        verifyEquals("sel-imei-222", selenium.getText("//tbody[@id='table-all-devices']/tr[5]/td[3]"));
        verifyEquals("sel-inv-222", selenium.getText("//tbody[@id='table-all-devices']/tr[5]/td[4]"));
        verifyEquals("1974-01-01", selenium.getText("//tbody[@id='table-all-devices']/tr[5]/td[8]"));
        selenium.click("xpath=(//a[contains(text(),'Details')])[15]");
        selenium.click("//div[@id='device-details-dialog']/div/ul/li[2]/button");
        verifyEquals("Google Inc.", selenium.getText("//div[@id='device-details-dialog']/div/div/div[2]/table/tbody/tr[5]/td[2]"));
        verifyEquals("48", selenium.getText("//div[@id='device-details-dialog']/div/div/div[2]/table/tbody/tr[6]/td[2]"));
        verifyEquals("selenium sort out comment", selenium.getText("//div[@id='device-details-dialog']/div/div/div[2]/table/tbody/tr[8]/td[2]"));
        selenium.click("css=button.button-panel-button");
    }

}
