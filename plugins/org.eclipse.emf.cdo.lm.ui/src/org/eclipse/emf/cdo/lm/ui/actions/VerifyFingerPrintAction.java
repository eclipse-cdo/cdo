/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.ui.actions;

import org.eclipse.emf.cdo.lm.FixedBaseline;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor;
import org.eclipse.emf.cdo.lm.client.ISystemManager;
import org.eclipse.emf.cdo.lm.ui.bundle.OM;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class VerifyFingerPrintAction extends LMAction<FixedBaseline>
{
  private static final String TITLE = "Verify Fingerprint";

  public VerifyFingerPrintAction(IWorkbenchPage page, StructuredViewer viewer, FixedBaseline fixedBaseline)
  {
    super(page, //
        TITLE + INTERACTIVE, //
        "Verify the fingerprint of baseline '" + fixedBaseline.getName() + "'", //
        OM.getImageDescriptor("icons/FingerPrint.gif"), //
        "Verify the fingerprint of baseline '" + fixedBaseline.getName() + "'.", //
        "icons/wizban/FingerPrint.png", //
        fixedBaseline);
  }

  @Override
  protected boolean isDialogNeeded()
  {
    return false;
  }

  @Override
  protected void fillDialogArea(LMAction<FixedBaseline>.LMDialog dialog, Composite parent)
  {
    // Do nothing.
  }

  @Override
  protected void doRun(FixedBaseline fixedBaseline, IProgressMonitor monitor) throws Exception
  {
    verifyFingerPrint(fixedBaseline, getShell());
  }

  public static void verifyFingerPrint(FixedBaseline fixedBaseline, Shell shell)
  {
    ISystemDescriptor systemDescriptor = ISystemManager.INSTANCE.getDescriptor(fixedBaseline.getSystem());
    boolean valid = systemDescriptor.verifyFingerPrint(fixedBaseline);

    shell.getDisplay().syncExec(() -> {
      if (valid)
      {
        MessageDialog.openInformation(shell, TITLE, "The fingerprint is valid.");
      }
      else
      {
        MessageDialog.openWarning(shell, TITLE, "The  fingerprint is invalid.");
      }
    });
  }
}
