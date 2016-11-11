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

import com.atlassian.selenium.SeleniumClient;
import com.atlassian.selenium.SingleBrowserSeleniumClient;
import junit.framework.TestCase;

import static com.atlassian.selenium.browsers.AutoInstallClient.assertThat;
import static com.atlassian.selenium.browsers.AutoInstallClient.seleniumClient;
public class TestHelloWorld extends TestCase
{
    public void testHelloWorld() throws Exception
    {
        SeleniumClient client = seleniumClient();
        client.open("/");
//         client.get
        client.waitForPageToLoad("3000");
        client.open("/plugins/servlet/admin_helper/hardware");
        client.waitForPageToLoad("3000");
        assertThat().textPresent("Hello world");
    }
}