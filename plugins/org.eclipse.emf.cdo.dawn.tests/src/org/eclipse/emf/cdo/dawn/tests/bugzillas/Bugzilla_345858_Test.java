/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.tests.bugzillas;

import org.eclipse.emf.cdo.dawn.tests.AbstractDawnGEFTest;
import org.eclipse.emf.cdo.dawn.tests.ui.util.DawnAcoreTestUtil;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Martin Fluegge
 */
@CleanRepositoriesBefore
@RunWith(SWTBotJunit4ClassRunner.class)
public class Bugzilla_345858_Test extends AbstractDawnGEFTest
{
  @Test
  public void testCreateNewDawnDiagramEmptySemanticResourceName() throws Exception
  {
    SWTBotGefEditor editor = DawnAcoreTestUtil.openNewAcoreGMFEditor("default0.acore_diagram", getBot());
    editor.close();
    sleep(1000);

    editor = DawnAcoreTestUtil.openNewAcoreGMFEditor("default1.acore_diagram", getBot());
    editor.close();
    sleep(1000);

    editor = DawnAcoreTestUtil.openNewAcoreGMFEditor("default2.acore_diagram", getBot());
    editor.close();
    sleep(1000);
  }
}
