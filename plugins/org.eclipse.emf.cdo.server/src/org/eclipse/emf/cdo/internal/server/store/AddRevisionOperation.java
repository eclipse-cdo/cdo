package org.eclipse.emf.cdo.internal.server.store;

import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.server.RevisionManager;

import org.eclipse.net4j.util.transaction.ITransaction;
import org.eclipse.net4j.util.transaction.ITransactionalOperation;

/**
 * @author Eike Stepper
 */
public abstract class AddRevisionOperation<T extends ITransaction> implements ITransactionalOperation<T, Object>
{
  private RevisionManager revisionManager;

  private CDORevisionImpl revision;

  public AddRevisionOperation(RevisionManager revisionManager, CDORevisionImpl revision)
  {
    this.revisionManager = revisionManager;
    this.revision = revision;
  }

  public Object prepare(T transaction) throws Exception
  {
    update(transaction, revision);
    revisionManager.addRevisionToCache(revision);
    return null;
  }

  public void onCommit(T transaction)
  {
  }

  public void onRollback(T transaction)
  {
    revisionManager.removeRevision(revision);
  }

  protected abstract void update(T transaction, CDORevisionImpl revision);
}