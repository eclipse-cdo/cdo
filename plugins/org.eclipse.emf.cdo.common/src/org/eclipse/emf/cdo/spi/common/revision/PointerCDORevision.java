/*
 * Copyright (c) 2010-2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.common.revision;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;

import org.eclipse.emf.ecore.EClass;

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

  public PointerCDORevision(EClass eClass, CDOID id, CDOBranch branch, long revised, CDOBranchVersion target)
  {
    super(eClass, id, branch);
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
    String prefix = super.toString() + "->";
    if (target == null)
    {
      return prefix + "?";
    }

    return prefix + target.getBranch().getID() + "v" + target.getVersion();
  }
}
