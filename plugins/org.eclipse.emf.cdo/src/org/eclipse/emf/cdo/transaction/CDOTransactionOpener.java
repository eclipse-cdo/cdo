/*
 * Copyright (c) 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
