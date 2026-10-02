/*
 * Copyright (c) 2009-2012, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;

import org.eclipse.emf.internal.cdo.transaction.CDOSingleTransactionStrategyImpl;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Internal policy used by a transaction to perform commit, rollback, and savepoint operations. The default strategy
 * delegates these operations to the single-transaction implementation; alternate strategies can control how a
 * transaction is attached to and detached from the transaction machinery.
 *
 * @author Simon McDuff
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOTransactionStrategy
{
  public static final CDOTransactionStrategy DEFAULT = CDOSingleTransactionStrategyImpl.INSTANCE;

  public void setTarget(InternalCDOTransaction transaction);

  public void unsetTarget(InternalCDOTransaction transaction);

  /**
   * TODO Better use an {@link OMMonitor}?
   *
   * @since 3.0
   */
  public CDOCommitInfo commit(InternalCDOTransaction transaction, IProgressMonitor progressMonitor) throws Exception;

  /**
   * @since 3.0
   */
  public void rollback(InternalCDOTransaction transaction, InternalCDOUserSavepoint savepoint);

  /**
   * @since 3.0
   */
  public InternalCDOUserSavepoint setSavepoint(InternalCDOTransaction transaction);
}
