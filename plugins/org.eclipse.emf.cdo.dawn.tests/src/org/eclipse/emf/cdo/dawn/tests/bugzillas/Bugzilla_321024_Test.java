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
package org.eclipse.emf.cdo.dawn.tests.bugzillas;

import org.eclipse.emf.cdo.dawn.tests.AbstractDawnGEFTest;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Martin Fluegge
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class Bugzilla_321024_Test extends AbstractDawnGEFTest
{
  @Test
  public void testCreateNewDawnDiagramEmptySemanticResourceName() throws Exception
  {
    getBot().menu("File").menu("New").menu("Other...").click();

    SWTBotShell shell = getBot().shell("New");
    shell.activate();
    getBot().tree().expandNode("Dawn Examples").select("Dawn Acore Diagram");
    getBot().button("Next >").click();
    getBot().button("Finish").click();

    SWTBotGefEditor editor = getBot().gefEditor("default.acore_diagram");
    assertNotNull(editor);
    editor.close();
    {
      assertEquals(true, resourceExists("/default.acore"));
      assertEquals(true, resourceExists("/default.acore_diagram"));
    }
  }
}
