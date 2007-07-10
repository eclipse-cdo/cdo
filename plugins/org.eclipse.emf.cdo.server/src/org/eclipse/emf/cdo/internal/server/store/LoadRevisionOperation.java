package org.eclipse.emf.cdo.internal.server.store;

import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.CDOID;

import org.eclipse.net4j.util.transaction.ITransaction;
import org.eclipse.net4j.util.transaction.ITransactionalOperation;

/**
 * @author Eike Stepper
 */
public abstract class LoadRevisionOperation<T extends ITransaction> implements
    ITransactionalOperation<T, CDORevisionImpl>
{
  private CDOID id;

  public LoadRevisionOperation(CDOID id)
  {
    this.id = id;
  }

  public CDORevisionImpl prepare(T transaction) throws Exception
  {
    return query(transaction, id);
  }

  public void onCommit(T transaction)
  {
  }

  public void onRollback(T transaction)
  {
  }

  protected abstract CDORevisionImpl query(T transaction, CDOID id) throws Exception;
}