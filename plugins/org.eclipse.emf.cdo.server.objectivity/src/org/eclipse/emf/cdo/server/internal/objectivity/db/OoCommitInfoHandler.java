/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Ibrahim Sallam - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.objectivity.db;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.internal.server.Session;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.OoCommitInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OoCommitInfoHandler
{
  protected ooId commitInfoSetId;

  public OoCommitInfoHandler(ooId oid)
  {
    commitInfoSetId = oid;
  }

  /***
   * Factory method to create the CommitInfoList, which is a TreeListX
   */
  public static ooId create(ooId scopeContOid)
  {
    ooTreeSetX treeSet = new ooTreeSetX(20, true);
    ooObj clusterObject = ooObj.create_ooObj(scopeContOid);
    clusterObject.cluster(treeSet);

    return treeSet.getOid();
  }

  /***
   * This function assume we are in an Objy trnasaction.
   */

  public void writeCommitInfo(int id, long timeStamp, String userID, String comment)
  {
    OoCommitInfo commitInfo = new OoCommitInfo(id, timeStamp, userID, comment);
    getTreeSet().add(commitInfo);
  }

  /***
   * Find all objects in the ooTreeListX that's between startTime and endTime inclusive, and have branch.getID() if
   * branch is not null We don't have any optimization for time, but we could make the treeset use custom comparator.
   */
  public List<OoCommitInfo> getCommitInfo(CDOBranch branch, long startTime, long endTime)
  {
    ooTreeSetX treeSet = getTreeSet();
    OoCommitInfo ooCommitInfo = null;
    List<OoCommitInfo> results = new ArrayList<OoCommitInfo>();

    boolean getIt = false;
    Iterator<OoCommitInfo> itr = treeSet.iterator();
    while (itr.hasNext())
    {
      getIt = true;
      long timeStamp = ooCommitInfo.getTimeStamp();
      long branchId = ooCommitInfo.getBranchId();

      if (branch != null && (branch.getID() != branchId))
        getIt = false;
      if (getIt && (startTime != CDOBranchPoint.UNSPECIFIED_DATE) && (timeStamp < startTime))
        getIt = false;
      if (getIt && (endTime != CDOBranchPoint.UNSPECIFIED_DATE) && (timeStamp > endTime))
        getIt = false;

      if (getIt)
      {
        results.add(ooCommitInfo);
      }
    }

    return results;
  }

  /***
   * This function assume we are in an Objy trnasaction.
   */
  protected ooTreeSetX getTreeSet()
  {
    ooTreeSetX treeSet = null;
    treeSet = (ooTreeSetX)Session.getCurrent().getFD().objectFrom(this.commitInfoSetId);
    return treeSet;

  }

}
