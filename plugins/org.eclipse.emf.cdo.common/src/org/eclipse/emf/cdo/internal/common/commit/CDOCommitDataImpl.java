/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.commit;

import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;

import java.util.Collection;

/**
 * @author Eike Stepper
 */
public class CDOCommitDataImpl implements CDOCommitData
{
  private Collection<CDOPackageUnit> newPackageUnits;

  private Collection<CDOIDAndVersion> newObjects;

  private Collection<CDORevisionKey> changedObjects;

  private Collection<CDOIDAndVersion> detachedObjects;

  public CDOCommitDataImpl(Collection<CDOPackageUnit> newPackageUnits, Collection<CDOIDAndVersion> newObjects,
      Collection<CDORevisionKey> changedObjects, Collection<CDOIDAndVersion> detachedObjects)
  {
    this.newPackageUnits = newPackageUnits;
    this.newObjects = newObjects;
    this.changedObjects = changedObjects;
    this.detachedObjects = detachedObjects;
  }

  public Collection<CDOPackageUnit> getNewPackageUnits()
  {
    return newPackageUnits;
  }

  public Collection<CDOIDAndVersion> getNewObjects()
  {
    return newObjects;
  }

  public Collection<CDORevisionKey> getChangedObjects()
  {
    return changedObjects;
  }

  public Collection<CDOIDAndVersion> getDetachedObjects()
  {
    return detachedObjects;
  }
}
