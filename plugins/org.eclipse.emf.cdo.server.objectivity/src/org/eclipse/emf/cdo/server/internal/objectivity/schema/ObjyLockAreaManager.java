/*
 * Copyright (c) 2011, 2012, 2016 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ibrahim Sallam - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.objectivity.schema;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.lock.CDOLockUtil;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockArea;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockArea.Handler;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;

import com.objy.db.app.Iterator;
import com.objy.db.app.ooId;
import com.objy.db.app.ooObj;
import com.objy.db.util.ooMap;

/**
 * @author Ibrahim Sallam
 */
public class ObjyLockAreaManager extends ooObj
{

  private ooMap lockAreasMap;

  private ObjyLockAreaManager()
  {
  }

  protected void createMap(ooObj clusterObject)
  {
    lockAreasMap = new ooMap();
    clusterObject.cluster(lockAreasMap);
  }

  public static ObjyLockAreaManager create(ooId scopeContOid)
  {
    ObjyLockAreaManager manager = new ObjyLockAreaManager();
    ooObj clusterObject = ooObj.create_ooObj(scopeContOid);
    clusterObject.cluster(manager);

    manager.createMap(manager);

    return manager;
  }

  public void getLockAreas(InternalCDOBranchManager branchManager, String userIDPrefix, Handler handler)
  {
    fetch();
    Iterator itr = lockAreasMap.elements();
    while (itr.hasNext())
    {
      ObjyLockArea objyLockArea = (ObjyLockArea)itr.next();
      String userID = objyLockArea.getUserID();
      if (userID != null && userID.startsWith(userIDPrefix))
      {
        if (!handler.handleLockArea(makeLockArea(branchManager, objyLockArea)))
        {
          return;
        }
      }
    }
  }

  private LockArea makeLockArea(InternalCDOBranchManager branchManager, ObjyLockArea objyLockArea)
  {
    ObjyBranch objyBranch = objyLockArea.getBranch();
    CDOBranchPoint branchPoint = branchManager.getBranch(objyBranch.getBranchId()).getPoint(objyLockArea.getTimeStamp());
    return CDOLockUtil.createLockArea(objyLockArea.getDurableLockingID(), objyLockArea.getUserID(), branchPoint, objyLockArea.isReadOnly(),
        objyLockArea.getLocks());
  }

}
