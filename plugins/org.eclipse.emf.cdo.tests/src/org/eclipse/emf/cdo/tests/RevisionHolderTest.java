/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.internal.common.branch.CDOBranchImpl;
import org.eclipse.emf.cdo.internal.common.revision.cache.lru.DLRevisionHolder;
import org.eclipse.emf.cdo.internal.common.revision.cache.lru.LRURevisionCache;
import org.eclipse.emf.cdo.internal.common.revision.cache.lru.LRURevisionHolder;
import org.eclipse.emf.cdo.internal.common.revision.cache.lru.LRURevisionList;
import org.eclipse.emf.cdo.internal.common.revision.cache.lru.RevisionHolder;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.revision.StubCDORevision;

import org.eclipse.emf.ecore.EClass;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class RevisionHolderTest extends AbstractCDOTest
{
  public void testChronology() throws Exception
  {
    LRURevisionCache cache = new LRURevisionCache();
    cache.activate();

    TestRevision r1v1 = new TestRevision(1, 1, 1);
    cache.addRevision(r1v1);
    assertEquals(CDORevision.UNSPECIFIED_DATE, r1v1.getRevised());

    TestRevision r1v2 = new TestRevision(1, 2, 6);
    cache.addRevision(r1v2);
    assertEquals(CDORevision.UNSPECIFIED_DATE, r1v2.getRevised());
    assertEquals(r1v2.getTimeStamp() - 1, r1v1.getRevised());

    TestRevision r1v3 = new TestRevision(1, 3, 11);
    cache.addRevision(r1v3);
    assertEquals(CDORevision.UNSPECIFIED_DATE, r1v3.getRevised());
    assertEquals(r1v3.getTimeStamp() - 1, r1v2.getRevised());
    assertEquals(r1v2.getTimeStamp() - 1, r1v1.getRevised());

    CDOID id = r1v1.getID();

    RevisionHolder h1v3 = cache.getHolder(id);
    assertEquals(r1v3, h1v3.getRevision());

    RevisionHolder h1v2 = h1v3.getNext();
    assertEquals(r1v2, h1v2.getRevision());

    RevisionHolder h1v1 = h1v2.getNext();
    assertEquals(r1v1, h1v1.getRevision());
    assertEquals(null, h1v1.getNext());

    h1v2 = h1v1.getPrev();
    assertEquals(r1v2, h1v2.getRevision());

    h1v3 = h1v2.getPrev();
    assertEquals(r1v3, h1v3.getRevision());
    assertEquals(null, h1v3.getPrev());
  }

  public void testAddHead() throws Exception
  {
    LinkedList<LRURevisionHolder> linkedList = new LinkedList<LRURevisionHolder>();
    LRURevisionList list = new LRURevisionList(100);
    validateList(list, 0);
    for (int i = 0; i < 10; i++)
    {
      LRURevisionHolder holder = new LRURevisionHolder(new TestRevision(i));
      linkedList.addFirst(holder);
      list.addHead(holder);
      validateList(list, i + 1);
      validateList(list, linkedList);
    }
  }

  public void testAddTail() throws Exception
  {
    LinkedList<LRURevisionHolder> linkedList = new LinkedList<LRURevisionHolder>();
    LRURevisionList list = new LRURevisionList(100);
    validateList(list, 0);
    for (int i = 0; i < 10; i++)
    {
      LRURevisionHolder holder = new LRURevisionHolder(new TestRevision(i));
      linkedList.addLast(holder);
      list.addTail(holder);
      validateList(list, i + 1);
      validateList(list, linkedList);
    }
  }

  public void testRemoveHead() throws Exception
  {
    LinkedList<LRURevisionHolder> linkedList = new LinkedList<LRURevisionHolder>();
    LRURevisionList list = new LRURevisionList(100);
    for (int i = 0; i < 10; i++)
    {
      LRURevisionHolder holder = new LRURevisionHolder(new TestRevision(i));
      linkedList.addLast(holder);
      list.addTail(holder);
    }

    validateList(list, 10);
    validateList(list, linkedList);

    LRURevisionHolder holder = linkedList.removeFirst();
    list.remove(holder);
    validateList(list, 9);
    validateList(list, linkedList);
  }

  public void testRemoveTail() throws Exception
  {
    LinkedList<LRURevisionHolder> linkedList = new LinkedList<LRURevisionHolder>();
    LRURevisionList list = new LRURevisionList(100);
    for (int i = 0; i < 10; i++)
    {
      LRURevisionHolder holder = new LRURevisionHolder(new TestRevision(i));
      linkedList.addLast(holder);
      list.addTail(holder);
    }

    validateList(list, 10);
    validateList(list, linkedList);

    LRURevisionHolder holder = linkedList.removeLast();
    list.remove(holder);
    validateList(list, 9);
    validateList(list, linkedList);
  }

  public void testRemoveMiddle() throws Exception
  {
    LinkedList<LRURevisionHolder> linkedList = new LinkedList<LRURevisionHolder>();
    LRURevisionList list = new LRURevisionList(100);
    for (int i = 0; i < 10; i++)
    {
      LRURevisionHolder holder = new LRURevisionHolder(new TestRevision(i));
      linkedList.addLast(holder);
      list.addTail(holder);
    }

    validateList(list, 10);
    validateList(list, linkedList);

    LRURevisionHolder holder = linkedList.remove(5);
    list.remove(holder);
    validateList(list, 9);
    validateList(list, linkedList);
  }

  public void testRemoveAll() throws Exception
  {
    LinkedList<LRURevisionHolder> linkedList = new LinkedList<LRURevisionHolder>();
    LRURevisionList list = new LRURevisionList(100);
    for (int i = 0; i < 10; i++)
    {
      LRURevisionHolder holder = new LRURevisionHolder(new TestRevision(i));
      linkedList.addLast(holder);
      list.addTail(holder);
    }

    validateList(list, 10);
    validateList(list, linkedList);

    for (int i = 10; i > 0; i--)
    {
      LRURevisionHolder holder = linkedList.remove(0);
      list.remove(holder);
      validateList(list, i - 1);
      validateList(list, linkedList);
    }

    validateList(list, 0);
    validateList(list, linkedList);
  }

  public void testRevised() throws Exception
  {
    LRURevisionCache cache = new LRURevisionCache();
    cache.activate();

    TestRevision r1v1 = new TestRevision(1, 1, 1);
    cache.addRevision(r1v1);
    assertEquals(CDORevision.UNSPECIFIED_DATE, r1v1.getRevised());

    TestRevision r1v3 = new TestRevision(1, 3, 11);
    cache.addRevision(r1v3);
    assertEquals(CDORevision.UNSPECIFIED_DATE, r1v3.getRevised());
    assertNotSame(r1v3.getTimeStamp() - 1, r1v1.getRevised());

    CDOID id = r1v1.getID();

    RevisionHolder h1v3 = cache.getHolder(id);
    assertEquals(r1v3, h1v3.getRevision());
  }

  private void validateList(LRURevisionList list, int size)
  {
    assertEquals(size, list.size());
    if (size == 0)
    {
      assertEquals(list.getDLHead(), list.getDLTail());
    }
    else
    {
      assertNotNull(list.getDLHead());
      assertNotNull(list.getDLTail());
    }

    assertEquals(list.getDLHead(), list.getDLHead().getDLNext().getDLPrev());
    assertEquals(list.getDLTail(), list.getDLTail().getDLPrev().getDLNext());

    DLRevisionHolder holder = list.getDLHead();
    for (int i = 0; i < size; i++)
    {
      assertEquals(holder, holder.getDLNext().getDLPrev());
      assertEquals(holder, holder.getDLPrev().getDLNext());
    }
  }

  private void validateList(LRURevisionList list, List<LRURevisionHolder> expected)
  {
    assertEquals(expected.size(), list.size());
    for (int i = 0; i < expected.size(); i++)
    {
      assertEquals(expected.get(i), list.get(i));
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class TestRevision extends StubCDORevision
  {
    private CDOID id;

    private CDOBranchPoint branchPoint;

    private int version;

    private long revised;

    public TestRevision(long id, int version, long created, long revised)
    {
      this.id = CDOIDUtil.createLong(id);
      branchPoint = CDOBranchUtil.createBranchPoint(new CDOBranchImpl(CDOBranch.MAIN_BRANCH_ID, null), created);
      this.version = version;
      this.revised = revised;
    }

    public TestRevision(long id, int version, long created)
    {
      this(id, version, created, CDORevision.UNSPECIFIED_DATE);
    }

    public TestRevision(long id)
    {
      this(id, 0, CDORevision.UNSPECIFIED_DATE);
    }

    @Override
    public EClass getEClass()
    {
      return null;
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
    public CDOBranch getBranch()
    {
      return branchPoint.getBranch();
    }

    @Override
    public long getTimeStamp()
    {
      return branchPoint.getTimeStamp();
    }

    @Override
    public void setBranchPoint(CDOBranchPoint branchPoint)
    {
      this.branchPoint = CDOBranchUtil.createBranchPoint(branchPoint);
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
    public CDORevision copy()
    {
      return new TestRevision(CDOIDUtil.getLong(id), version, branchPoint.getTimeStamp(), revised);
    }
  }
}
