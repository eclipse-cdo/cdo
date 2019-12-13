/*
 * Copyright (c) 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.internal.ui.actions.delegates;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.internal.ui.actions.OpenTransactionAction;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.AdapterUtil;
import org.eclipse.net4j.util.om.monitor.SubProgressMonitor;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.actions.LongRunningActionDelegate;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import java.text.MessageFormat;

/**
 * @author Victor Roldan Betancort
 */
@Deprecated
public abstract class TransactionalBackgroundActionDelegate extends LongRunningActionDelegate implements IObjectActionDelegate
{
  private IWorkbenchPart targetPart;

  private String text;

  private CDOObject transactionalObject;

  public TransactionalBackgroundActionDelegate(String text)
  {
    this.text = text;
  }

  @Override
  public void setActivePart(IAction action, IWorkbenchPart targetPart)
  {
    this.targetPart = targetPart;
    selectionChanged(action, getSelection());
  }

  public IWorkbenchPart getTargetPart()
  {
    return targetPart;
  }

  @Override
  protected String getText()
  {
    return text;
  }

  protected int getCommitWorkPercent()
  {
    return 90;
  }

  @Override
  protected final void preRun() throws Exception
  {
    Object element = UIUtil.getElement(getSelection());
    CDOObject object = AdapterUtil.adapt(element, CDOObject.class);
    if (object != null)
    {
      transactionalObject = preRun(object);
      if (transactionalObject != null)
      {
        CDOView view = transactionalObject.cdoView();
        if (view.isReadOnly())
        {
          throw new IllegalStateException(MessageFormat.format(Messages.getString("TransactionalBackgroundAction_0"), transactionalObject)); //$NON-NLS-1$
        }

        // Bypass cancel()
        return;
      }
    }

    cancel();
  }

  /**
   * Usually opens a new transaction based on the passed object and its view/session and returns a "contextualized" copy
   * of this object. Clients may override to access the UI thread before the background job is started or change the
   * contextualization procedure.
   *
   * @param object
   *          Usually an object in a read-only view that needs to be modified in a separate transaction.
   * @return A transactional copy of the passed object, or <code>null</code> to indicate cancelation of this action.
   */
  protected CDOObject preRun(CDOObject object)
  {
    CDOSession session = object.cdoView().getSession();
    CDOTransaction transaction = OpenTransactionAction.openTransaction(session);

    CDOObject transactionalObject = transaction.getObject(object);
    return transactionalObject;
  }

  @Override
  protected final void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    CDOTransaction transaction = (CDOTransaction)transactionalObject.cdoView();
    int commitWorkPercent = getCommitWorkPercent();
    progressMonitor.beginTask(Messages.getString("TransactionalBackgroundAction_1"), 100); //$NON-NLS-1$

    try
    {
      doRun(transaction, transactionalObject, new SubProgressMonitor(progressMonitor, 100 - commitWorkPercent));
      transaction.commit(new SubProgressMonitor(progressMonitor, commitWorkPercent));
    }
    finally
    {
      progressMonitor.done();
      transaction.close();
      transactionalObject = null;
    }
  }

  protected abstract void doRun(CDOTransaction transaction, CDOObject object, IProgressMonitor progressMonitor) throws Exception;
}
