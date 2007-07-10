package org.eclipse.emf.cdo.internal.server.store;

import org.eclipse.net4j.util.transaction.ITransaction;
import org.eclipse.net4j.util.transaction.ITransactionalOperation;

/**
 * @author Eike Stepper
 */
public abstract class AbstractQueryOperation<T extends ITransaction, RESULT, PARAMETER> implements
    ITransactionalOperation<T, RESULT>
{
  private PARAMETER parameter;

  public AbstractQueryOperation(PARAMETER parameter)
  {
    this.parameter = parameter;
  }

  public RESULT prepare(T transaction) throws Exception
  {
    return query(transaction, parameter);
  }

  public void onCommit(T transaction)
  {
  }

  public void onRollback(T transaction)
  {
  }

  protected abstract RESULT query(T transaction, PARAMETER parameter);
}