/*
 * Copyright (c) 2009, 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.ui.internal.ide.actions;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionCommentator;
import org.eclipse.emf.cdo.ui.internal.ide.bundle.OM;
import org.eclipse.emf.cdo.ui.internal.ide.messages.Messages;

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
public class RemoveResourceActionDelegate implements IObjectActionDelegate
{
  // private Image deleteIcon;

  private List<CDOResourceNode> nodes;

  private Shell shell;

  public RemoveResourceActionDelegate()
  {
  }

  public void setActivePart(IAction action, IWorkbenchPart targetPart)
  {
    shell = targetPart.getSite().getShell();
  }

  public void selectionChanged(IAction action, ISelection selection)
  {
    nodes = UIUtil.getElements(selection, CDOResourceNode.class);
  }

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
          Map<Integer, CDOTransaction> repositoryToTransaction = new HashMap<Integer, CDOTransaction>();
          for (CDOResourceNode node : nodes)
          {
            int sessionID = node.cdoView().getSession().getSessionID();
            CDOTransaction transaction = repositoryToTransaction.get(sessionID);
            if (transaction == null)
            {
              transaction = node.cdoView().getSession().openTransaction();
              new CDOTransactionCommentator(transaction);
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
              OM.LOG.error(MessageFormat.format(
                  Messages.getString("RemoveResourceAction_4"), this.getClass().getName().toString()), ex); //$NON-NLS-1$
            }
            finally
            {
              transaction.close();
            }
          }

          // UIUtil.setStatusBarMessage(
          //              MessageFormat.format(Messages.getString("RemoveResourceAction_5"), nodes.size()), getDeleteIcon()); //$NON-NLS-1$
          return Status.OK_STATUS;
        }
      };

      job.schedule();
    }
  }
}
