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
package org.eclipse.emf.cdo.tests.objectivity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.emf.cdo.server.internal.objectivity.db.FdManager;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyConnection;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjySession;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyBranch;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyBranchManager;
import org.eclipse.emf.cdo.server.internal.objectivity.utils.ObjyDb;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader.BranchInfo;

import com.objy.db.app.ooObj;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class Temp extends ooObj
{
  protected long lValue = 0;

  protected int revision = 0;

  private int branchId = 0;

  public Temp(long value)
  {
    lValue = value;
    revision = 1;
  }

  public void revisionIt()
  {
    markModified();
    revision++;
  }

  public void setBranch(int id)
  {
    markModified();
    branchId = id;
  }

  public int getRevision()
  {
    fetch();
    return revision;
  }

  public int getBranchId()
  {
    fetch();
    return branchId;
  }

  public long getValue()
  {
    fetch();
    return lValue;
  }
}

/**
 * This class will hide access to Objectivity/Db
 * 
 * @author Ibrahim Sallam
 */
class DbStore
{

  // private final String fdName = "c:\\data\\objyBranchTest.boot"; // TODO - this is windows path style, generalise it.

  private final String repoName = "TestRepo";

  private FdManager fdManager;

  private ObjyConnection objyConnection;

  private ObjySession objySession;

  public DbStore()
  {
    fdManager = new FdManager();
    objyConnection = ObjyConnection.INSTANCE;
  }

  public void startup()
  {
    fdManager.configure("objyBrnachTest");
    objyConnection.connect(fdManager.getFd());
    objySession = objyConnection.getWriteSessionFromPool("TestSession");
  }

  public void shutdown()
  {
    fdManager.removeData();
    objyConnection.disconnect();
  }

  public ObjyBranchManager getBranchManager()
  {
    return ObjyDb.getOrCreateBranchManager(repoName);
  }

  public void begin()
  {
    objySession.begin();
  }

  public void commit()
  {
    objySession.commit();
  }

  public Temp newObject()
  {
    return new Temp(System.currentTimeMillis());
  }

}

/**
 * @author Ibrahim Sallam
 */
public class ObjyBranchManagementTest
{
  private static final String BRANCH_NAME_A = "MyBranchA";

  private static final String BRANCH_NAME_B = "MyBranchB";

  private final int baseBranchId = 0;

  private final long baseBranchTimeStamp = System.currentTimeMillis();

  DbStore dbStore = null;

  BranchInfo branchInfoA = null;

  BranchInfo branchInfoB = null;

  public static final int NEW_BRANCH = Integer.MAX_VALUE;

  public static final int NEW_LOCAL_BRANCH = Integer.MIN_VALUE;

  @Before
  public void setUp()
  {
    dbStore = new DbStore();
    branchInfoA = new BranchInfo(BRANCH_NAME_A, baseBranchId, baseBranchTimeStamp);
    branchInfoB = new BranchInfo(BRANCH_NAME_B, baseBranchId, baseBranchTimeStamp);
    dbStore.startup();
  }

  @Test
  public void getNextBranchId()
  {
    dbStore.begin();
    int branchId = dbStore.getBranchManager().nextBranchId();
    dbStore.commit();
    assertTrue(branchId > 0);
  }

  @Test
  public void createBranch()
  {
    dbStore.begin();
    int nextBranchId = dbStore.getBranchManager().getLastBranchId() + 1;
    dbStore.commit();

    dbStore.begin();
    int branchId = dbStore.getBranchManager().createBranch(NEW_BRANCH, branchInfoA).getElement1();
    dbStore.commit();
    assertEquals(branchId, nextBranchId);
  }

  @Test
  public void getUnavailableBranch()
  {
    dbStore.begin();
    ObjyBranch objyBranch = dbStore.getBranchManager().getBranch(1);
    dbStore.commit();
    assertTrue(objyBranch == null);
  }

