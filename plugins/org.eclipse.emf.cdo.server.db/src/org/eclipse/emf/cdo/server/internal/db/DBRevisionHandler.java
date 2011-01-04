package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;

/**
 * @author Eike Stepper
 */
public class DBRevisionHandler implements CDORevisionHandler
{
  private CDORevisionHandler delegate;

  public DBRevisionHandler(CDORevisionHandler delegate)
  {
    this.delegate = delegate;
  }

  public boolean handleRevision(CDORevision revision)
  {
    return delegate.handleRevision(revision);
  }
}
