/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.ide;

import org.eclipse.emf.cdo.releng.workingsets.WorkingSetGroup;
import org.eclipse.emf.cdo.releng.workingsets.util.WorkingSetsUtil;

import org.eclipse.emf.ecore.resource.Resource;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author Eike Stepper
 */
public final class WorkingSets
{
  private static final SetupContext CONTEXT = Activator.getDefault();

  private static final IWorkbench WORKBENCH = PlatformUI.getWorkbench();

  private static final String JAVA_PACKAGE_EXPLORER_VIEW_ID = "org.eclipse.jdt.ui.PackageExplorer";

  public static void init() throws IOException
  {
    WorkingSetGroup workingSetGroup = CONTEXT.getSetup().getBranch().getProject().getWorkingSetGroup();
    if (workingSetGroup != null)
    {
      initPackageExplorer();

      WorkingSetGroup defaultWorkingSetGroup = WorkingSetsUtil.getWorkingSetGroup();
      Resource resource = defaultWorkingSetGroup.eResource();
      resource.getContents().set(0, workingSetGroup);
      resource.save(null);
    }
  }

  private static void initPackageExplorer()
  {
    IWorkbenchWindow workbenchWindow = WORKBENCH.getWorkbenchWindows()[0];
    for (final IViewReference viewReference : workbenchWindow.getActivePage().getViewReferences())
    {
      if (JAVA_PACKAGE_EXPLORER_VIEW_ID.equals(viewReference.getId()))
      {
        workbenchWindow.getShell().getDisplay().asyncExec(new Runnable()
        {
          public void run()
          {
            IViewPart view = viewReference.getView(false);
            if (view != null)
            {
              try
              {
                Method method = view.getClass().getMethod("rootModeChanged", int.class);
                method.invoke(view, 2);
              }
              catch (Exception ex)
              {
                Activator.log(ex);
              }
            }
          }
        });
      }
    }
  }
}
