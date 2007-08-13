package org.eclipse.emf.cdo.internal.server.store;

import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageImpl;
import org.eclipse.emf.cdo.internal.server.PackageManager;

import org.eclipse.net4j.util.transaction.ITransaction;
import org.eclipse.net4j.util.transaction.ITransactionalOperation;

/**
 * @author Eike Stepper
 */
public abstract class AddPackageOperation<T extends ITransaction> implements ITransactionalOperation<T, Object>
{
  private PackageManager packageManager;

  private CDOPackageImpl cdoPackage;

  public AddPackageOperation(PackageManager packageManager, CDOPackageImpl cdoPackage)
  {
    this.packageManager = packageManager;
    this.cdoPackage = cdoPackage;
  }

  public Object prepare(T transaction) throws Exception
  {
    update(transaction, cdoPackage);
    packageManager.addPackageToCache(cdoPackage);
    return null;
  }

  public void onCommit(T transaction)
  {
  }

  public void onRollback(T transaction)
  {
    packageManager.removePackage(cdoPackage);
  }

  protected abstract void update(T transaction, CDOPackageImpl cdoPackage);
}