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
package org.eclipse.emf.cdo.releng.setup.product;

import org.eclipse.emf.cdo.releng.internal.setup.ui.ProgressLogDialog;
import org.eclipse.emf.cdo.releng.internal.setup.ui.ResourceManager;
import org.eclipse.emf.cdo.releng.setup.Preferences;
import org.eclipse.emf.cdo.releng.setup.rcp.presentation.SetupEditorAdvisor;

import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.common.util.URI;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import java.io.PrintWriter;
import java.io.StringWriter;

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

      for (;;)
      {
        SetupDialog dialog = new SetupDialog(null);
        dialog.open();
        if (dialog.getReturnCode() != -2)
        {
          break;
        }

        display.asyncExec(new Runnable()
        {
          public void run()
          {
            IWorkbench workbench = PlatformUI.getWorkbench();
            IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
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
                page.openEditor(editorInput, "org.eclipse.emf.cdo.releng.setup.rcp.presentation.SetupEditorID");
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

      return 0;
    }
    catch (final Throwable ex)
    {
      Activator.log(ex);
      new InternalErrorDialog(ex).open();
      return 1;
    }
  }

  public void stop()
  {
  }

  private static class InternalErrorDialog extends MessageDialog
  {
    private static int DETAILS_BUTTON_ID = 1;

    private static final int TEXT_LINE_COUNT = 15;

    private Throwable throwable;

    private Text text;

    public InternalErrorDialog(Throwable detail)
    {
      super(null, ProgressLogDialog.TITLE, ResourceManager.getPluginImage("org.eclipse.emf.cdo.releng.setup",
          "icons/install_wiz.gif"), "Internal error" + System.getProperty("line.separator") + detail.getMessage(),
          MessageDialog.ERROR, new String[] { IDialogConstants.OK_LABEL, IDialogConstants.SHOW_DETAILS_LABEL }, 0);
      throwable = detail;
      setShellStyle(getShellStyle() | SWT.APPLICATION_MODAL);
    }

    @Override
    protected void buttonPressed(int buttonId)
    {
      if (buttonId == DETAILS_BUTTON_ID)
      {
        toggleDetailsArea();
      }
      else
      {
        super.buttonPressed(buttonId);
      }
    }

    private void toggleDetailsArea()
    {
      Point windowSize = getShell().getSize();
      Point oldSize = getContents().computeSize(SWT.DEFAULT, SWT.DEFAULT);

      if (text != null)
      {
        text.dispose();
        text = null;
        getButton(DETAILS_BUTTON_ID).setText(IDialogConstants.SHOW_DETAILS_LABEL);
      }
      else
      {
        createDropDownText((Composite)getContents());
        getButton(DETAILS_BUTTON_ID).setText(IDialogConstants.HIDE_DETAILS_LABEL);
      }

      Point newSize = getContents().computeSize(SWT.DEFAULT, SWT.DEFAULT);
      getShell().setSize(new Point(windowSize.x, windowSize.y + newSize.y - oldSize.y));
    }

    protected void createDropDownText(Composite parent)
    {
      text = new Text(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
      text.setFont(parent.getFont());

      StringWriter stringWriter = new StringWriter();
      PrintWriter out = new PrintWriter(stringWriter);
      throwable.printStackTrace(out);
      out.close();
      text.setText(stringWriter.toString());

      GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL
          | GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_VERTICAL);
      data.heightHint = text.getLineHeight() * TEXT_LINE_COUNT;
      data.horizontalSpan = 2;
      text.setLayoutData(data);
    }
  }
}
