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

import java.util.List;

/**
 * @author Eike Stepper
 */
public class CDOCommitDataImpl extends CDOChangeSetDataImpl implements CDOCommitData
{
  private List<CDOPackageUnit> newPackageUnits;

  public CDOCommitDataImpl(List<CDOPackageUnit> newPackageUnits, List<CDOIDAndVersion> newObjects,
      List<CDORevisionKey> changedObjects, List<CDOIDAndVersion> detachedObjects)
  {
    super(newObjects, changedObjects, detachedObjects);
    this.newPackageUnits = newPackageUnits;
  }

  public List<CDOPackageUnit> getNewPackageUnits()
  {
    return newPackageUnits;
  }
}
