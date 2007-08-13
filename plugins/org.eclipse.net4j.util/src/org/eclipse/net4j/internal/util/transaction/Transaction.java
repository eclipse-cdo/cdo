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
public class Transaction<CONTEXT> implements ITransaction<CONTEXT>
{
  private List<ITransactionalOperation> operations = new ArrayList();

  private CONTEXT context;

  public Transaction(CONTEXT context)
  {
    this.context = context;
  }

  public boolean isActive()
  {
    return operations != null;
  }

  public CONTEXT getContext()
  {
    return context;
  }

  public void execute(ITransactionalOperation<CONTEXT> operation) throws TransactionException
  {
    if (!isActive())
    {
      throw new TransactionException("Transaction inactive");
    }

    try
    {
      operation.phase1(context);
      operations.add(operation);
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

  public void commit()
  {
    for (ITransactionalOperation operation : end())
    {
      operation.phase2(context);
    }
  }

  public void rollback()
  {
    for (ITransactionalOperation operation : end())
    {
      operation.undoPhase1(context);
    }
  }

  private List<ITransactionalOperation> end()
  {
    List<ITransactionalOperation> tmp = operations;
    operations = null;
    return tmp;
  }
}
