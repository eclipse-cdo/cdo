package org.eclipse.emf.cdo.internal.server.store;

import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageImpl;

import org.eclipse.net4j.util.transaction.ITransaction;
import org.eclipse.net4j.util.transaction.ITransactionalOperation;

/**
 * @author Eike Stepper
 */
public abstract class LoadPackageOperation<T extends ITransaction> implements ITransactionalOperation<T, Object>
{
  private CDOPackageImpl cdoPackage;

  public LoadPackageOperation(CDOPackageImpl cdoPackage)
  {
    this.cdoPackage = cdoPackage;
  }

  public Object prepare(T transaction) throws Exception
  {
    queryAndFill(transaction, cdoPackage);
    return null;
  }

  public void onCommit(T transaction)
  {
  }

  public void onRollback(T transaction)
  {
  }

  protected abstract void queryAndFill(T transaction, CDOPackageImpl cdoPackage);
}