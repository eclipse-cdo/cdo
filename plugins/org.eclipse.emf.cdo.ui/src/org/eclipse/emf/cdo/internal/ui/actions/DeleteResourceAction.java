/*
 * Copyright (c) 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.ui.shared.SharedIcons;
import org.eclipse.net4j.util.ui.actions.SafeAction;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class DeleteResourceAction extends SafeAction
{
  private final Shell shell;

  private final Set<CDOResourceNode> nodes;

  public DeleteResourceAction(Shell shell, List<CDOResourceNode> nodes)
  {
    super(Messages.getString("DeleteResourceAction_1"), Messages.getString("DeleteResourceAction_5"), SharedIcons.getDescriptor(SharedIcons.ETOOL_DELETE));
    this.shell = shell;
    this.nodes = new HashSet<>(nodes);
  }

  @Override
  protected void safeRun() throws Exception
  {
    if (MessageDialog.openConfirm(shell, Messages.getString("DeleteResourceAction_1"), MessageFormat.format( //$NON-NLS-1$
        Messages.getString("DeleteResourceAction_2"), nodes.size()))) //$NON-NLS-1$
    {
      Job job = new Job(Messages.getString("DeleteResourceAction_3")) //$NON-NLS-1$
      {
        @Override
        protected IStatus run(IProgressMonitor monitor)
        {
          Set<CDOResourceNode> nestedNodes = new HashSet<>();
          for (CDOResourceNode node : nodes)
          {
            if (isNested(node))
            {
              nestedNodes.add(node);
            }
          }

          nodes.removeAll(nestedNodes);

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
              OM.LOG.error(MessageFormat.format(Messages.getString("DeleteResourceAction_4"), //$NON-NLS-1$
                  this.getClass().getName().toString()), ex);
            }
            finally
            {
              transaction.close();
            }
          }

          return Status.OK_STATUS;
        }

        private boolean isNested(CDOResourceNode node)
        {
          CDOResourceFolder folder = node.getFolder();
          if (folder != null)
          {
            if (nodes.contains(folder))
            {
              return true;
            }

            return isNested(folder);
          }

          return false;
        }
      };

      job.schedule();
    }
  }
}
