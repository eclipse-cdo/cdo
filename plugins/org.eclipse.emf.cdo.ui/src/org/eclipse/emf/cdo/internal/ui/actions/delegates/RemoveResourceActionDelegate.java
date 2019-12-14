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
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.internal.ui.actions.OpenTransactionAction;
import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Victor Roldan Betancort
 */
@Deprecated
public class RemoveResourceActionDelegate implements IObjectActionDelegate
{
  // private Image deleteIcon;

  private List<CDOResourceNode> nodes;

  private Shell shell;

  public RemoveResourceActionDelegate()
  {
  }

  @Override
  public void setActivePart(IAction action, IWorkbenchPart targetPart)
  {
    shell = targetPart.getSite().getShell();
  }

  @Override
  public void selectionChanged(IAction action, ISelection selection)
  {
    nodes = UIUtil.getElements(selection, CDOResourceNode.class);
  }

  @Override
  public void run(IAction action)
  {
    if (MessageDialog.openConfirm(shell, Messages.getString("RemoveResourceAction_1"), MessageFormat.format( //$NON-NLS-1$
        Messages.getString("RemoveResourceAction_2"), nodes.size()))) //$NON-NLS-1$
    {
      Job job = new Job(Messages.getString("RemoveResourceAction_3")) //$NON-NLS-1$
      {
        @Override
        protected IStatus run(IProgressMonitor monitor)
        {
          Map<Integer, CDOTransaction> repositoryToTransaction = new HashMap<>();
          for (CDOResourceNode node : nodes)
          {
            CDOSession session = node.cdoView().getSession();
            int sessionID = session.getSessionID();
            CDOTransaction transaction = repositoryToTransaction.get(sessionID);
            if (transaction == null)
            {
              transaction = OpenTransactionAction.openTransaction(session);
              repositoryToTransaction.put(sessionID, transaction);
            }

            CDOObject writableNode = transaction.getObject(node.cdoID());
            EObject container = writableNode.eContainer();
            if (container == null)
            {
              container = (CDOResource)writableNode.eResource();
            }

            if (container instanceof CDOResource)
            {
              ((CDOResource)container).getContents().remove(writableNode);
            }
            else if (container instanceof CDOResourceFolder)
            {
              ((CDOResourceFolder)container).getNodes().remove(writableNode);
            }
          }

          for (CDOTransaction transaction : repositoryToTransaction.values())
          {
            try
            {
              transaction.commit();
            }
            catch (Exception ex)
            {
              OM.LOG.error(MessageFormat.format(Messages.getString("RemoveResourceAction_4"), //$NON-NLS-1$
                  this.getClass().getName().toString()), ex);
            }
            finally
            {
              transaction.close();
            }
          }

          // UIUtil.setStatusBarMessage(
          // MessageFormat.format(Messages.getString("RemoveResourceAction_5"), nodes.size()), getDeleteIcon());
          // //$NON-NLS-1$
          return Status.OK_STATUS;
        }
      };

      job.schedule();
    }
  }
}
