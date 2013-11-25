/*
 * Copyright (C) 2013 salesforce.com, inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.auraframework.components.ui.tabset;

import org.auraframework.test.WebDriverTestCase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class TabsetUITest extends WebDriverTestCase {
    private final String URL = "/uitest/tabset_Test.cmp";

    public TabsetUITest(String name) {
        super(name);
    }

    public void testTabSwitching() throws Exception {
        open(URL);

        WebDriver d = getDriver();
        WebElement tab1 = d.findElement(By.cssSelector("ul[class='tabLabels'] li:first-child"));
        WebElement tab2 = d.findElement(By.cssSelector("ul[class='tabLabels'] li:first-child+li"));

        // verify initial setup
        verifyTab(tab1, "tab 1 contents", true);
        verifyTab(tab2, "tab 2 contents", false);

        // switch tabs by clicking on the anchor (to make this test cross
        // browser).
        tab2.findElement(By.tagName("a")).click();

        // verify switched
        verifyTab(tab1, "tab 1 contents", false);
        verifyTab(tab2, "tab 2 contents", true);
    }

    private void verifyTab(WebElement tab, String tabBody, boolean isActive) {
        String tabClassName = tab.getAttribute("class").trim();
        WebElement tabLink = tab.findElement(By.tagName("a"));
        String tabLinkAria = tabLink.getAttribute("aria-selected");
        String tabBodyActual = tab.getText();

        if (isActive) {
            auraUITestingUtil.assertClassNameContains(tab, "active");
            assertTrue("Since tab is active should be able to read tab body: " + tabBody,
                    tabBodyActual.contains(tabBody));

            // accessibility check
            assertEquals("Tab role should be presentation so screen read can read '1 of 2', '2 of 2', etc..",
                    "active uiTab dynamicallyAddedFromVBox uiVbox bContainer bVerticalContainer", tabClassName);
            assertEquals("Since tab is active link should have aria set (for accessability)", "true", tabLinkAria);
        } else {
            auraUITestingUtil.assertClassNameDoesNotContain(tab, "active");
            assertFalse("Since tab is inactive should not be able to read tab body: " + tabBody,
                    tabBodyActual.contains(tabBody));
            assertFalse("Since tab is inactive link should not have aria set (for accessability)",Boolean.valueOf(tabLinkAria));
        }

    }
}
