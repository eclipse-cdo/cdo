package org.eclipse.emf.cdo.spi.common;

import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public interface CDOCloningContext
{
  public void addPackageUnit(String id);

  public void addBranch(int id);

  public void addRevision(InternalCDORevision revision);
}
