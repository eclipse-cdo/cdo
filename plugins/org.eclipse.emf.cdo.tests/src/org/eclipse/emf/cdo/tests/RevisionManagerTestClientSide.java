package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

/**
 * @author Eike Stepper
 */
public class RevisionManagerTestClientSide extends RevisionManagerTest
{
  @Override
  protected InternalCDORevisionManager getRevisionManager(InternalRepository repository, InternalCDOSession session)
  {
    return session.getRevisionManager();
  }

  @Override
  protected String getLocation()
  {
    return "Client";
  }

  @Override
  protected void dumpCache(CDOBranchPoint branchPoint)
  {
    BranchingTest.dump("ServerCache", repository.getRevisionManager().getCache().getAllRevisions());
    super.dumpCache(branchPoint);
  }
}
