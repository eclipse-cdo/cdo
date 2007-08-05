package org.eclipse.emf.cdo.internal.server.store;

import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOClassRef;

import org.eclipse.net4j.util.transaction.ITransaction;
import org.eclipse.net4j.util.transaction.ITransactionalOperation;

/**
 * @author Eike Stepper
 */
public abstract class QueryObjectTypeOperation<T extends ITransaction> implements
    ITransactionalOperation<T, CDOClassRef>
{
  private CDOID id;

  public QueryObjectTypeOperation(CDOID id)
  {
    this.id = id;
  }

  public CDOClassRef prepare(T transaction) throws Exception
  {
    return query(transaction, id);
  }

  public void onCommit(T transaction)
  {
  }

  public void onRollback(T transaction)
  {
  }

  protected abstract CDOClassRef query(T transaction, CDOID id) throws Exception;
}