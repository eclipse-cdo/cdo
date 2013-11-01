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
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.internal.ui.dialogs.SelectBranchPointDialog;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.transaction.CDOMerger;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.spi.cdo.DefaultCDOMerger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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
    CDOTransaction target = (CDOTransaction)getView();
    SelectBranchPointDialog dialog = new SelectBranchPointDialog(getPage(), target.getSession(), target,
        target.isReadOnly())
    {
      @Override
      protected Control createDialogArea(Composite parent)
      {
        getShell().setText(TITLE);
        setTitle(TITLE);
        setTitleImage(SharedIcons.getImage(SharedIcons.WIZBAN_TARGET_SELECTION));
        setMessage("Compose a valid source point or select one from commits, tags or views.");
        return super.createDialogArea(parent);
      }

      @Override
      protected String getComposeTabTitle()
      {
        return "Source Point";
      }
    };

    if (dialog.open() == Dialog.OK)
    {
      source = dialog.getBranchPoint();
      if (source == null)
      {
        cancel();
      }
    }
    else
    {
      cancel();
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
