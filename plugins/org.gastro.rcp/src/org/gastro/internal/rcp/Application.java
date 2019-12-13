/*
 * Copyright (c) 2009-2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *
 *  Initial Publication:
 *    Eclipse Magazin - http://www.eclipse-magazin.de
 */
package org.gastro.internal.rcp;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * This class controls all aspects of the application's execution
 *
 * @author Eike Stepper
 */
public class Application implements IApplication
{
  /*
   * (non-Javadoc)
   * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
   */
  @Override
  public Object start(IApplicationContext context)
  {
    Display display = PlatformUI.createDisplay();
    try
    {
      int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
      if (returnCode == PlatformUI.RETURN_RESTART)
      {
        return IApplication.EXIT_RESTART;
      }

      return IApplication.EXIT_OK;
    }
    finally
    {
      display.dispose();
    }
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.equinox.app.IApplication#stop()
   */
  @Override
  public void stop()
  {
    final IWorkbench workbench = PlatformUI.getWorkbench();
    if (workbench == null)
    {
      return;
    }
    final Display display = workbench.getDisplay();
    display.syncExec(new Runnable()
    {
      @Override
      public void run()
      {
        if (!display.isDisposed())
        {
          workbench.close();
        }
      }
    });
  }
}
