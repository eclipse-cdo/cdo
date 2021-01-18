/*
 * Copyright (c) 2012, 2015, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.client.offline;

import org.eclipse.emf.cdo.examples.client.offline.nodes.Node;
import org.eclipse.emf.cdo.examples.client.offline.nodes.NodeManager;
import org.eclipse.emf.cdo.examples.client.offline.nodes.NodeManagerDialog;

import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import java.io.File;

/**
 * @author Eike Stepper
 */
public class Application implements IApplication
{
  public static final String PLUGIN_ID = "org.eclipse.emf.cdo.examples.client.offline";

  private static final String ROOT_PROPERTY = "node.manager.root";

  public static Node NODE;

  @Override
  public Object start(IApplicationContext context)
  {
    Display display = PlatformUI.createDisplay();

    try
    {
      String rootProperty = System.getProperty(ROOT_PROPERTY);
      if (rootProperty == null)
      {
        System.err.println("Property not set: " + ROOT_PROPERTY);
        return IApplication.EXIT_OK;
      }

      NodeManager nodeManager = new NodeManager(new File(rootProperty));
      NodeManagerDialog dialog = new NodeManagerDialog(UIUtil.getShell(), nodeManager);
      if (dialog.open() != NodeManagerDialog.OK)
      {
        return IApplication.EXIT_OK;
      }

      NODE = dialog.getCurrentNode();
      System.out.println("Node: " + NODE);
      BusyIndicator.showWhile(display, new Runnable()
      {
        @Override
        public void run()
        {
          NODE.start();
        }
      });

      // Make sure that the workbench doesn't persist the perspective layout in a single shared file.
      System.setProperty("clearPersistedState", "true");
      System.setProperty("persistState", "false");

      int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
      if (returnCode == PlatformUI.RETURN_RESTART)
      {
        return IApplication.EXIT_RESTART;
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    finally
    {
      if (NODE != null)
      {
        try
        {
          NODE.stop();
        }
        catch (Exception ex)
        {
          ex.printStackTrace();
        }

        NODE = null;
      }

      display.dispose();
    }

    return IApplication.EXIT_OK;
  }

  @Override
  public void stop()
  {
    if (!PlatformUI.isWorkbenchRunning())
    {
      return;
    }

    final IWorkbench workbench = PlatformUI.getWorkbench();
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
