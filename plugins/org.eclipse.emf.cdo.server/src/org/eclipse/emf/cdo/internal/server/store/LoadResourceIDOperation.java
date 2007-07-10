package org.eclipse.emf.cdo.internal.server.store;

import org.eclipse.emf.cdo.protocol.CDOID;

import org.eclipse.net4j.util.transaction.ITransaction;
import org.eclipse.net4j.util.transaction.ITransactionalOperation;

/**
 * @author Eike Stepper
 */
public abstract class LoadResourceIDOperation<T extends ITransaction> implements ITransactionalOperation<T, CDOID>
{
  private String path;

  public LoadResourceIDOperation(String path)
  {
    this.path = path;
  }

  public CDOID prepare(T transaction) throws Exception
  {
    return query(transaction, path);
  }

  public void onCommit(T transaction)
  {
  }

  public void onRollback(T transaction)
  {
  }

  protected abstract CDOID query(T transaction, String path) throws Exception;
}