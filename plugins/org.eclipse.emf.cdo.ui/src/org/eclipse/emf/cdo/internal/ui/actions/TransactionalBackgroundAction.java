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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.ui.actions.LongRunningAction;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public abstract class TransactionalBackgroundAction extends LongRunningAction
{
  private final CDOObject object;

  public TransactionalBackgroundAction(IWorkbenchPage page, String text, String toolTipText, ImageDescriptor image,
      CDOObject object)
  {
    super(page, text, toolTipText, image);
    this.object = object;
  }

  public final CDOObject getObject()
  {
    return object;
  }

  @Override
  protected final void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    progressMonitor.beginTask(Messages.getString("TransactionalBackgroundAction_1"), 100); //$NON-NLS-1$

    CDOView view = object.cdoView();
    CDOSession session = view.getSession();
    CDOTransaction transaction = session.openTransaction(view.getBranch());
    OpenTransactionAction.configureTransaction(transaction);

    CDOObject transactionalObject = transaction.getObject(object);
    progressMonitor.worked(5);

    CDOCommitInfo commitInfo = null;

    try
    {
      doRun(transaction, transactionalObject, new SubProgressMonitor(progressMonitor, 5));
      commitInfo = transaction.commit(new SubProgressMonitor(progressMonitor, 90));
    }
    finally
    {
      progressMonitor.done();
      transaction.close();
      transactionalObject = null;
    }

    if (commitInfo != null)
    {
      view.waitForUpdate(commitInfo.getTimeStamp(), 5000);
      postRun(view, object);
    }
  }

  protected abstract void doRun(CDOTransaction transaction, CDOObject object, IProgressMonitor progressMonitor)
      throws Exception;

  protected void postRun(CDOView view, CDOObject object)
  {
  }
}
