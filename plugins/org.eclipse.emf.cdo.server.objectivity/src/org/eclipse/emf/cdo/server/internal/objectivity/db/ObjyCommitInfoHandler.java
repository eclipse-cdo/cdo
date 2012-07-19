/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyCommitInfo;
import org.eclipse.emf.cdo.server.internal.objectivity.utils.ObjyDb;

import com.objy.db.app.Session;
import com.objy.db.app.ooId;
import com.objy.db.app.ooObj;
import com.objy.db.util.ooTreeSetX;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ObjyCommitInfoHandler
{
  protected ooId commitInfoSetId;

  public ObjyCommitInfoHandler(String repositoryName)
  {
    commitInfoSetId = ObjyDb.getOrCreateCommitInfoList(repositoryName);
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

  public void writeCommitInfo(int id, long timeStamp, long previousTimeStamp, String userID, String comment)
  {
    ObjyCommitInfo commitInfo = new ObjyCommitInfo(id, timeStamp, previousTimeStamp, userID, comment);
    getTreeSet().add(commitInfo);
  }

  /***
   * Find all objects in the ooTreeListX that's between startTime and endTime inclusive, and have branch.getID() if
   * branch is not null We don't have any optimization for time, but we could make the treeset use custom comparator.
   */
  public List<ObjyCommitInfo> getCommitInfo(CDOBranch branch, long startTime, long endTime)
  {
    ooTreeSetX treeSet = getTreeSet();
    ObjyCommitInfo ooCommitInfo = null;
    List<ObjyCommitInfo> results = new ArrayList<ObjyCommitInfo>();

    boolean getIt = false;

    @SuppressWarnings("unchecked")
    Iterator<ObjyCommitInfo> itr = treeSet.iterator();
    while (itr.hasNext())
    {
      ooCommitInfo = itr.next();
      getIt = true; // assume it's what we need, then we filter below.
      long timeStamp = ooCommitInfo.getTimeStamp();
      long branchId = ooCommitInfo.getBranchId();

      if (branch != null && branch.getID() != branchId)
      {
        getIt = false;
      }
      if (getIt && startTime != CDOBranchPoint.UNSPECIFIED_DATE && timeStamp < startTime)
      {
        getIt = false;
      }
      if (getIt && endTime != CDOBranchPoint.UNSPECIFIED_DATE && timeStamp > endTime)
      {
        getIt = false;
      }

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
    treeSet = (ooTreeSetX)Session.getCurrent().getFD().objectFrom(commitInfoSetId.getString());
    return treeSet;

  }

}
