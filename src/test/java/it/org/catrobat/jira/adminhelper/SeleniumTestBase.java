/*
 * Copyright 2014 Stephan Fellhofer
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

import com.atlassian.selenium.SeleniumClient;
import com.thoughtworks.selenium.SeleneseTestBase;
import com.thoughtworks.selenium.Selenium;
import junit.framework.TestCase;

import java.util.Calendar;

import static com.atlassian.selenium.browsers.AutoInstallClient.seleniumClient;

public abstract class SeleniumTestBase extends TestCase {
    protected Selenium selenium;
    protected String today;

    @Override
    public void setUp() {
        // Backdoor testKit = jira().backdoor();
//        backdoor.applicationProperties().setString("jira.baseurl", "http://localhost:2990/jira");
//        backdoor.restoreDataFromResource("selenium.zip", TimeBombLicence.LICENCE_FOR_TESTING);
//        backdoor.websudo().disable();
//        backdoor.usersAndGroups().addUser("selenium-admin", "1234", "Selenium Admin", "selenium@test.me")
//                .addUserToGroup("selenium-admin", "jira-administrators");

//        SeleniumClient selenium = new SingleBrowserSeleniumClient(new AdminHelperSeleniumConfiguration());
//        selenium = seleniumClient();
//        selenium.waitForPageToLoad("30000");
//
//        navigation.loginUsingForm("admin", "admin", true, true);
//        navigation.gotoDashboard();
//        selenium.waitForPageToLoad("30000");
//        navigation.gotoPage("plugins/servlet/admin_helper/hardware");
        // jira().gotoLoginPage().loginAsSysAdmin(HardwareServletPage.class);
        // WebDriver driver = jira().getTester().getDriver();
        // String baseUrl = "http://localhost:2990/";

        // selenium = new WebDriverBackedSelenium(driver, baseUrl);
        SeleniumClient client = seleniumClient();
        selenium = client;

        selenium.setSpeed("3000");

//        client.open("/");
//         client.get
//        client.waitForPageToLoad("3000");
        client.open("/jira/plugins/servlet/admin_helper/hardware");
        client.waitForPageToLoad("3000");

        // logging in - if necessary
        if(selenium.isElementPresent("css=#login-form-username")) {
            // #login-form-username
            selenium.typeKeys("css=#login-form-username", "admin");
            // #login-form-password
            selenium.typeKeys("css=#login-form-password", "admin");
            // #login-form-submit
            selenium.click("css=#login-form-submit");

            client.waitForPageToLoad("3000");
        }

        Calendar calendar = Calendar.getInstance();
        String month = "" + (calendar.get(Calendar.MONTH) + 1);
        month = month.length() == 1 ? "0" + month : month;
        today = calendar.get(Calendar.YEAR) + "-" +
                month + "-" +
                calendar.get(Calendar.DAY_OF_MONTH);
    }

    protected void verifyEquals(String expected, String actual) {
        SeleneseTestBase.assertEquals(expected, actual);
    }

    protected void verifyNotEquals(String expected, String actual) {
        SeleneseTestBase.assertNotEquals(expected, actual);
    }
}
