package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;

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

  public void handleRevision(CDORevision revision)
  {
    if (revision.getVersion() < CDOBranchVersion.FIRST_VERSION - 1)
    {
      revision = new DetachedCDORevision(revision.getEClass(), revision.getID(), revision.getBranch(), -revision
          .getVersion(), revision.getTimeStamp());
    }

    delegate.handleRevision(revision);
  }
}
