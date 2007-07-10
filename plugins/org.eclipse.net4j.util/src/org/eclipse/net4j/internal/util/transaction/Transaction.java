/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.util.transaction;

import org.eclipse.net4j.util.transaction.ITransaction;
import org.eclipse.net4j.util.transaction.ITransactionalOperation;
import org.eclipse.net4j.util.transaction.TransactionException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class Transaction implements ITransaction
{
  private List<ITransactionalOperation> operations = new ArrayList();

  public boolean isActive()
  {
    return operations != null;
  }

  public <R> R execute(ITransactionalOperation<ITransaction, R> operation) throws TransactionException
  {
    if (!isActive())
    {
      throw new TransactionException("Transaction inactive");
    }

    try
    {
      operations.add(operation);
      return operation.prepare(this);
    }
    catch (TransactionException ex)
    {
      rollback();
      throw ex;
    }
    catch (Exception ex)
    {
      rollback();
      throw new TransactionException(ex);
    }
  }

  public void commit() throws TransactionException
  {
    for (ITransactionalOperation operation : end())
    {
      try
      {
        operation.onCommit(this);
      }
      catch (RuntimeException ex)
      {
        throw new TransactionException("Unexpected problem during commit", ex);
      }
    }
  }

  public void rollback() throws TransactionException
  {
    for (ITransactionalOperation operation : end())
    {
      try
      {
        operation.onRollback(this);
      }
      catch (RuntimeException ex)
      {
        throw new TransactionException("Unexpected problem during rollback", ex);
      }
    }
  }

  private List<ITransactionalOperation> end()
  {
    try
    {
      return operations;
    }
    finally
    {
      operations = null;
    }
  }
}
