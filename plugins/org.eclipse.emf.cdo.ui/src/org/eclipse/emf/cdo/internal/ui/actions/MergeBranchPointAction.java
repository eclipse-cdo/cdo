/*
 * Copyright (c) 2013, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.internal.ui.dialogs.AbstractBranchPointDialog;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.transaction.CDOMerger;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.spi.cdo.DefaultCDOMerger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class MergeBranchPointAction extends AbstractViewAction
{
  public static final String ID = "merge"; //$NON-NLS-1$

  private static final String TITLE = Messages.getString("MergeBranchPointAction.0"); //$NON-NLS-1$

  private static final String TOOL_TIP = Messages.getString("MergeBranchPointAction.1"); //$NON-NLS-1$

  private CDOBranchPoint source;

  public MergeBranchPointAction(IWorkbenchPage page, CDOView view)
  {
    super(page, TITLE + INTERACTIVE, TOOL_TIP, null, view);
    setId(ID);
  }

  @Override
  protected void preRun() throws Exception
  {
    Shell shell = getShell();
    CDOTransaction target = (CDOTransaction)getView();

    source = AbstractBranchPointDialog.select(shell, true, target);
    if (source == null)
    {
      cancel();
      return;
    }

    super.preRun();
  }

  @Override
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    CDOMerger merger = new DefaultCDOMerger.PerFeature.ManyValued();

    CDOTransaction target = (CDOTransaction)getView();
    target.merge(source, merger);
  }
}
