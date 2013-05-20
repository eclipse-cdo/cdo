/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.tests.ui;

import org.eclipse.emf.cdo.dawn.tests.AbstractDawnUITest;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Martin Fluegge
 */
@CleanRepositoriesBefore
@RunWith(SWTBotJunit4ClassRunner.class)
public class DawnPreferencesTest extends AbstractDawnUITest<SWTWorkbenchBot>
{
  @Test
  public void testOpenPreferencePage() throws Exception
  {
    getBot().menu("Window").menu("Preferences").click();
    SWTBotShell shell = getBot().shell("Preferences");
    shell.activate();

    getBot().tree().select("Dawn Remote Preferences");

    SWTBotText serverNameLabel = getBot().textWithLabel("server name:");
    SWTBotText serverPortLabel = getBot().textWithLabel("server port:");
    SWTBotText repositoryLabel = getBot().textWithLabel("repository:");
    SWTBotText fileNameLabel = getBot().textWithLabel("protocol:");

    assertEquals("localhost", serverNameLabel.getText());
    assertEquals("2036", serverPortLabel.getText());
    assertEquals("repo1", repositoryLabel.getText());
    assertEquals("tcp", fileNameLabel.getText());
  }
}
