/*
 * Copyright (c) 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.transaction;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ConcurrentAccessException;

import org.eclipse.emf.common.notify.Notification;

/**
 * @author Eike Stepper
 * @since 4.7
 */
public class CDOAutoCommitter extends CDOPostEventTransactionHandler.Default
{
  public CDOAutoCommitter(CDOTransaction transaction)
  {
    transaction.addTransactionHandler(this);
  }

  @Override
  protected void modifiedObject(CDOTransaction transaction, CDOObject object, Notification msg)
  {
    try
    {
      transaction.commit();
    }
    catch (ConcurrentAccessException ex)
    {
      handleConcurrentAccessException(transaction, ex);
    }
    catch (CommitException ex)
    {
      handleCommitException(transaction, ex);
    }
  }

  protected void handleConcurrentAccessException(CDOTransaction transaction, ConcurrentAccessException ex)
  {
    handleCommitException(transaction, ex);
  }

  protected void handleCommitException(CDOTransaction transaction, CommitException ex)
  {
    rollbackTransaction(transaction);
  }

  protected void rollbackTransaction(CDOTransaction transaction)
  {
    transaction.rollback();
  }
}
