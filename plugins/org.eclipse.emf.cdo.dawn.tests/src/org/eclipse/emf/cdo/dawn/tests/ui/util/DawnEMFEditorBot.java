/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.tests.ui.util;

import static org.eclipse.swtbot.eclipse.finder.matchers.WidgetMatcherFactory.withPartName;
import static org.eclipse.swtbot.eclipse.finder.waits.Conditions.waitForEditor;
import static org.hamcrest.core.AllOf.allOf;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.waits.WaitForEditor;
import org.eclipse.swtbot.eclipse.gef.finder.matchers.IsInstanceOf;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.ui.IEditorReference;

import org.hamcrest.Matcher;

/**
 * @author Martin Fluegge
 */
public class DawnEMFEditorBot extends SWTWorkbenchBot
{
  public DawnSWTBotEMFEditor emfEditor(String fileName) throws WidgetNotFoundException
  {
    return emfEditor(fileName, 0);
  }

  public DawnSWTBotEMFEditor emfEditor(String fileName, int index) throws WidgetNotFoundException
  {
    Matcher<IEditorReference> withPartName = withPartName(fileName);
    Matcher<IEditorReference> matcher = allOf(IsInstanceOf.instanceOf(IEditorReference.class), withPartName);
    WaitForEditor waitForEditor = waitForEditor(matcher);
    waitUntilWidgetAppears(waitForEditor);
    return createEditor(waitForEditor.get(index), this);
  }

  protected DawnSWTBotEMFEditor createEditor(final IEditorReference reference, final SWTWorkbenchBot bot)
  {
    return new DawnSWTBotEMFEditor(reference, bot);
  }
}