  @Test
  public void getBranch()
  {
    dbStore.begin();
    int branchId = dbStore.getBranchManager().createBranch(NEW_LOCAL_BRANCH, branchInfoB).getElement1();
    dbStore.commit();
    // using the branchId we'll get it from the dbStore.
    dbStore.begin();
    BranchInfo branchInfo = dbStore.getBranchManager().getBranch(branchId).getBranchInfo();
    dbStore.commit();
    assertEquals(baseBranchId, branchInfo.getBaseBranchID());
    assertEquals(baseBranchTimeStamp, branchInfo.getBaseTimeStamp());
    assertEquals(BRANCH_NAME_B, branchInfo.getName());
  }

  @Test
  public void createBranches()
  {
    // Create a group of branches (no sub-branches)
    dbStore.begin();
    int branchIdA = dbStore.getBranchManager().createBranch(NEW_BRANCH, branchInfoA).getElement1();
    int branchIdB = dbStore.getBranchManager().createBranch(NEW_BRANCH, branchInfoB).getElement1();
    dbStore.commit();

    dbStore.begin();
    int nextBranchId = dbStore.getBranchManager().nextBranchId();
    dbStore.commit();
    assertTrue(branchIdA < nextBranchId);
    assertTrue(branchIdB < nextBranchId);
  }

  @Test
  public void getBranches()
  {
    // Get all branches from a created group.

    // Create a group of branches (no sub-branches)
    dbStore.begin();
    int branchIdA = dbStore.getBranchManager().createBranch(NEW_BRANCH, branchInfoA).getElement1();
    int branchIdB = dbStore.getBranchManager().createBranch(NEW_BRANCH, branchInfoB).getElement1();
    dbStore.commit();

    // using the branchId we'll get it from the dbStore.
    dbStore.begin();
    BranchInfo branchInfoB = dbStore.getBranchManager().getBranch(branchIdB).getBranchInfo();
    dbStore.commit();
    assertEquals(baseBranchId, branchInfoB.getBaseBranchID());
    assertEquals(baseBranchTimeStamp, branchInfoB.getBaseTimeStamp());
    assertEquals(BRANCH_NAME_B, branchInfoB.getName());

    // using the branchId we'll get it from the dbStore.
    dbStore.begin();
    BranchInfo branchInfoA = dbStore.getBranchManager().getBranch(branchIdA).getBranchInfo();
    dbStore.commit();
    assertEquals(baseBranchId, branchInfoA.getBaseBranchID());
    assertEquals(baseBranchTimeStamp, branchInfoA.getBaseTimeStamp());
    assertEquals(BRANCH_NAME_A, branchInfoA.getName());
  }

  @Test
  public void getBrancheRange()
  {
    // Get a range of branches from a created group.
    int thisBaseBranchId = 0;
    long thisBaseBranchTimeStamp = System.currentTimeMillis();

    // Create a group of branches (no sub-branches)
    dbStore.begin();
    String prefixString = "Branch_";
    List<Integer> branchList = new ArrayList<Integer>();
    for (int i = 0; i < 20; i++)
    {
      dbStore.begin();
      BranchInfo branchInfo = new BranchInfo(prefixString + i, thisBaseBranchId, thisBaseBranchTimeStamp);
      int id = dbStore.getBranchManager().createBranch(NEW_BRANCH, branchInfo).getElement1();
      branchList.add(id);
      dbStore.commit();
    }
    dbStore.commit();

    // Get range from 5 to 10.
    {
      dbStore.begin();
      List<ObjyBranch> branchRangeList = dbStore.getBranchManager().getBranches(branchList.get(5), branchList.get(10));
      int index = 5;
      for (ObjyBranch objyBranch : branchRangeList)
      {
        int expectedId = branchList.get(index);
        assertEquals(expectedId, objyBranch.getBranchId());
        assertEquals(thisBaseBranchId, objyBranch.getBaseBranchId());
        assertEquals(thisBaseBranchTimeStamp, objyBranch.getBaseBranchTimeStamp());
        assertEquals("Branch_" + index, objyBranch.getBranchName());
        index++;
      }
      dbStore.commit();
    }

    // Get range from 7 to end.
    {
      dbStore.begin();
      List<ObjyBranch> branchRangeList = dbStore.getBranchManager().getBranches(branchList.get(7), 0);
      assertEquals(13, branchRangeList.size());

      int index = 7;
      for (ObjyBranch objyBranch : branchRangeList)
      {
        int expectedId = branchList.get(index);
        assertEquals(expectedId, objyBranch.getBranchId());
        assertEquals(thisBaseBranchId, objyBranch.getBaseBranchId());
        assertEquals(thisBaseBranchTimeStamp, objyBranch.getBaseBranchTimeStamp());
        assertEquals("Branch_" + index, objyBranch.getBranchName());
        index++;
      }
      dbStore.commit();
    }
  }

