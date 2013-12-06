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
import org.eclipse.emf.cdo.releng.internal.setup.ui.InstallerDialog;
import org.eclipse.emf.cdo.releng.internal.setup.ui.InstallerDialog.StartType;
import org.eclipse.emf.cdo.releng.internal.setup.ui.PreferenceRecorderAction;
import org.eclipse.emf.cdo.releng.setup.Preferences;
import org.eclipse.emf.cdo.releng.setup.installer.editor.SetupEditorAdvisor;

import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.common.util.URI;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import java.io.File;

/**
 * @author Eike Stepper
 */
public class Application implements IApplication
{
  public Object start(final IApplicationContext context) throws Exception
  {
    StartType startType = StartType.APPLICATION;
    File restarting = new File(Activator.getDefault().getStateLocation().toString(), "restarting");

    try
    {
      if (restarting.exists())
      {
        startType = StartType.RESTART;
        if (!restarting.delete())
        {
          restarting.deleteOnExit();
        }
      }
    }
    catch (Exception ex)
    {
      // Ignore
    }

    try
    {
      final Display display = Display.getDefault();

      for (;;)
      {
        InstallerDialog dialog = new InstallerDialog(null, startType, true);
        final int retcode = dialog.open();

        if (retcode == InstallerDialog.RETURN_RESTART)
        {
          try
          {
            restarting.createNewFile();
          }
          catch (Exception ex)
          {
            // Ignore
          }

          return EXIT_RESTART;
        }

        if (retcode != InstallerDialog.RETURN_WORKBENCH
            && retcode != InstallerDialog.RETURN_WORKBENCH_NETWORK_PREFERENCES)
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
                IEditorPart editorPart = page.openEditor(editorInput,
                    "org.eclipse.emf.cdo.releng.setup.installer.editor.SetupEditorID");

                if (retcode == InstallerDialog.RETURN_WORKBENCH_NETWORK_PREFERENCES
                    && editorPart instanceof ISelectionProvider)
                {
                  ISelectionProvider selectionProvider = (ISelectionProvider)editorPart;
                  PreferenceRecorderAction preferenceRecorderAction = new PreferenceRecorderAction(true);
                  preferenceRecorderAction.setChecked(false);
                  preferenceRecorderAction.selectionChanged(new SelectionChangedEvent(selectionProvider,
                      selectionProvider.getSelection()));
                  preferenceRecorderAction.setChecked(true);
                  preferenceRecorderAction.run();

                  if (editorPart.isDirty())
                  {
                    editorPart.doSave(new NullProgressMonitor());
                  }

                  page.getWorkbenchWindow().close();
                }
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
