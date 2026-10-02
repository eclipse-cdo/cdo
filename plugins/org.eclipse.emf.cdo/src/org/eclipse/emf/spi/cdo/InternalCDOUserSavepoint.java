/*
 * Copyright (c) 2009-2012, 2019, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.transaction.CDOUserSavepoint;

/**
 * Internal node in a user transaction's savepoint chain. The transaction uses the links to restore or traverse retained
 * savepoints while preserving the public {@link CDOUserSavepoint} view of the same state.
 *
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalCDOUserSavepoint extends CDOUserSavepoint
{
  @Override
  public InternalCDOUserTransaction getTransaction();

  public InternalCDOUserSavepoint getFirstSavePoint();

  @Override
  public InternalCDOUserSavepoint getPreviousSavepoint();

  @Override
  public InternalCDOUserSavepoint getNextSavepoint();

  public void setPreviousSavepoint(InternalCDOUserSavepoint previousSavepoint);

  public void setNextSavepoint(InternalCDOUserSavepoint nextSavepoint);
}