  @Test
  public void deleteBranch()
  {
    dbStore.begin();
    int branchId = dbStore.getBranchManager().createBranch(NEW_BRANCH, branchInfoA).getElement1();
    dbStore.commit();

    // delete it.
    dbStore.begin();
    assertTrue(dbStore.getBranchManager().deleteBranch(branchId));
    dbStore.commit();
  }

  @Test
  public void createSubBranches()
  {
    dbStore.begin();
    int thisBaseBranchId = dbStore.getBranchManager().createBranch(NEW_BRANCH, branchInfoA).getElement1();
    dbStore.getBranchManager().createBranch(NEW_BRANCH, branchInfoB);
    long thisBaseBranchTimeStamp = System.currentTimeMillis();
    // using the created branch we'll create subBranches.
    dbStore.commit();

    String prefixString = "SubBranch_";
    List<Integer> subBranchList = new ArrayList<Integer>();
    for (int i = 0; i < 10; i++)
    {
      dbStore.begin();
      BranchInfo branchInfo = new BranchInfo(prefixString + i, thisBaseBranchId, thisBaseBranchTimeStamp);
      int id = dbStore.getBranchManager().createBranch(NEW_BRANCH, branchInfo).getElement1();
      subBranchList.add(id);
      dbStore.commit();
    }
    // get sub-branches for 0.
    List<ObjyBranch> objyBranchList = new ArrayList<ObjyBranch>();

    dbStore.begin();
    objyBranchList = dbStore.getBranchManager().getSubBranches(0);
    assertEquals(2, objyBranchList.size());
    dbStore.commit();

    dbStore.begin();
    objyBranchList = dbStore.getBranchManager().getSubBranches(thisBaseBranchId);
    assertEquals(10, objyBranchList.size());
    dbStore.commit();

  }

  @Test
  public void getSubBranches()
  {
    dbStore.begin();
    int thisBaseBranchId = dbStore.getBranchManager().createBranch(NEW_LOCAL_BRANCH, branchInfoA).getElement1();
    long thisBaseBranchTimeStamp = System.currentTimeMillis();
    // using the created branch we'll create subBranches.
    dbStore.commit();

    String prefixString = "SubBranch_";
    List<Integer> subBranchList = new ArrayList<Integer>();
    for (int i = 0; i < 10; i++)
    {
      dbStore.begin();
      BranchInfo branchInfo = new BranchInfo(prefixString + i, thisBaseBranchId, thisBaseBranchTimeStamp);
      int id = dbStore.getBranchManager().createBranch(NEW_LOCAL_BRANCH, branchInfo).getElement1();
      subBranchList.add(id);
      dbStore.commit();
    }
    // verify that we have sub-branches.
    dbStore.begin();
    int i = 0;
    for (Integer iValue : subBranchList)
    {
      BranchInfo branchInfo = dbStore.getBranchManager().getBranch(iValue).getBranchInfo();
      assertEquals(thisBaseBranchId, branchInfo.getBaseBranchID());
      assertEquals(thisBaseBranchTimeStamp, branchInfo.getBaseTimeStamp());
      assertEquals(prefixString + i++, branchInfo.getName());
    }
    dbStore.commit();

  }

  @Test
  public void addRevisionsToBranch()
  {
    List<Temp> objectList = new ArrayList<Temp>();
    // create 100 objects in Objy (Main Branch).
    dbStore.begin();
    for (int i = 0; i < 100; i++)
    {
      Temp tempObject = dbStore.newObject();
      if (i % 2 == 0)
      {
        objectList.add(tempObject);
      }
    }
    dbStore.commit();

    // create a branch.
    dbStore.begin();
    int branchId = dbStore.getBranchManager().createBranch(NEW_BRANCH, branchInfoA).getElement1();
    dbStore.commit();

    // version what we collected in the array, and put them into a new branch.
    {
      dbStore.begin();
      ObjyBranch objyBranch = dbStore.getBranchManager().getBranch(branchId);

      for (Temp tempObj : objectList)
      {
        tempObj.setBranch(branchId);
        tempObj.revisionIt();
        objyBranch.addRevision(tempObj);
      }
      dbStore.commit();
    }
    // verify that the number of revisions in a branch is the same as what
    // we added.
    {
      dbStore.begin();
      ObjyBranch objyBranch = dbStore.getBranchManager().getBranch(branchId);
      assertEquals(objectList.size(), objyBranch.numberOfRevisions());
      dbStore.commit();
    }
  }

