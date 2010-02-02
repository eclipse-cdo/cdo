package org.eclipse.emf.cdo.spi.common.revision;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;

import java.text.MessageFormat;

/**
 * A {@link SyntheticCDORevision synthetic} revision that represents the initial period of an object in a
 * {@link CDOBranch branch} when the object is still associated with a revision from one of the baseline branches. It
 * always has {@link #getVersion() version} {@link CDOBranchVersion#UNSPECIFIED_VERSION zero} and can only appear in
 * branches below the {@link CDOBranch#isMainBranch() main} branch.
 * 
 * @author Eike Stepper
 * @since 3.0
 */
public class PointerCDORevision extends SyntheticCDORevision
{
  private long revised = UNSPECIFIED_DATE;

  private CDOBranchVersion target;

  public PointerCDORevision(CDOID id, CDOBranch branch, long revised, CDOBranchVersion target)
  {
    super(id, branch);
    this.revised = revised;
    this.target = target;
  }

  @Override
  public final int getVersion()
  {
    return UNSPECIFIED_VERSION;
  }

  @Override
  public long getTimeStamp()
  {
    return getBranch().getBase().getTimeStamp();
  }

  @Override
  public long getRevised()
  {
    return revised;
  }

  @Override
  public void setRevised(long revised)
  {
    this.revised = revised;
  }

  public CDOBranchVersion getTarget()
  {
    return target;
  }

  @Override
  public String toString()
  {
    if (target == null)
    {
      return MessageFormat.format("PointerCDORevision[{0}:{1}v0 --> null]", getID(), getBranch().getID());
    }

    return MessageFormat.format("PointerCDORevision[{0}:{1}v0 --> {2}v{3}]", getID(), getBranch().getID(), target
        .getBranch().getID(), target.getVersion());
  }
}
