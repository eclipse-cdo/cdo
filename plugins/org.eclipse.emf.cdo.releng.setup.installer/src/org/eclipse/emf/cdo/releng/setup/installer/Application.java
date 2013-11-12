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
package org.eclipse.emf.cdo.releng.setup.installer;

import org.eclipse.emf.cdo.releng.internal.setup.ui.ErrorDialog;
import org.eclipse.emf.cdo.releng.setup.Preferences;
import org.eclipse.emf.cdo.releng.setup.installer.editor.SetupEditorAdvisor;

import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.common.util.URI;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * @author Eike Stepper
 */
public class Application implements IApplication
{
  public Object start(final IApplicationContext context) throws Exception
  {
    try
    {
      final Display display = Display.getDefault();

      int retcode;
      for (;;)
      {
        InstallerDialog dialog = new InstallerDialog(null);
        retcode = dialog.open();

        if (retcode == InstallerDialog.RETURN_RESTART)
        {
          return EXIT_RESTART;
        }

        if (retcode != InstallerDialog.RETURN_WORKBENCH)
        {
          return EXIT_OK;
        }

        display.asyncExec(new Runnable()
        {
          public void run()
          {
            IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            if (workbenchWindow == null)
            {
              display.asyncExec(this);
            }
            else
            {
              URI uri = Preferences.PREFERENCES_URI;
              IEditorInput editorInput = new URIEditorInput(uri, uri.lastSegment());

              try
              {
                IWorkbenchPage page = workbenchWindow.getActivePage();
                page.openEditor(editorInput, "org.eclipse.emf.cdo.releng.setup.installer.editor.SetupEditorID");
              }
              catch (PartInitException ex)
              {
                Activator.log(ex);
              }
            }
          }
        });

        PlatformUI.createAndRunWorkbench(display, new SetupEditorAdvisor());
      }
    }
    catch (Throwable ex)
    {
      Activator.log(ex);
      ErrorDialog.open(ex);
      return 1;
    }
  }

  public void stop()
  {
  }
}
