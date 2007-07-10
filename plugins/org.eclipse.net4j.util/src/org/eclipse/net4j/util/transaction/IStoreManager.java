/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.transaction;

/**
 * @author Eike Stepper
 */
public interface IStoreManager<TRANSACTION extends IStoreTransaction>
{
  public String getStoreType();

  public String getInstanceID();

  public TRANSACTION startTransaction() throws StoreException;

  public void commitTransaction(TRANSACTION transaction) throws StoreException;

  public void rollbackTransaction(TRANSACTION transaction) throws StoreException;

  public void transact(TransactedOperation<TRANSACTION> operation) throws StoreException;

  /**
   * @author Eike Stepper
   */
  public interface TransactedOperation<TRANSACTION extends IStoreTransaction>
  {
    public void run(TRANSACTION transaction) throws Exception;
  }
}
