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
package org.eclipse.emf.cdo.server.internal.objectivity.schema;

import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader.BranchInfo;

import com.objy.db.app.ooObj;
import com.objy.db.util.ooTreeSetX;

import java.util.Iterator;

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

  public ObjyBranch(int id, BranchInfo branchInfo)
  {
    branchId = id;
    baseBranchId = branchInfo.getBaseBranchID();
    baseBranchTimeStamp = branchInfo.getBaseTimeStamp();
    branchName = branchInfo.getName();
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

  public void addRevision(Object objyObject)
  {
    markModified();
    if (revisions == null) // we'll only allocate if needed.
    {
      revisions = new ooTreeSetX();
      this.cluster(revisions);
    }
    revisions.add(objyObject);
  }

  public int numberOfRevisions()
  {
    fetch();
    return revisions.size();
  }

  public Iterator getRevisions()
  {
    fetch();
    return revisions.iterator();
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

}
