package org.eclipse.emf.cdo.spi.common.commit;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public final class CDORevisionAvailabilityInfo
{
  private CDOBranchPoint branchPoint;

  private Set<CDOID> availableRevisions = new HashSet<CDOID>();

  public CDORevisionAvailabilityInfo(CDOBranchPoint branchPoint)
  {
    this.branchPoint = branchPoint;
  }

  public CDOBranchPoint getBranchPoint()
  {
    return branchPoint;
  }

  public Set<CDOID> getAvailableRevisions()
  {
    return availableRevisions;
  }
}
