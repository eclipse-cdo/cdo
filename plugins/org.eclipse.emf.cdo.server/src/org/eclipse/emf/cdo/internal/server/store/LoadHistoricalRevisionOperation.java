package org.eclipse.emf.cdo.internal.server.store;

import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.CDOID;

import org.eclipse.net4j.util.transaction.ITransaction;
import org.eclipse.net4j.util.transaction.ITransactionalOperation;

/**
 * @author Eike Stepper
 */
public abstract class LoadHistoricalRevisionOperation<T extends ITransaction> implements
    ITransactionalOperation<T, CDORevisionImpl>
{
  private CDOID id;

  private long timeStamp;

  public LoadHistoricalRevisionOperation(CDOID id, long timeStamp)
  {
    this.id = id;
    this.timeStamp = timeStamp;
  }

  public CDORevisionImpl prepare(T transaction) throws Exception
  {
    return query(transaction, id, timeStamp);
  }

  public void onCommit(T transaction)
  {
  }

  public void onRollback(T transaction)
  {
  }

  protected abstract CDORevisionImpl query(T transaction, CDOID id, long timeStamp) throws Exception;
}