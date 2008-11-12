/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo;

import org.eclipse.net4j.util.transaction.TransactionException;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Only deal with transaction process.
 * 
 * @author Simon McDuff
 * @since 2.0
 */
public interface CDOUserTransaction
{
  public void commit() throws TransactionException;

  public void commit(IProgressMonitor progressMonitor) throws TransactionException;

  public void rollback();

  public void rollback(CDOSavepoint savepoint);

  /**
   * Creates a save point in the {@link CDOTransaction} that can be used to roll back a part of the transaction
   * <p>
   * Save points do not involve the server side, everything is done on the client side.
   * <p>
   */
  public CDOSavepoint setSavepoint();

  public CDOSavepoint getLastSavepoint();
}
