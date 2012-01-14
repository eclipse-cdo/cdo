/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.explorer;

import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/**
 * @author Eike Stepper
 */
public class CDOExplorerWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor
{
  public CDOExplorerWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer)
  {
    super(configurer);
  }

  @Override
  public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer)
  {
    return new CDOExplorerActionBarAdvisor(configurer);
  }

  @Override
  public void preWindowOpen()
  {
    IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
    configurer.setInitialSize(new Point(700, 550));
    configurer.setShowCoolBar(false);
    configurer.setShowStatusLine(false);
    configurer.setTitle("CDO Explorer");
  }
}
