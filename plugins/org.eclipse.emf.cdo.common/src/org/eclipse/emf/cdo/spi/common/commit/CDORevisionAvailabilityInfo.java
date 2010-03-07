package org.eclipse.emf.cdo.spi.common.commit;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public final class CDORevisionAvailabilityInfo implements CDORevisionProvider
{
  private CDOBranchPoint branchPoint;

  private Map<CDOID, CDORevisionKey> availableRevisions = new HashMap<CDOID, CDORevisionKey>();

  public CDORevisionAvailabilityInfo(CDOBranchPoint branchPoint)
  {
    this.branchPoint = branchPoint;
  }

  public CDOBranchPoint getBranchPoint()
  {
    return branchPoint;
  }

  public Map<CDOID, CDORevisionKey> getAvailableRevisions()
  {
    return availableRevisions;
  }

  public void addRevision(CDORevisionKey key)
  {
    availableRevisions.put(key.getID(), key);
  }

  public void removeRevision(CDOID id)
  {
    availableRevisions.remove(id);
  }

  public boolean containsRevision(CDOID id)
  {
    return availableRevisions.containsKey(id);
  }

  public CDORevision getRevision(CDOID id)
  {
    return (CDORevision)availableRevisions.get(id);
  }
}
