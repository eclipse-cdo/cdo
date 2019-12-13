/*
 * Copyright (c) 2011-2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public final class EnableViewDurabilityAction extends AbstractViewAction
{
  private static final String TITLE = Messages.getString("EnableViewDurabilityAction.0"); //$NON-NLS-1$

  private static final String TOOL_TIP = Messages.getString("EnableViewDurabilityAction.1"); //$NON-NLS-1$

  public EnableViewDurabilityAction(IWorkbenchPage page, CDOView view)
  {
    super(page, TITLE, TOOL_TIP, null, view);
  }

  @Override
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    final String durableLockingID = getView().enableDurableLocking();

    final Display display = getDisplay();
    display.syncExec(new Runnable()
    {
      @Override
      public void run()
      {
        UIUtil.copyToClipboard(display, durableLockingID);
        MessageDialog.openInformation(getShell(), TITLE, "The following area ID has been copied to the clipboard:\n" + durableLockingID);
      }
    });
  }
}
