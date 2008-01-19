/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.internal.protocol.CDOIDImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.DLRevisionHolder;
import org.eclipse.emf.cdo.internal.protocol.revision.LRURevisionHolder;
import org.eclipse.emf.cdo.internal.protocol.revision.LRURevisionList;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;
import org.eclipse.emf.cdo.protocol.revision.CDORevisionData;
import org.eclipse.emf.cdo.protocol.revision.CDORevisionResolver;
import org.eclipse.emf.cdo.protocol.revision.delta.CDORevisionDelta;

import org.eclipse.net4j.tests.AbstractOMTest;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class RevisionHolderTest extends AbstractOMTest
{
  public void testAddHead() throws Exception
  {
    LinkedList<LRURevisionHolder> linkedList = new LinkedList<LRURevisionHolder>();
    LRURevisionList list = new LRURevisionList(100);
    validateList(list, 0);
    for (int i = 0; i < 10; i++)
    {
      LRURevisionHolder holder = new LRURevisionHolder(list, new RevisionStub(i));
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
      LRURevisionHolder holder = new LRURevisionHolder(list, new RevisionStub(i));
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
      LRURevisionHolder holder = new LRURevisionHolder(list, new RevisionStub(i));
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
      LRURevisionHolder holder = new LRURevisionHolder(list, new RevisionStub(i));
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
      LRURevisionHolder holder = new LRURevisionHolder(list, new RevisionStub(i));
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
      LRURevisionHolder holder = new LRURevisionHolder(list, new RevisionStub(i));
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
  private static final class RevisionStub implements CDORevision
  {
    private CDOID id;

    public RevisionStub(long id)
    {
      this.id = CDOIDImpl.create(id);
    }

    public CDORevisionDelta compare(CDORevision origin)
    {
      return null;
    }

    public void merge(CDORevisionDelta delta)
    {
      throw new UnsupportedOperationException();
    }

    public CDOClass getCDOClass()
    {
      return null;
    }

    public long getCreated()
    {
      return 0;
    }

    public CDORevisionData getData()
    {
      return null;
    }

    public CDOID getID()
    {
      return id;
    }

    public long getRevised()
    {
      return 0;
    }

    public CDORevisionResolver getRevisionResolver()
    {
      return null;
    }

    public int getVersion()
    {
      return 0;
    }

    public boolean isCurrent()
    {
      return false;
    }

    public boolean isResource()
    {
      return false;
    }

    public boolean isTransactional()
    {
      return false;
    }

    public boolean isValid(long timeStamp)
    {
      return false;
    }
  }
}
