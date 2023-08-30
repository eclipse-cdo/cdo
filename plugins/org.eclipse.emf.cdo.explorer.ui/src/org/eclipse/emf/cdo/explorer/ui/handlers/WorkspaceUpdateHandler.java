/*
 * Copyright (c) 2015, 2016, 2019, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.handlers;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.util.Support;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.explorer.checkouts.OfflineCDOCheckout;
import org.eclipse.emf.cdo.transaction.CDOCommitContext;
import org.eclipse.emf.cdo.transaction.CDODefaultTransactionHandler2;
import org.eclipse.emf.cdo.transaction.CDOMerger.ConflictException;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.ui.compare.CDOCompareEditorUtil;
import org.eclipse.emf.cdo.ui.internal.compare.CompareCDOMerger;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ConcurrentAccessException;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.workspace.CDOWorkspace;

import org.eclipse.net4j.util.registry.IRegistry;
import org.eclipse.net4j.util.ui.handlers.AbstractBaseHandler;

import org.eclipse.emf.spi.cdo.DefaultCDOMerger;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;

import java.util.Set;

/**
 * @author Eike Stepper
 */
public class WorkspaceUpdateHandler extends AbstractBaseHandler<OfflineCDOCheckout>
{
  public WorkspaceUpdateHandler()
  {
    super(OfflineCDOCheckout.class, false);
  }

  @Override
  protected void doExecute(ExecutionEvent event, IProgressMonitor monitor) throws Exception
  {
    OfflineCDOCheckout checkout = elements.get(0);

    CDOWorkspace workspace = checkout.getWorkspace();
    if (workspace != null)
    {
      try
      {
        mergeDefault(checkout, workspace, monitor);
      }
      catch (ConflictException ex)
      {
        if (Support.UI_COMPARE.isAvailable())
        {
          mergeCompare(checkout, workspace);
        }
        else
        {
          throw ex;
        }
      }
    }
  }

  private void mergeCompare(final OfflineCDOCheckout checkout, final CDOWorkspace workspace)
  {
    CDOCompareEditorUtil.runWithTitle("Update " + checkout.getLabel(), new Runnable()
    {
      @Override
      public void run()
      {
        CDOTransaction transaction = workspace.update(new CompareCDOMerger()
        {
          @Override
          public void merge(CDOTransaction localTransaction, CDOView remoteView, Set<CDOID> affectedIDs) throws ConflictException
          {
            IRegistry<String, Object> remoteProperties = remoteView.properties();
            remoteProperties.put(CDOCompareEditorUtil.PROP_COMPARISON_IMAGE, OM.getImage("icons/repository.gif"));
            remoteProperties.put(CDOCompareEditorUtil.PROP_COMPARISON_LABEL, "From remote");

            IRegistry<String, Object> localProperties = localTransaction.properties();
            localProperties.put(CDOCompareEditorUtil.PROP_COMPARISON_IMAGE, OM.getImage("icons/checkout.gif"));
            localProperties.put(CDOCompareEditorUtil.PROP_COMPARISON_LABEL, "To local");

            super.merge(localTransaction, remoteView, affectedIDs);
          }
        });

        transaction.addTransactionHandler(new CDODefaultTransactionHandler2()
        {
          @Override
          public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
          {
            checkout.refresh();
          }
        });
      }
    });
  }

  private void mergeDefault(OfflineCDOCheckout checkout, CDOWorkspace workspace, IProgressMonitor monitor) throws ConcurrentAccessException, CommitException
  {
    CDOTransaction transaction = workspace.update(new DefaultCDOMerger.PerFeature.ManyValued());
    transaction.commit(monitor);
    transaction.close();

    checkout.refresh();
  }
}
