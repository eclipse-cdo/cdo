/*
 * Copyright (c) 2004-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.ide;

import org.eclipse.emf.cdo.releng.setup.helper.OS;
import org.eclipse.emf.cdo.releng.setup.helper.ProgressLog;
import org.eclipse.emf.cdo.releng.setup.helper.ProgressLogRunnable;
import org.eclipse.emf.cdo.releng.setup.ui.ProgressLogDialog;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * @author Eike Stepper
 */
public final class SetupIDE
{
  private static final IWorkbench WORKBENCH = PlatformUI.getWorkbench();

  private static final int INITIAL = 0;

  private static final int DONE = 100;

  private static int state;

  public static void run() throws Exception
  {
    state = readState();
    if (state == DONE)
    {
      return;
    }

    IWorkbenchWindow window = WORKBENCH.getWorkbenchWindows()[0];
    run(window);
  }

  private static void run(IWorkbenchWindow window)
  {
    final Shell shell = window.getShell();
    shell.getDisplay().asyncExec(new Runnable()
    {
      public void run()
      {
        ProgressLogDialog.run(shell, "Setting up IDE", new ProgressLogRunnable()
        {
          public boolean run(ProgressLog log) throws Exception
          {
            boolean autoBuilding = disableAutoBuilding();

            try
            {
              return SetupIDE.run(log);
            }
            finally
            {
              restoreAutoBuilding(autoBuilding);
            }
          }
        });
      }
    });
  }

  private static boolean run(ProgressLog log) throws Exception
  {
    boolean restart = false;
    if (state < 1)
    {
      P2.registerUpdateLocations();
      saveState(1);
    }

    if (state < 2)
    {
      Prefs.init();
      saveState(2);
    }

    if (state < 3)
    {
      GitClones.init();
      saveState(3);
    }

    if (state < 4)
    {
      Buckminster.importBaseline();
      saveState(4);
    }

    if (state < 5)
    {
      Buckminster.importTarget();
      saveState(5);
    }

    if (state < DONE)
    {
      Buckminster.importMSpec();
      saveState(DONE);
      restart = true;
    }

    return restart;
  }

  private static void saveState(int state)
  {
    File stateFile = new File(Activator.getDefault().getStateLocation().toOSString(), "state.txt");
    OS.INSTANCE.writeText(stateFile, Collections.singletonList(Integer.toString(state)));
  }

  private static int readState()
  {
    File stateFile = new File(Activator.getDefault().getStateLocation().toOSString(), "state.txt");
    if (stateFile.exists())
    {
      List<String> lines = OS.INSTANCE.readText(stateFile);
      if (!lines.isEmpty())
      {
        String line = lines.get(0);
        return Integer.parseInt(line);
      }
    }

    return INITIAL;
  }

  private static boolean disableAutoBuilding()
  {
    boolean autoBuilding = ResourcesPlugin.getWorkspace().isAutoBuilding();
    if (autoBuilding)
    {
      ResourcesPlugin.getWorkspace().getDescription().setAutoBuilding(false);
    }

    return autoBuilding;
  }

  private static void restoreAutoBuilding(boolean autoBuilding)
  {
    if (autoBuilding != ResourcesPlugin.getWorkspace().isAutoBuilding())
    {
      ResourcesPlugin.getWorkspace().getDescription().setAutoBuilding(autoBuilding);
    }
  }
}
