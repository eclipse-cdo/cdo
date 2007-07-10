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
package org.eclipse.emf.cdo.server;

import org.eclipse.net4j.util.transaction.IStoreManager;
import org.eclipse.net4j.util.transaction.StoreException;
import org.eclipse.net4j.util.transaction.IStoreManager.TransactedOperation;

/**
 * @author Eike Stepper
 */
public final class StoreUtil
{
  private static ThreadLocal<ITransaction> cdotx = new InheritableThreadLocal();

  private StoreUtil()
  {
  }

  public static void transact(IStoreManager<ITransaction> storeManager, final Runnable runnable) throws StoreException
  {
    TransactedOperation operation = new TransactedOperation<ITransaction>()
    {
      public void run(ITransaction transaction) throws Exception
      {
        setTransaction(transaction);
        try
        {
          runnable.run();
        }
        finally
        {
          unsetTransaction();
        }
      }
    };

    storeManager.transact(operation);
  }

  public static ITransaction getTransaction()
  {
    ITransaction transaction = cdotx.get();
    if (transaction == null)
    {
      throw new IllegalStateException("No transaction ongoing");
    }

    return transaction;
  }

  private static void setTransaction(ITransaction transaction)
  {
    if (transaction == null)
    {
      throw new IllegalArgumentException("transaction == null");
    }

    ITransaction existingTransaction = cdotx.get();
    if (existingTransaction != null)
    {
      throw new IllegalStateException("Transaction already ongoing");
    }

    cdotx.set(transaction);
  }

  private static void unsetTransaction()
  {
    ITransaction transaction = cdotx.get();
    if (transaction == null)
    {
      throw new IllegalStateException("No transaction ongoing");
    }

    cdotx.set(null);
  }
}
