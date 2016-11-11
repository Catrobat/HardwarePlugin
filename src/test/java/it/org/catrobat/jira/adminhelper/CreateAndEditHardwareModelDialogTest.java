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

import static org.junit.Assert.fail;

public class CreateAndEditHardwareModelDialogTest extends SeleniumTestBase {

    @Test
    public void testCreateAndEditHardwareModelDialog() throws Exception {
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
        selenium.click("id=new_model");
        verifyEquals("Create/Edit Hardware Model", selenium.getText("css=#example-dialog > div.dialog-components > h2.dialog-title"));
        selenium.type("id=name", "Selenium");
        selenium.click("css=div#s2id_type-of-device .select2-choice");
        for (int second = 0; ; second++) {
            if (second >= 60) fail("timeout");
            try {
                if ("Smartphone".equals(selenium.getText("css=li.select2-highlighted"))) break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        verifyEquals("Smartphone", selenium.getText("css=li.select2-highlighted"));
        selenium.typeKeys("css=#select2-drop input.select2-input", "SeleniumTypeOfDevice");
        for (int second = 0; ; second++) {
            if (second >= 60) SeleneseTestBase.fail("timeout");
            try {
                if ("SeleniumTypeOfDevice".equals(selenium.getText("css=li.select2-highlighted"))) break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }
        verifyEquals("SeleniumTypeOfDevice", selenium.getText("css=li.select2-highlighted"));
        verifyEquals("Smartphone", selenium.getText("css=li.select2-result:nth-child(2)"));
        selenium.keyPress("css=#select2-drop input.select2-input", "\\13");
        for (int second = 0; ; second++) {
            if (second >= 60) fail("timeout");
            try {
                if ("SeleniumTypeOfDevice".equals(selenium.getText("css=#s2id_type-of-device > a.select2-choice > span.select2-chosen")))
                    break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        selenium.type("id=version", "SeleniumVersion");
        selenium.type("id=price", "SeleniumPrice");
        selenium.click("css=div#s2id_producer .select2-choice");
        for (int second = 0; ; second++) {
            if (second >= 60) fail("timeout");
            try {
                if ("LG".equals(selenium.getText("css=li.select2-highlighted"))) break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        verifyEquals("LG", selenium.getText("css=li.select2-highlighted"));
        verifyEquals("Asus", selenium.getText("css=li.select2-result:nth-child(2)"));
        selenium.typeKeys("css=#select2-drop input.select2-input", "SeleniumProducer");
        for (int second = 0; ; second++) {
            if (second >= 60) SeleneseTestBase.fail("timeout");
            try {
                if ("SeleniumProducer".equals(selenium.getText("css=li.select2-highlighted"))) break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }
        verifyEquals("SeleniumProducer", selenium.getText("css=li.select2-highlighted"));
        verifyEquals("LG", selenium.getText("css=li.select2-result:nth-child(2)"));
        verifyEquals("Asus", selenium.getText("css=li.select2-result:nth-child(3)"));
        selenium.keyPress("css=#select2-drop input.select2-input", "\\13");
        for (int second = 0; ; second++) {
            if (second >= 60) fail("timeout");
            try {
                if ("SeleniumProducer".equals(selenium.getText("css=#s2id_producer > a.select2-choice > span.select2-chosen")))
                    break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        selenium.click("css=div#s2id_operating-system .select2-choice");
        for (int second = 0; ; second++) {
            if (second >= 60) fail("timeout");
            try {
                if ("Android".equals(selenium.getText("css=li.select2-highlighted"))) break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        verifyEquals("Android", selenium.getText("css=li.select2-highlighted"));
        selenium.typeKeys("css=#select2-drop input.select2-input", "SeleniumOS");
        for (int second = 0; ; second++) {
            if (second >= 60) SeleneseTestBase.fail("timeout");
            try {
                if ("SeleniumOS".equals(selenium.getText("css=li.select2-highlighted"))) break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }
        verifyEquals("SeleniumOS", selenium.getText("css=li.select2-highlighted"));
        verifyEquals("Android", selenium.getText("css=li.select2-result:nth-child(2)"));
        selenium.keyPress("css=#select2-drop input.select2-input", "\\13");
        for (int second = 0; ; second++) {
            if (second >= 60) fail("timeout");
            try {
                if ("SeleniumOS".equals(selenium.getText("css=#s2id_operating-system > a.select2-choice > span.select2-chosen")))
                    break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }
        selenium.type("id=article-number", "SeleniumArticleNumber");
        selenium.click("css=button.button-panel-button");
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
        verifyEquals("Selenium", selenium.getText("//tbody[@id='table-management']/tr[3]/td"));
        verifyEquals("SeleniumVersion", selenium.getText("//tbody[@id='table-management']/tr[3]/td[2]"));
        verifyEquals("SeleniumTypeOfDevice", selenium.getText("//tbody[@id='table-management']/tr[3]/td[3]"));
        verifyEquals("SeleniumOS", selenium.getText("//tbody[@id='table-management']/tr[3]/td[4]"));
        verifyEquals("0", selenium.getText("//tbody[@id='table-management']/tr[3]/td[5]"));
        selenium.click("xpath=(//a[contains(text(),'Edit')])[3]");
        verifyEquals("Selenium", selenium.getValue("id=name"));
        verifyEquals("SeleniumVersion", selenium.getValue("id=version"));
        verifyEquals("SeleniumPrice", selenium.getValue("id=price"));
        verifyEquals("SeleniumArticleNumber", selenium.getValue("id=article-number"));
        selenium.type("id=name", "SeleniumEdited");
        selenium.type("id=version", "SeleniumVersionEdited");
        selenium.type("id=price", "SeleniumPriceEdited");
        selenium.type("id=article-number", "SeleniumArticleNumberEdited");
        selenium.click("css=div#s2id_type-of-device .select2-choice");
        for (int second = 0; ; second++) {
            if (second >= 60) fail("timeout");
            try {
                if ("SeleniumTypeOfDevice".equals(selenium.getText("css=li.select2-highlighted"))) break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }
        selenium.click("css=li.select2-result");
        selenium.click("css=div#s2id_producer .select2-choice");
        for (int second = 0; ; second++) {
            if (second >= 60) fail("timeout");
            try {
                if ("SeleniumProducer".equals(selenium.getText("css=li.select2-highlighted"))) break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }
        selenium.click("css=li.select2-result");
        selenium.click("css=div#s2id_operating-system .select2-choice");
        for (int second = 0; ; second++) {
            if (second >= 60) fail("timeout");
            try {
                if ("SeleniumOS".equals(selenium.getText("css=li.select2-highlighted"))) break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }
        selenium.click("css=li.select2-result");
        selenium.click("css=button.button-panel-button");
        verifyEquals("SeleniumEdited", selenium.getText("//tbody[@id='table-management']/tr[3]/td"));
        verifyEquals("SeleniumVersionEdited", selenium.getText("//tbody[@id='table-management']/tr[3]/td[2]"));
        verifyEquals("Smartphone", selenium.getText("//tbody[@id='table-management']/tr[3]/td[3]"));
        verifyEquals("Android", selenium.getText("//tbody[@id='table-management']/tr[3]/td[4]"));
        verifyEquals("0", selenium.getText("//tbody[@id='table-management']/tr[3]/td[5]"));
        selenium.click("xpath=(//a[contains(text(),'Edit')])[3]");
        verifyEquals("SeleniumEdited", selenium.getValue("id=name"));
        verifyEquals("SeleniumVersionEdited", selenium.getValue("id=version"));
        verifyEquals("SeleniumPriceEdited", selenium.getValue("id=price"));
        verifyEquals("SeleniumArticleNumberEdited", selenium.getValue("id=article-number"));
        selenium.click("link=Cancel");
    }

}
