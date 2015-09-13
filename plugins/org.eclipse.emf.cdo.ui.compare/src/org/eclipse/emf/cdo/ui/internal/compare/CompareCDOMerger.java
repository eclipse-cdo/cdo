/*
 * Copyright (c) 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui.internal.compare;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOChangeSet;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.transaction.CDOCommitContext;
import org.eclipse.emf.cdo.transaction.CDODefaultTransactionHandler2;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionOpener;
import org.eclipse.emf.cdo.ui.compare.CDOCompareEditorUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.transaction.CDOMerger2;

import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.emf.ecore.resource.ResourceSet;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;

import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CompareCDOMerger implements CDOMerger2
{
  public static final String PROP_COMPARISON_LABEL = "comparison.label";

  public static final String PROP_COMPARISON_IMAGE = "comparison.image";

  public CompareCDOMerger()
  {
  }

  @Deprecated
  public CDOChangeSetData merge(CDOChangeSet target, CDOChangeSet source) throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException();
  }

  public void merge(final CDOTransaction localTransaction, CDOView remoteView, Set<CDOID> affectedIDs)
      throws ConflictException
  {
    CompareCDOMerger.closeTransactionAfterCommit(localTransaction);
    CompareCDOMerger.closeEditorWithTransaction(localTransaction);
    CDOCompareEditorUtil.openEditor(remoteView, localTransaction, affectedIDs, true);
  }

  public static void closeTransactionAfterCommit(final CDOTransaction transaction)
  {
    transaction.addTransactionHandler(new CDODefaultTransactionHandler2()
    {
      @Override
      public void rolledBackTransaction(CDOTransaction transaction)
      {
        closeTransaction(transaction);
      }

      @Override
      public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
      {
        closeTransaction(transaction);
      }

      private void closeTransaction(final CDOTransaction transaction)
      {
        UIUtil.getDisplay().asyncExec(new Runnable()
        {
          public void run()
          {
            transaction.close();
          }
        });
      }
    });
  }

  public static void closeEditorWithTransaction(final CDOTransaction transaction)
  {
    final IEditorPart[] result = { null };

    final IWorkbenchPage page = UIUtil.getActiveWorkbenchPage();
    final IPartListener listener = new IPartListener()
    {
      @SuppressWarnings("restriction")
      public void partOpened(IWorkbenchPart part)
      {
        if (part instanceof org.eclipse.compare.internal.CompareEditor)
        {
          result[0] = (IEditorPart)part;
        }
      }

      public void partDeactivated(IWorkbenchPart part)
      {
        // Do nothing.
      }

      public void partClosed(IWorkbenchPart part)
      {
        if (part == result[0])
        {
          transaction.close();
          page.removePartListener(this);
        }
      }

      public void partBroughtToTop(IWorkbenchPart part)
      {
        // Do nothing.
      }

      public void partActivated(IWorkbenchPart part)
      {
        // Do nothing.
      }
    };

    page.addPartListener(listener);

    transaction.addListener(new LifecycleEventAdapter()
    {
      @Override
      protected void onDeactivated(ILifecycle lifecycle)
      {
        if (result[0] != null)
        {
          UIUtil.getDisplay().asyncExec(new Runnable()
          {
            public void run()
            {
              page.closeEditor(result[0], false);
            }
          });
        }
      }
    });
  }

  /**
   * @author Eike Stepper
   */
  public static final class TransactionOpenerAndEditorCloser implements CDOTransactionOpener
  {
    private final CDOTransactionOpener delegate;

    private final boolean closeTransactionAfterCommit;

    public TransactionOpenerAndEditorCloser(CDOTransactionOpener delegate, boolean closeTransactionAfterCommit)
    {
      this.delegate = delegate;
      this.closeTransactionAfterCommit = closeTransactionAfterCommit;
    }

    public boolean isCloseTransactionAfterCommit()
    {
      return closeTransactionAfterCommit;
    }

    public CDOTransaction openTransaction(String durableLockingID, ResourceSet resourceSet)
    {
      return wrap(delegate.openTransaction(durableLockingID, resourceSet));
    }

    public CDOTransaction openTransaction(CDOBranchPoint target, ResourceSet resourceSet)
    {
      return wrap(delegate.openTransaction(target, resourceSet));
    }

    private CDOTransaction wrap(CDOTransaction transaction)
    {
      if (closeTransactionAfterCommit)
      {
        closeTransactionAfterCommit(transaction);
      }

      closeEditorWithTransaction(transaction);
      return transaction;
    }
  }
}
