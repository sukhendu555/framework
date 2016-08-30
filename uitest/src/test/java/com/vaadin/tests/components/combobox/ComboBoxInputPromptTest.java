/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.components.combobox;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.v7.testbench.customelements.ComboBoxElement;

public class ComboBoxInputPromptTest extends MultiBrowserTest {

    @Test
    public void promptIsHiddenForDisabledAndReadonly() {
        openTestURL();

        ComboBoxElement normalComboBox = getComboBoxWithCaption("Normal");
        ComboBoxElement disabledComboBox = getComboBoxWithCaption("Disabled");
        ComboBoxElement readOnlyComboBox = getComboBoxWithCaption("Read-only");

        assertThat(getInputPromptValue(normalComboBox),
                is("Normal input prompt"));
        assertThat(getInputPromptValue(disabledComboBox), isEmptyString());
        assertThat(getInputPromptValue(readOnlyComboBox), isEmptyString());

        toggleDisabledAndReadonly();
        assertThat(getInputPromptValue(disabledComboBox),
                is("Disabled input prompt"));
        assertThat(getInputPromptValue(readOnlyComboBox),
                is("Read-only input prompt"));

        toggleDisabledAndReadonly();
        assertThat(getInputPromptValue(disabledComboBox), isEmptyString());
        assertThat(getInputPromptValue(readOnlyComboBox), isEmptyString());
    }

    private void toggleDisabledAndReadonly() {
        $(ButtonElement.class).first().click();
    }

    private String getInputPromptValue(ComboBoxElement comboBox) {
        WebElement input = comboBox.findElement(By.tagName("input"));

        return input.getAttribute("value");
    }

    private ComboBoxElement getComboBoxWithCaption(String caption) {
        return $(ComboBoxElement.class).caption(caption).first();
    }

}
