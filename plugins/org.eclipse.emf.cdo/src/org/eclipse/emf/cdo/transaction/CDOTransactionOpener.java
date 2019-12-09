/*
 * Copyright (c) 2015 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;

import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * Can open new {@link CDOTransaction transactions}.
 *
 * @author Eike Stepper
 * @since 4.4
 */
public interface CDOTransactionOpener
{
  /**
   * Opens and returns a new {@link CDOTransaction transaction} on the given EMF {@link ResourceSet resource set}.
   */
  public CDOTransaction openTransaction(CDOBranchPoint target, ResourceSet resourceSet);

  /**
   * Opens and returns a {@link CDOTransaction transaction} on the given EMF {@link ResourceSet resource set} by
   * resuming a transaction that has previously been made durable by calling
   * {@link CDOTransaction#enableDurableLocking(boolean) CDOTransaction.enableDurableLocking(true)}.
   */
  public CDOTransaction openTransaction(String durableLockingID, ResourceSet resourceSet);
}
