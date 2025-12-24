/*
 * Copyright (c) 2011, 2012, 2015, 2016, 2019, 2021, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 226778
 *    Simon McDuff - bug 230832
 *    Simon McDuff - bug 233490
 *    Simon McDuff - bug 213402
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.internal.cdo.session;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransactionContainer;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.transaction.CDOTransactionImpl;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.util.List;

/**
 * @author Eike Stepper
 */
public abstract class CDOTransactionContainerImpl extends CDOViewContainerImpl implements CDOTransactionContainer
{
  public CDOTransactionContainerImpl()
  {
  }

  @Override
  public InternalCDOTransaction getTransaction(int viewID)
  {
    CDOView view = getView(viewID);
    if (view instanceof InternalCDOTransaction)
    {
      return (InternalCDOTransaction)view;
    }

    return null;
  }

  @Override
  public InternalCDOTransaction[] getTransactions()
  {
    return getTransactions(null);
  }

  @Override
  public InternalCDOTransaction[] getTransactions(CDOBranch branch)
  {
    List<InternalCDOView> transactions = getViews(branch, true);
    return transactions.toArray(new InternalCDOTransaction[transactions.size()]);
  }

  @Override
  public InternalCDOTransaction openTransaction(CDOBranchPoint target, ResourceSet resourceSet)
  {
    checkArg(target.getTimeStamp() == CDOBranchPoint.UNSPECIFIED_DATE, "Target is not head of a branch: " + target); //$NON-NLS-1$
    return openTransaction(target.getBranch(), resourceSet);
  }

  @Override
  public InternalCDOTransaction openTransaction(CDOBranchPoint target)
  {
    return openTransaction(target, createResourceSet());
  }

  @Override
  public InternalCDOTransaction openTransaction(CDOBranch branch, ResourceSet resourceSet)
  {
    checkActive();
    InternalCDOTransaction transaction = createTransaction(branch);
    initView(transaction, resourceSet);
    return transaction;
  }

  @Override
  public InternalCDOTransaction openTransaction(ResourceSet resourceSet)
  {
    return openTransaction(getMainBranch(), resourceSet);
  }

  @Override
  public InternalCDOTransaction openTransaction(CDOBranch branch)
  {
    return openTransaction(branch, createResourceSet());
  }

  /**
   * @since 2.0
   */
  @Override
  public InternalCDOTransaction openTransaction()
  {
    return openTransaction(getMainBranch());
  }

  @Override
  public InternalCDOTransaction openTransaction(String durableLockingID)
  {
    return openTransaction(durableLockingID, createResourceSet());
  }

  @Override
  public InternalCDOTransaction openTransaction(String durableLockingID, ResourceSet resourceSet)
  {
    checkActive();
    InternalCDOTransaction transaction = createTransaction(durableLockingID);
    initView(transaction, resourceSet);
    return transaction;
  }

  /**
   * @since 2.0
   */
  protected InternalCDOTransaction createTransaction(CDOBranch branch)
  {
    return TransactionCreator.instance.createTransaction((CDOSession)this, branch);
  }

  /**
   * @since 4.0
   */
  protected InternalCDOTransaction createTransaction(String durableLockingID)
  {
    return TransactionCreator.instance.createTransaction((CDOSession)this, durableLockingID);
  }

  /**
   * @author Eike Stepper
   */
  public static class TransactionCreator
  {
    public static final TransactionCreator DEFAULT = new TransactionCreator();

    private static TransactionCreator instance = DEFAULT;

    public static void set(TransactionCreator creator)
    {
      instance = creator;
    }

    public static void reset()
    {
      instance = DEFAULT;
    }

    public InternalCDOTransaction createTransaction(CDOSession session, CDOBranch branch)
    {
      return new CDOTransactionImpl(session, branch);
    }

    public InternalCDOTransaction createTransaction(CDOSession session, String durableLockingID)
    {
      return new CDOTransactionImpl(session, durableLockingID);
    }
  }
}
