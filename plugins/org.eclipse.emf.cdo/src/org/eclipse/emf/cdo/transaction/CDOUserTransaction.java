/*
 * Copyright (c) 2009-2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.transaction;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ConcurrentAccessException;
import org.eclipse.emf.cdo.util.ContainmentCycleException;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Provides functionality that is common to both {@link CDOTransaction single} transactions and {@link CDOXATransaction
 * distributed} (XA) transactions.
 *
 * @author Simon McDuff
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOUserTransaction
{
  /**
   * Same as {@link #commit(IProgressMonitor) commit(null)}.
   *
   * @since 3.0
   */
  public CDOCommitInfo commit() throws ConcurrentAccessException, CommitException;

  /**
   * Commits the modifications of this transaction to the repository and returns a {@link CDOCommitInfo commit info} object if successful.
   * <p>
   * Various kinds of problems <b>can</b> cause the commit to fail and not all of them can be avoided by acquiring pessimistic {@link CDOObject#cdoWriteLock() locks}
   * on the modified objects. In particular you <b>must</b> expect and handle {@link ContainmentCycleException containment cycle exceptions}. The following example shows how
   * write robust transactions:
   * <pre>
    CDOTransaction transaction = null;

    try
    {
      transaction = session.openTransaction();

      for (;;)
      {
        transaction.getViewLock().lock();

        try
        {
          CDOResource resource = transaction.getResource("/stock/resource1");

          // Modify the model here...

          transaction.commit();
          break;
        }
        catch (ConcurrentAccessException ex)
        {
          transaction.rollback();
        }
        catch (CommitException ex)
        {
          throw ex.wrap();
        }
        finally
        {
          transaction.getViewLock().unlock();
        }
      }
    }
    finally
    {
      if (transaction != null)
      {
        transaction.close();
      }
    }
   * </pre>
   *
   * Note that the transaction stays functional after a any call to the <code>commit()</code> methods. If the transaction is not closed after a commit
   * it can be used to apply additional modifications to the model.
   *
   * @since 3.0
   */
  public CDOCommitInfo commit(IProgressMonitor monitor) throws ConcurrentAccessException, CommitException;

  public void rollback();

  /**
   * Creates a save point in the {@link CDOTransaction} that can be used to roll back a part of the transaction
   * <p>
   * Save points do not involve the server side, everything is done on the client side.
   * <p>
   *
   * @since 3.0
   */
  public CDOUserSavepoint setSavepoint();

  /**
   * @since 3.0
   */
  public CDOUserSavepoint getLastSavepoint();
}
