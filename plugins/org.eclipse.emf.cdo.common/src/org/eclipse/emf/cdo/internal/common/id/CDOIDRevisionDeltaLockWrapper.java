package org.eclipse.emf.cdo.internal.common.id;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndBranch;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;

/**
 * @author Martin Fluegge
 */
public class CDOIDRevisionDeltaLockWrapper implements CDOIDAndBranch
{
  private Object key;

  private InternalCDORevisionDelta delta;

  public CDOIDRevisionDeltaLockWrapper(Object key, InternalCDORevisionDelta delta)
  {
    this.key = key;
    this.delta = delta;
  }

  public Object getKey()
  {
    return key;
  }

  public InternalCDORevisionDelta getDelta()
  {
    return delta;
  }

  public CDOID getID()
  {
    return key instanceof CDOIDAndBranch ? ((CDOIDAndBranch)key).getID() : (CDOID)key;
  }

  public CDOBranch getBranch()
  {
    return key instanceof CDOIDAndBranch ? ((CDOIDAndBranch)key).getBranch() : null;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof CDOIDRevisionDeltaLockWrapper)
    {
      CDOIDRevisionDeltaLockWrapper wrapper = (CDOIDRevisionDeltaLockWrapper)obj;
      return key.equals(wrapper.getKey());
    }

    return key.equals(obj);
  }

  @Override
  public int hashCode()
  {
    return key.hashCode();
  }

  @Override
  public String toString()
  {
    return key.toString();
  }
}
