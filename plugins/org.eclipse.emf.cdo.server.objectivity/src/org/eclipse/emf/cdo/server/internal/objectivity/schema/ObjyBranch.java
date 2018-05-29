/*
 * Copyright (c) 2010-2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ibrahim Sallam - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.objectivity.schema;

import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader.BranchInfo;

import com.objy.db.app.ooObj;
import com.objy.db.util.ooTreeSetX;

import java.util.SortedSet;

/**
 * @author Ibrahim Sallam
 */

/**
 * @author Ibrahim Sallam
 */
/**
 * @author Ibrahim Sallam
 */
public class ObjyBranch extends ooObj
{
  protected int branchId;

  protected int baseBranchId;

  protected long baseBranchTimeStamp;

  protected String branchName;

  protected ooTreeSetX revisions;

  public static ObjyBranch create(ooObj clusterObj, int branchId, int baseBranchId, String branchName, long timeStamp)
  {
    ObjyBranch objyBranch = new ObjyBranch(branchId, baseBranchId, branchName, timeStamp);
    clusterObj.cluster(objyBranch);
    objyBranch.createRevisionsSet();
    return objyBranch;
  }

  public static ObjyBranch create(ooObj clusterObj, int branchId, BranchInfo branchInfo)
  {
    ObjyBranch objyBranch = new ObjyBranch(branchId, branchInfo);
    clusterObj.cluster(objyBranch);
    objyBranch.createRevisionsSet();
    return objyBranch;
  }

  private ObjyBranch(int id, BranchInfo branchInfo)
  {
    branchId = id;
    baseBranchId = branchInfo.getBaseBranchID();
    baseBranchTimeStamp = branchInfo.getBaseTimeStamp();
    branchName = branchInfo.getName();
    revisions = null;
  }

  private ObjyBranch(int branchId, int baseBranchId, String branchName, long timeStamp)
  {
    this.branchId = branchId;
    this.baseBranchId = baseBranchId;
    baseBranchTimeStamp = timeStamp;
    this.branchName = branchName;
    revisions = null;
  }

  public int getBranchId()
  {
    fetch();
    return branchId;
  }

  public int getBaseBranchId()
  {
    fetch();
    return baseBranchId;
  }

  public long getBaseBranchTimeStamp()
  {
    fetch();
    return baseBranchTimeStamp;
  }

  public String getBranchName()
  {
    fetch();
    return branchName;
  }

  public void addRevision(ooObj anObj)
  {
    markModified();
    if (revisions == null) // we'll only allocate if needed.
    {
      revisions = new ooTreeSetX();
      this.cluster(revisions);
    }
    revisions.add(anObj);
  }

  public int numberOfRevisions()
  {
    fetch();
    return revisions.size();
  }

  public SortedSet<?> getRevisions()
  {
    fetch();
    // // we don't want to allocate the ooTreeSetX object if the branch is empty.
    // if (revisions == null)
    // {
    // return new ArrayList<Object>().iterator();
    // }

    return revisions;
  }

  public BranchInfo getBranchInfo()
  {
    fetch();
    BranchInfo branchInfo = new BranchInfo(getBranchName(), getBaseBranchId(), getBaseBranchTimeStamp());
    return branchInfo;
  }

  public boolean deleteRevision(Object anObject)
  {
    markModified();
    return revisions.remove(anObject);
  }

  private void createRevisionsSet()
  {
    if (revisions == null) // we'll only allocate if needed.
    {
      revisions = new ooTreeSetX();
      this.cluster(revisions);
    }
  }

}
