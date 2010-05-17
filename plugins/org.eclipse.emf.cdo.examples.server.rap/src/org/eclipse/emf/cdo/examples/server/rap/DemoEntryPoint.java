/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.server.rap;

import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.rwt.lifecycle.UICallBack;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.WorkbenchAdvisor;

/**
 * @author Eike Stepper
 */
public class DemoEntryPoint implements IEntryPoint
{
  public int createUI()
  {
    UICallBack.activate(getClass().getName());
    WorkbenchAdvisor workbenchAdvisor = new DemoWorkbenchAdvisor();

    Display display = PlatformUI.createDisplay();
    int result = PlatformUI.createAndRunWorkbench(display, workbenchAdvisor);
    display.dispose();
    return result;
  }
}
