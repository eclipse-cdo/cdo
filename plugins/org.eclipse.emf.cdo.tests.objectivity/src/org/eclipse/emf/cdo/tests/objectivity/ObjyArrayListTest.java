/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.objectivity;

import static org.junit.Assert.assertEquals;

import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyArrayList;

import com.objy.as.app.Class_Object;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Class to be used for testing, all persistence related calls are overriden by local ones.
 * 
 * @author ibrahim
 */
class ObjyLocalArrayListLong extends ObjyArrayList<Long>
{
  public ObjyLocalArrayListLong(Class_Object classObject)
  {
    super(classObject);
  }

  ArrayList<Long> arrayList = new ArrayList<Long>();

  long arraySize = 0;

  @Override
  protected void setValue(long index, Long newValue)
  {
    arrayList.set((int)index, newValue);
  }

  @Override
  protected Long getValue(long index)
  {
    return arrayList.get((int)index);
  }

  @Override
  protected long cachedSize()
  {
    if (cacheSize == -1)
    {
      cacheSize = arraySize;
    }
    return cacheSize;
  }

  @Override
  protected void update()
  {
    // do nothing.
  }

  @Override
  protected long getVArraySize()
  {
    return arrayList.size();
  }

  @Override
  protected void saveSize()
  {
    arraySize = cacheSize;
    cacheSize = -1;
  }

  @Override
  protected void grow(int item)
  {
    for (int i = 0; i < Math.max(item + 10, 10); i++)
    {
      arrayList.add(new Long(0));
    }
  }

  @Override
  public long size()
  {
    return arraySize;
  }
}

public class ObjyArrayListTest
{
  ObjyLocalArrayListLong arrayListLong;

  int numItems = 10;

  @Before
  public void setUp()
  {
    arrayListLong = new ObjyLocalArrayListLong(null);
    // fill the array.
    for (int i = 0; i < numItems; i++)
    {
      arrayListLong.add(new Long(i));
    }
  }

  @Test
  public void moveItems()
  {
    // move elemnt 7 to spot 2.
    arrayListLong.move(2, 7);
    assertEquals(numItems, arrayListLong.size());
    assertEquals(new Long(7), arrayListLong.get(2));
  }

  @Test
  public void removeLastItem()
  {
    //
    arrayListLong.remove((int)arrayListLong.size());
    assertEquals((numItems - 1), arrayListLong.size());
  }

  @Test
  public void removeMiddleItem()
  {
    //
    arrayListLong.remove(5);
    assertEquals((numItems - 1), arrayListLong.size());
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void empty()
  {
    new ArrayList<Object>().get(0);
  }

  @After
  public void tearDown()
  {
  }
}
