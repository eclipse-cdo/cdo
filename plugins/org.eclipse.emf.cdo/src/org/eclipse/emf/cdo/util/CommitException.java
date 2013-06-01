/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.transaction.TransactionException;

/**
 * A checked exception being thrown from {@link CDOTransaction#commit()} in case of commit problems such as commit conflicts.
 * <p>
 * This class is the root of an exception hierarchy that allows to determine and handle specific causes of commit problems:
 *
 * <pre>
    CDOTransaction transaction = session.openTransaction();

    for (;;)
    {
      try
      {
        synchronized (transaction)
        {
          CDOResource resource = transaction.getResource("/stock/resource1");

          // Modify the model here...

          transaction.commit();
          break;
        }
      }
      catch (ConcurrentAccessException ex)
      {
        transaction.rollback();
      }
      catch (CommitException ex)
      {
        throw ex.wrap();
      }
    }
 * </pre>
 *
 * Instances of this class indicate low-level technical problems such as database or network issues.
 *
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class CommitException extends Exception
{
  private static final long serialVersionUID = 1L;

  public CommitException()
  {
  }

  public CommitException(String message)
  {
    super(message);
  }

  public CommitException(Throwable cause)
  {
    super(cause);
  }

  public CommitException(String message, Throwable cause)
  {
    super(message, cause);
  }

  /**
   * @since 4.2
   */
  public boolean isLocal()
  {
    return false;
  }

  /**
   * @since 4.2
   */
  public TransactionException wrap()
  {
    return new TransactionException(this);
  }
}
