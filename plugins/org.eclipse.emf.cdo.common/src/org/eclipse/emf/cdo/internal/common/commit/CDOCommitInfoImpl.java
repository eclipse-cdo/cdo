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

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.internal.common.branch.CDOBranchPointImpl;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;

import org.eclipse.net4j.util.CheckUtil;

import java.util.List;

/**
 * @author Eike Stepper
 */
public class CDOCommitInfoImpl extends CDOBranchPointImpl implements CDOCommitInfo
{
  private InternalCDOCommitInfoManager commitInfoManager;

  private String userID;

  private String comment;

  private CDOCommitData commitData;

  public CDOCommitInfoImpl(InternalCDOCommitInfoManager commitInfoManager, CDOBranch branch, long timeStamp,
      String userID, String comment, CDOCommitData commitData)
  {
    super(branch, timeStamp);
    CheckUtil.checkArg(commitInfoManager, "commitInfoManager"); //$NON-NLS-1$
    this.commitInfoManager = commitInfoManager;
    this.userID = userID;
    this.comment = comment;
    this.commitData = commitData;
  }

  public InternalCDOCommitInfoManager getCommitInfoManager()
  {
    return commitInfoManager;
  }

  public String getUserID()
  {
    return userID;
  }

  public String getComment()
  {
    return comment;
  }

  public synchronized List<CDOPackageUnit> getNewPackageUnits()
  {
    return commitData == null ? null : commitData.getNewPackageUnits();
  }

  public synchronized List<CDOIDAndVersion> getNewObjects()
  {
    return commitData == null ? null : commitData.getNewObjects();
  }

  public synchronized List<CDORevisionKey> getChangedObjects()
  {
    return commitData == null ? null : commitData.getChangedObjects();
  }

  public synchronized List<CDOIDAndVersion> getDetachedObjects()
  {
    return commitData == null ? null : commitData.getDetachedObjects();
  }
}
