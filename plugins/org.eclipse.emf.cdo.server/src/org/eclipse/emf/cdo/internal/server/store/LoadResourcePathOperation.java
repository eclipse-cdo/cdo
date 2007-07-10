package org.eclipse.emf.cdo.internal.server.store;

import org.eclipse.emf.cdo.protocol.CDOID;

import org.eclipse.net4j.util.transaction.ITransaction;
import org.eclipse.net4j.util.transaction.ITransactionalOperation;

/**
 * @author Eike Stepper
 */
public abstract class LoadResourcePathOperation<T extends ITransaction> implements ITransactionalOperation<T, String>
{
  private CDOID id;

  public LoadResourcePathOperation(CDOID id)
  {
    this.id = id;
  }

  public String prepare(T transaction) throws Exception
  {
    return query(transaction, id);
  }

  public void onCommit(T transaction)
  {
  }

  public void onRollback(T transaction)
  {
  }

  protected abstract String query(T transaction, CDOID id) throws Exception;
}