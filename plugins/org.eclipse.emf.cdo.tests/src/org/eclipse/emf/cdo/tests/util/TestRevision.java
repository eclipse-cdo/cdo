/*
 * Copyright (c) 2011-2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.util;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.internal.common.branch.CDOBranchImpl;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.StubCDORevision;

import org.eclipse.emf.ecore.EClass;

/**
 * @author Eike Stepper
 */
public final class TestRevision extends StubCDORevision
{
  private CDOID id;

  private CDOBranchPoint branchPoint;

  private int version;

  private long revised;

  public TestRevision(EClass eClass, long id, int version, long created, long revised)
  {
    super(eClass);
    this.id = CDOIDUtil.createLong(id);
    branchPoint = new CDOBranchImpl(null, CDOBranch.MAIN_BRANCH_ID, CDOBranch.MAIN_BRANCH_NAME, null).getPoint(created);
    this.version = version;
    this.revised = revised;
  }

  public TestRevision(EClass eClass, long id, int version, long created)
  {
    this(eClass, id, version, created, CDORevision.UNSPECIFIED_DATE);
  }

  public TestRevision(EClass eClass, long id)
  {
    this(eClass, id, 0, CDORevision.UNSPECIFIED_DATE);
  }

  @Override
  public CDOID getID()
  {
    return id;
  }

  @Override
  public void setID(CDOID id)
  {
    this.id = id;
  }

  @Override
  public InternalCDOBranch getBranch()
  {
    return (InternalCDOBranch)branchPoint.getBranch();
  }

  @Override
  public long getTimeStamp()
  {
    return branchPoint.getTimeStamp();
  }

  @Override
  public void setBranchPoint(CDOBranchPoint branchPoint)
  {
    this.branchPoint = CDOBranchUtil.copyBranchPoint(branchPoint);
  }

  @Override
  public int getVersion()
  {
    return version;
  }

  @Override
  public void setVersion(int version)
  {
    this.version = version;
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

  @Override
  public InternalCDORevision copy()
  {
    return new TestRevision(getEClass(), CDOIDUtil.getLong(id), version, branchPoint.getTimeStamp(), revised);
  }
}
