/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.tests.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.server.ClientConnector;
import com.vaadin.tests.util.MockUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComponentTest;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

/**
 * Test for mark as dirty listener functionality.
 */
public class MarkAsDirtyListenerTest {

    @Test
    public void fire_event_when_ui_marked_dirty() {
        UI ui = new MockUI();

        AtomicReference<UI.MarkedAsDirtyConnectorEvent> events = new AtomicReference<>();
        ui.addMarkedAsDirtyListener(event -> Assert
                .assertTrue("No reference should have been registered",
                        events.compareAndSet(null, event)));
        // UI is marked dirty on creation and when adding a listener
        ComponentTest.syncToClient(ui);

        ui.getConnectorTracker().markDirty(ui);

        Assert.assertNotNull("Mark as dirty event should have fired",
                events.get());
        Assert.assertEquals("Event contains wrong ui", ui,
                events.get().getUi());
        Assert.assertEquals("Found wrong connector in event", ui,
                events.get().getConnector());
    }

    @Test
    public void fire_event_for_setContent() {
        List<UI.MarkedAsDirtyConnectorEvent> events = new ArrayList<>();
        UI ui = new MockUI() {{
            addMarkedAsDirtyListener(event -> events.add(event));
        }};
        ComponentTest.syncToClient(ui);

        Button button = new Button("Button");
        ui.setContent(button);

        Assert.assertEquals("Mark as dirty events should have fired", 2,
                events.size());
        Assert.assertEquals("Expected button to inform first for creation",
                button, events.get(0).getConnector());
        Assert.assertEquals("Expected UI marked as dirty for setContent", ui,
                events.get(1).getConnector());
    }

    @Test
    public void fire_event_for_component_stateChange() {
        UI ui = new MockUI();
        Button button = new Button("empty");
        ui.setContent(button);
        ComponentTest.syncToClient(button);

        AtomicReference<UI.MarkedAsDirtyConnectorEvent> events = new AtomicReference<>();
        ui.addMarkedAsDirtyListener(event -> Assert
                .assertTrue("No reference should have been registered",
                        events.compareAndSet(null, event)));

        button.setIconAlternateText("alternate");

        Assert.assertNotNull("Mark as dirty event should have fired",
                events.get());
        Assert.assertEquals("Event contains wrong ui", ui,
                events.get().getUi());
        Assert.assertEquals("Found wrong connector in event", button,
                events.get().getConnector());
    }

    @Test
    public void fire_events_for_each_component() {
        List<UI.MarkedAsDirtyConnectorEvent> events = new ArrayList<>();
        UI ui = new MockUI() {{
            addMarkedAsDirtyListener(event -> events.add(event));
        }};

        HorizontalLayout layout = new HorizontalLayout();
        // UI initially marked as dirty so should not show as event.
        ui.setContent(layout);
        TextField field = new TextField("Name");
        Button button = new Button("say hello");
        layout.addComponents(field, button);

        Assert.assertFalse("Mark as dirty event should have fired",
                events.isEmpty());
        Assert.assertEquals("Unexpected amount of connector events", 3,
                events.size());

        Set<ClientConnector> connectors = events.stream()
                .map(UI.MarkedAsDirtyConnectorEvent::getConnector)
                .collect(Collectors.toSet());

        Assert.assertTrue(
                "HorizontalLayout should have fired an markedAsDirty event",
                connectors.contains(layout));
        Assert.assertTrue("TextField should have fired an markedAsDirty event",
                connectors.contains(field));
        Assert.assertTrue("Button should have fired an markedAsDirty event",
                connectors.contains(button));
    }
}
