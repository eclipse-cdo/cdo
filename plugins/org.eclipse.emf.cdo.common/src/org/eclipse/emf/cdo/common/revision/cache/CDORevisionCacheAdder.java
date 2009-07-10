package org.eclipse.emf.cdo.common.revision.cache;

import org.eclipse.emf.cdo.common.revision.CDORevision;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public interface CDORevisionCacheAdder
{
  public boolean addRevision(CDORevision revision);
}
