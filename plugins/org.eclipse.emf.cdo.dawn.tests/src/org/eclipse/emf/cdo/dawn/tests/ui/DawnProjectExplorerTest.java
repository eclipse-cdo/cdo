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
import org.eclipse.emf.cdo.dawn.ui.views.DawnExplorer;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.ui.IViewPart;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Martin Fluegge
 */
@CleanRepositoriesBefore
@RunWith(SWTBotJunit4ClassRunner.class)
public class DawnProjectExplorerTest extends AbstractDawnUITest<SWTWorkbenchBot>
{
  @Test
  public void testOpenDawnExplorer() throws Exception
  {
    getBot().menu("Window").menu("Show View").menu("Other...").click();

    SWTBotShell shell = getBot().shell("Show View");
    shell.activate();
    getBot().tree().expandNode("Dawn").select("Dawn Explorer");
    getBot().button("OK").click();

    SWTBotView activeView = getBot().activeView();
    assertEquals("Dawn Explorer", activeView.getViewReference().getTitle());
    IViewPart view = activeView.getViewReference().getView(false);
    assertInstanceOf(DawnExplorer.class, view);
  }
}
