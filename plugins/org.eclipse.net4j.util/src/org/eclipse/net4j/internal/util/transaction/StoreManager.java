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
package org.eclipse.net4j.internal.util.transaction;

import org.eclipse.net4j.internal.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.transaction.IStoreManager;
import org.eclipse.net4j.util.transaction.IStoreTransaction;
import org.eclipse.net4j.util.transaction.StoreException;

/**
 * @author Eike Stepper
 */
public abstract class StoreManager<TRANSACTION extends IStoreTransaction> extends Lifecycle implements
    IStoreManager<TRANSACTION>
{
  private String storeType;

  private String instanceID;

  public StoreManager(String storeType)
  {
    this.storeType = storeType;
  }

  public String getStoreType()
  {
    return storeType;
  }

  public String getInstanceID()
  {
    return instanceID;
  }

  public void setInstanceID(String instanceID)
  {
    this.instanceID = instanceID;
  }

  public void transact(TransactedOperation<TRANSACTION> operation) throws StoreException
  {
    TRANSACTION transaction = startTransaction();
    try
    {
      operation.run(transaction);
      commitTransaction(transaction);
    }
    catch (StoreException ex)
    {
      rollbackTransaction(transaction);
      throw ex;
    }
    catch (Exception ex)
    {
      rollbackTransaction(transaction);
      throw new StoreException(ex);
    }
  }
}
