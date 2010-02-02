package org.eclipse.emf.cdo.spi.common.revision;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public class DetachedCDORevision extends SyntheticCDORevision
{
  private int version;

  private long timeStamp;

  public DetachedCDORevision(CDOID id, CDOBranch branch, int version, long timeStamp)
  {
    super(id, branch);
    this.version = version;
    this.timeStamp = timeStamp;
  }

  @Override
  public final int getVersion()
  {
    return version;
  }

  @Override
  public long getTimeStamp()
  {
    return timeStamp;
  }

  @Override
  public long getRevised()
  {
    return UNSPECIFIED_DATE;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("DetachedCDORevision[{0}:{1}v{2}]", getID(), getBranch().getID(), version);
  }
}