  @Test
  public void getRevisionsFromBranch()
  {
    List<Temp> objectList = new ArrayList<Temp>();
    // create 100 objects in Objy (Main Branch).
    dbStore.begin();
    for (int i = 0; i < 200; i++)
    {
      Temp tempObject = dbStore.newObject();
      if (i % 2 == 0)
      {
        objectList.add(tempObject);
      }
    }
    dbStore.commit();

    // create a branch.
    dbStore.begin();
    int branchId = dbStore.getBranchManager().createBranch(NEW_BRANCH, branchInfoA).getElement1();
    dbStore.commit();

    // version what we collected in the array, and put them into a new branch.
    {
      dbStore.begin();
      ObjyBranch objyBranch = dbStore.getBranchManager().getBranch(branchId);

      for (Temp tempObj : objectList)
      {
        tempObj.setBranch(branchId);
        tempObj.revisionIt();
        objyBranch.addRevision(tempObj);
      }
      dbStore.commit();
    }
    // Get the objects from the branch.
    {
      dbStore.begin();
      ObjyBranch objyBranch = dbStore.getBranchManager().getBranch(branchId);
      Iterator<?> itr = objyBranch.getRevisions().iterator();
      List<Temp> actualList = new ArrayList<Temp>();
      while (itr.hasNext())
      {
        Temp tempObj = (Temp)itr.next();
        assertEquals(tempObj.getBranchId(), branchId);
        assertTrue(tempObj.getRevision() > 1);
        actualList.add(tempObj);
      }
      assertEquals(objectList.size(), actualList.size());
      dbStore.commit();
    }
  }

  @Test
  public void deleteRevisionsFromBranch()
  {
    List<Temp> objectList = new ArrayList<Temp>();
    // create 100 objects in Objy (Main Branch).
    dbStore.begin();
    for (int i = 0; i < 100; i++)
    {
      Temp tempObject = dbStore.newObject();
      if (i % 2 == 0)
      {
        objectList.add(tempObject);
      }
    }
    dbStore.commit();

    // create a branch.
    dbStore.begin();
    int branchId = dbStore.getBranchManager().createBranch(NEW_BRANCH, branchInfoA).getElement1();
    dbStore.commit();

    // version what we collected in the array, and put them into a new branch.
    {
      dbStore.begin();
      ObjyBranch objyBranch = dbStore.getBranchManager().getBranch(branchId);

      for (Temp tempObj : objectList)
      {
        tempObj.setBranch(branchId);
        tempObj.revisionIt();
        objyBranch.addRevision(tempObj);
      }
      dbStore.commit();
    }
    // verify that the number of revisions in a branch is the same as what
    // we added.
    {
      dbStore.begin();
      ObjyBranch objyBranch = dbStore.getBranchManager().getBranch(branchId);
      assertEquals(objectList.size(), objyBranch.numberOfRevisions());
      dbStore.commit();
    }

    // delete revisions
    {
      dbStore.begin();
      ObjyBranch objyBranch = dbStore.getBranchManager().getBranch(branchId);
      for (Temp tempObj : objectList)
      {
        assertTrue(objyBranch.deleteRevision(tempObj));
      }
      dbStore.commit();
    }

    // verify that the number of revisions are 0
    {
      dbStore.begin();
      ObjyBranch objyBranch = dbStore.getBranchManager().getBranch(branchId);
      assertEquals(0, objyBranch.numberOfRevisions());
      dbStore.commit();
    }
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void empty()
  {
    new ArrayList<Object>().get(0);
  }

  @After
  public void tearDown()
  {
    dbStore.shutdown();
  }

}
