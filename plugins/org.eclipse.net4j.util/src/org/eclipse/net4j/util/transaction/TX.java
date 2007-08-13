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
public final class TX
{
  private static ThreadLocal<ITransaction> tx = new InheritableThreadLocal();

  private TX()
  {
  }

  public static void execute(ITransactionalOperation<ITransaction> operation)
  {
    ITransaction transaction = get();
    transaction.execute(operation);
  }

  public static void begin(ITransaction transaction)
  {
    ITransaction existingTransaction = tx.get();
    if (existingTransaction != null)
    {
      throw new IllegalStateException("Transaction already ongoing");
    }

    tx.set(transaction);
  }

  public static void commit()
  {
    ITransaction transaction = get();
    try
    {
      transaction.commit();
    }
    finally
    {
      tx.set(null);
    }
  }

  public static void rollback()
  {
    ITransaction transaction = get();
    try
    {
      transaction.rollback();
    }
    finally
    {
      tx.set(null);
    }
  }

  private static ITransaction get()
  {
    ITransaction transaction = tx.get();
    if (transaction == null)
    {
      throw new IllegalStateException("No transaction ongoing");
    }

    return transaction;
  }
}
