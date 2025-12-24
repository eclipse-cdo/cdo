/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.ui.actions;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.lm.FixedBaseline;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor;
import org.eclipse.emf.cdo.lm.client.ISystemManager;
import org.eclipse.emf.cdo.lm.ui.bundle.OM;
import org.eclipse.emf.cdo.lm.util.LMOperations;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class AttachFingerPrintAction extends LMAction.NewElement<FixedBaseline>
{
  public AttachFingerPrintAction(IWorkbenchPage page, StructuredViewer viewer, FixedBaseline fixedBaseline)
  {
    super(page, viewer, //
        "Attach Fingerprint" + INTERACTIVE, //
        "Attach a new fingerprint to baseline '" + fixedBaseline.getName() + "'", //
        OM.getImageDescriptor("icons/FingerPrint.gif"), //
        "Attach a new fingerprint to baseline '" + fixedBaseline.getName() + "'.", //
        "icons/wizban/FingerPrint.png", //
        fixedBaseline);
  }

  @Override
  public String getAuthorizableOperationID()
  {
    return LMOperations.ATTACH_FINGERPRINT;
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
  protected CDOObject newElement(FixedBaseline fixedBaseline, IProgressMonitor monitor) throws Exception
  {
    ISystemDescriptor systemDescriptor = ISystemManager.INSTANCE.getDescriptor(fixedBaseline.getSystem());
    return systemDescriptor.attachFingerPrint(fixedBaseline);
  }
}
