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

import org.eclipse.emf.cdo.lm.Baseline;
import org.eclipse.emf.cdo.lm.FixedBaseline;
import org.eclipse.emf.cdo.lm.client.IAssemblyDescriptor;

import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.handlers.LongRunningHandler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Eike Stepper
 */
public class VerifyFingerPrintHandler extends LongRunningHandler
{
  public VerifyFingerPrintHandler()
  {
  }

  @Override
  protected void doExecute(ExecutionEvent event, IProgressMonitor progressMonitor) throws Exception
  {
    IAssemblyDescriptor descriptor = UIUtil.adaptElement(getSelection(), IAssemblyDescriptor.class);
    if (descriptor != null)
    {
      Baseline baseline = descriptor.getBaseline();
      if (baseline instanceof FixedBaseline)
      {
        FixedBaseline fixedBaseline = (FixedBaseline)baseline;

        Shell shell = HandlerUtil.getActiveShell(event);
        VerifyFingerPrintAction.verifyFingerPrint(fixedBaseline, shell);
      }
    }
  }
}
