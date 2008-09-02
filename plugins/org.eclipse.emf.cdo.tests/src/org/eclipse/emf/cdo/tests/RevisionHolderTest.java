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

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.common.revision.CDORevisionResolver;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.common.revision.cache.lru.DLRevisionHolder;
import org.eclipse.emf.cdo.internal.common.revision.cache.lru.LRURevisionHolder;
import org.eclipse.emf.cdo.internal.common.revision.cache.lru.LRURevisionList;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.eclipse.net4j.tests.AbstractOMTest;
import org.eclipse.net4j.util.collection.MoveableList;
import org.eclipse.net4j.util.io.ExtendedDataOutput;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
  private static final class RevisionStub implements InternalCDORevision
  {
    private CDOID id;

    public RevisionStub(long id)
    {
      this.id = CDOIDUtil.createLong(id);
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

    public CDORevisionDelta compare(CDORevision origin)
    {
      throw new UnsupportedOperationException();
    }

    public void merge(CDORevisionDelta delta)
    {
      throw new UnsupportedOperationException();
    }

    public void write(ExtendedDataOutput out, CDOIDProvider idProvider, int referenceChunk) throws IOException
    {
      throw new UnsupportedOperationException();
    }

    public void add(CDOFeature feature, int index, Object value)
    {
      throw new UnsupportedOperationException();
    }

    public void adjustReferences(Map<CDOIDTemp, CDOID> idMappings)
    {
      throw new UnsupportedOperationException();
    }

    public void clear(CDOFeature feature)
    {
      throw new UnsupportedOperationException();
    }

    public boolean contains(CDOFeature feature, Object value)
    {
      throw new UnsupportedOperationException();
    }

    public Object get(CDOFeature feature, int index)
    {
      throw new UnsupportedOperationException();
    }

    public CDOID getContainerID()
    {
      throw new UnsupportedOperationException();
    }

    public int getContainingFeatureID()
    {
      throw new UnsupportedOperationException();
    }

    public MoveableList<Object> getList(CDOFeature feature, int size)
    {
      throw new UnsupportedOperationException();
    }

    public MoveableList<Object> getList(CDOFeature feature)
    {
      throw new UnsupportedOperationException();
    }

    public CDOID getResourceID()
    {
      throw new UnsupportedOperationException();
    }

    public CDORevision getRevision()
    {
      throw new UnsupportedOperationException();
    }

    public Object getValue(CDOFeature feature)
    {
      throw new UnsupportedOperationException();
    }

    public int hashCode(CDOFeature feature)
    {
      throw new UnsupportedOperationException();
    }

    public int indexOf(CDOFeature feature, Object value)
    {
      throw new UnsupportedOperationException();
    }

    public boolean isEmpty(CDOFeature feature)
    {
      throw new UnsupportedOperationException();
    }

    public boolean isSet(CDOFeature feature)
    {
      throw new UnsupportedOperationException();
    }

    public int lastIndexOf(CDOFeature feature, Object value)
    {
      throw new UnsupportedOperationException();
    }

    public Object move(CDOFeature feature, int targetIndex, int sourceIndex)
    {
      throw new UnsupportedOperationException();
    }

    public Object remove(CDOFeature feature, int index)
    {
      throw new UnsupportedOperationException();
    }

    public Object set(CDOFeature feature, int index, Object value)
    {
      throw new UnsupportedOperationException();
    }

    public void setContainerID(CDOID containerID)
    {
      throw new UnsupportedOperationException();
    }

    public void setContainingFeatureID(int containingFeatureID)
    {
      throw new UnsupportedOperationException();
    }

    public void setCreated(long created)
    {
      throw new UnsupportedOperationException();
    }

    public void setID(CDOID id)
    {
      throw new UnsupportedOperationException();
    }

    public void setListSize(CDOFeature feature, int size)
    {
      throw new UnsupportedOperationException();
    }

    public void setResourceID(CDOID resourceID)
    {
      throw new UnsupportedOperationException();
    }

    public void setRevised(long revised)
    {
      throw new UnsupportedOperationException();
    }

    public int setTransactional()
    {
      throw new UnsupportedOperationException();
    }

    public void setUntransactional()
    {
      throw new UnsupportedOperationException();
    }

    public Object setValue(CDOFeature feature, Object value)
    {
      throw new UnsupportedOperationException();
    }

    public void setVersion(int version)
    {
      throw new UnsupportedOperationException();
    }

    public int size(CDOFeature feature)
    {
      throw new UnsupportedOperationException();
    }

    public <T> T[] toArray(CDOFeature feature, T[] array)
    {
      throw new UnsupportedOperationException();
    }

    public Object[] toArray(CDOFeature feature)
    {
      throw new UnsupportedOperationException();
    }

    public void unset(CDOFeature feature)
    {
      throw new UnsupportedOperationException();
    }
  }
}
