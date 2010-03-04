/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Simon McDuff - bug 212958
 *    Simon McDuff - bug 213402
 */
package org.eclipse.emf.cdo.spi.common.revision;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.model.CDOClassInfo;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;

import org.eclipse.net4j.util.ObjectUtil;

import org.eclipse.emf.ecore.EClass;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class AbstractCDORevision implements InternalCDORevision
{
  public EClass getEClass()
  {
    CDOClassInfo classInfo = getClassInfo();
    if (classInfo != null)
    {
      return classInfo.getEClass();
    }

    return null;
  }

  public boolean isResourceNode()
  {
    return getClassInfo().isResourceNode();
  }

  public boolean isResourceFolder()
  {
    return getClassInfo().isResourceFolder();
  }

  public boolean isResource()
  {
    return getClassInfo().isResource();
  }

  public CDORevisionData data()
  {
    return this;
  }

  public CDORevision revision()
  {
    return this;
  }

  /**
   * @since 3.0
   */
  public boolean isHistorical()
  {
    return getRevised() != UNSPECIFIED_DATE;
  }

  public boolean isValid(long timeStamp)
  {
    long startTime = getTimeStamp();
    long endTime = getRevised();
    return CDOCommonUtil.isValidTimeStamp(timeStamp, startTime, endTime);
  }

  /**
   * @since 3.0
   */
  public void adjustForCommit(CDOBranch branch, long timeStamp)
  {
    if (ObjectUtil.equals(branch, getBranch()))
    {
      // Same branch, increase version
      setVersion(getVersion() + 1);
    }
    else
    {
      // Different branch, start with v1
      setVersion(FIRST_VERSION);
    }

    setBranchPoint(branch.getPoint(timeStamp));
    setRevised(UNSPECIFIED_DATE);
  }

  @Override
  public String toString()
  {
    EClass eClass = getEClass();
    String name = eClass == null ? "Revision" : eClass.getName();

    CDOBranch branch = getBranch();
    if (branch == null)
    {
      return name + "@" + getID() + "v" + getVersion();
    }

    return name + "@" + getID() + ":" + branch.getID() + "v" + getVersion();
  }
}
