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
package org.eclipse.emf.cdo.releng.windowtitle;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * @author Eike Stepper
 */
public class TitleSetter extends Thread
{
  private boolean disposed;

  public TitleSetter()
  {
    setDaemon(true);
  }

  @Override
  public void run()
  {
    while (!disposed)
    {
      final String prefix = ResourcesPlugin.getWorkspace().getRoot().getLocation().lastSegment().toUpperCase() + " - ";
      for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows())
      {
        final Shell shell = window.getShell();
        if (shell != null && !shell.isDisposed())
        {
          Display display = shell.getDisplay();
          if (display != null && !display.isDisposed())
          {
            display.asyncExec(new Runnable()
            {
              public void run()
              {
                if (!shell.isDisposed())
                {
                  String title = shell.getText();
                  if (!title.startsWith(prefix))
                  {
                    shell.setText(prefix + title);
                  }
                }
              }
            });
          }
        }
      }

      try
      {
        sleep(500L);
      }
      catch (InterruptedException ex)
      {
        return;
      }
    }
  }

  public void dispose()
  {
    disposed = true;
    interrupt();
  }
}
