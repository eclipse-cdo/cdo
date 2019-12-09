/*
 * Copyright (c) 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 *    Victor Roldan Betancort - maintenance
 *    Andre Dietisheim - bug 256649
 */
package org.eclipse.emf.cdo.transaction;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.view.CDOViewContainer;

import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * Can open new {@link CDOTransaction transactions} and provide access to openend transactions.
 *
 * @author Eike Stepper
 * @since 4.1
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOTransactionContainer extends CDOViewContainer, CDOTransactionOpener
{
  /**
   * Returns an array of all open {@link CDOTransaction transactions} of this session.
   *
   * @see #openView()
   * @see #openTransaction()
   */
  public CDOTransaction[] getTransactions();

  /**
   * Returns an array of all {@link CDOTransaction transactions} of this session that are open on the given branch.
   *
   * @since 4.2
   */
  public CDOTransaction[] getTransactions(CDOBranch branch);

  /**
   * @since 4.1
   */
  public CDOTransaction getTransaction(int viewID);

  /**
   * Opens and returns a new {@link CDOTransaction transaction} on a new EMF {@link ResourceSet resource set}.
   *
   * @see #openTransaction()
   * @since 4.0
   */
  public CDOTransaction openTransaction(CDOBranchPoint target);

  /**
   * Opens and returns a new {@link CDOTransaction transaction} on the given EMF {@link ResourceSet resource set}.
   *
   * @see #openTransaction()
   * @since 3.0
   */
  public CDOTransaction openTransaction(CDOBranch branch, ResourceSet resourceSet);

  /**
   * Opens and returns a new {@link CDOTransaction transaction} on the given EMF {@link ResourceSet resource set}.
   *
   * @see #openTransaction()
   * @since 3.0
   */
  public CDOTransaction openTransaction(ResourceSet resourceSet);

  /**
   * Opens and returns a new {@link CDOTransaction transaction} on a new EMF {@link ResourceSet resource set}.
   * <p>
   * Same as calling <code>openTransaction(new ResourceSetImpl())</code>.
   *
   * @see #openTransaction(ResourceSet)
   * @since 3.0
   */
  public CDOTransaction openTransaction(CDOBranch branch);

  /**
   * Opens and returns a new {@link CDOTransaction transaction} on a new EMF {@link ResourceSet resource set}.
   * <p>
   * Same as calling <code>openTransaction(new ResourceSetImpl())</code>.
   *
   * @see #openTransaction(ResourceSet)
   */
  public CDOTransaction openTransaction();

  /**
   * Opens and returns a {@link CDOTransaction transaction} on a new EMF {@link ResourceSet resource set} by resuming a
   * transaction that has previously been made durable by calling {@link CDOTransaction#enableDurableLocking(boolean)
   * CDOTransaction.enableDurableLocking(true)}.
   * <p>
   * Same as calling <code>openTransaction(durableLockingID, new ResourceSetImpl())</code>.
   *
   * @see #openTransaction(String,ResourceSet)
   * @since 4.0
   */
  public CDOTransaction openTransaction(String durableLockingID);
}
