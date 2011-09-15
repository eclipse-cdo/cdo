/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Ibrahim Sallam - code refactoring for CDO 3.0
 */
package org.eclipse.emf.cdo.server.internal.objectivity.schema;

import com.objy.as.app.Class_Object;
import com.objy.as.app.Numeric_Value;
import com.objy.as.app.VArray_Object;
import com.objy.db.app.ooId;

/**
 * @author Simon McDuff
 */
public abstract class ObjyArrayList<T>
{
  final static String sizeName = "elementCount";

  final static String arrayName = "curr";

  protected Class_Object classObject;

  private VArray_Object vArray;

  protected transient long cacheSize;

  transient long position;

  public static void initObject(Class_Object classObject)
  {
    // set the size to 0;
    classObject.nset_numeric(sizeName, new Numeric_Value(0));
  }

  public ObjyArrayList(Class_Object classObject)
  {
    this.classObject = classObject;
    this.cacheSize = -1;

  }

  public ooId getID()
  {
    return classObject.objectID();
  }

  // /**
  // * TODO - verify need.
  // */
  // private Class_Object getClassObject()
  // {
  // return classObject;
  // }

  public void clear()
  {
    getVArray().resize(0);
    cacheSize = 0;
    saveSize();
  }

  private void shiftRight(int index)
  {
    shiftRight(index, 1);
  }

  private void shiftRight(int index, int sizeToShift)
  {
    long size = this.cachedSize();

    for (long i = size - 1; i >= index; i--)
    {
      setValue(i + sizeToShift, getValue(i));
    }

    cacheSize += sizeToShift;
    saveSize();
  }

  private void shiftLeft(int index)
  {
    long size = this.cachedSize();
    for (long i = index; i < size - 1; i++)
    {
      setValue(i, getValue(i + 1));
    }

    cacheSize--;
    saveSize();
  }

  /**
	 *
	 */
  protected void grow(int item)
  {
    getVArray().resize(getVArraySize() + Math.max(item + 10, 10));
  }

  /**
	 *
	 */
  private void prepareToInsert(int numberToAdd)
  {
    long size = cachedSize();
    update();

    if (size + numberToAdd > getVArraySize())
    {
      grow(numberToAdd);
    }
  }

  protected long getVArraySize()
  {
    return getVArray().size();
  }

  protected void update()
  {
    getVArray().update();
  }

  protected VArray_Object getVArray()
  {
    if (vArray == null)
    {
      vArray = classObject.nget_varray(arrayName);
    }
    return vArray;
  }

  public void add(int index, T newValue)
  {
    prepareToInsert(1);
    shiftRight(index);
    basicSet(index, newValue);
  }

  public void addAll(int index, Object[] newValue)
  {
    prepareToInsert(newValue.length);
    shiftRight(index, newValue.length);

    for (int i = 0; i < newValue.length; i++)
    {
      @SuppressWarnings("unchecked")
      T value = (T)newValue[i];
      basicSet(index + i, value);
    }
  }

  public void remove(int index)
  {
    shiftLeft(index);
  }

  public void add(T newValue)
  {
    long size = cachedSize();

    prepareToInsert(1);
    setValue(size, newValue);
    cacheSize++;
    saveSize();
  }

  public void set(long index, T newValue)
  {
    basicSet(index, newValue);
    // cacheSize = -1;
  }

  public void move(long newPosition, long oldPosition)
  {
    if (oldPosition == newPosition)
    {
      return;
    }

    // get the object at oldPosition.
    T value = getValue(oldPosition);
    // remove the oldPosition.
    remove((int)oldPosition);
    // make a space at the newPosition by shifting elements
    shiftRight((int)newPosition);
    set(newPosition, value);
  }

  protected void basicSet(long index, T newValue)
  {
    if (index >= cachedSize())
    {
      throw new ArrayIndexOutOfBoundsException();
    }

    update();

    setValue(index, newValue);
  }

  public T get(long index)
  {
    if (index >= size())
    {
      throw new ArrayIndexOutOfBoundsException();
    }

    return getValue(index);
  }

  protected abstract void setValue(long index, T newValue);

  protected abstract T getValue(long index);

  protected void saveSize()
  {
    // System.out.println(">>> classObject: " + classObject.objectID().getStoreString() + " <<<");
    // System.out.println("ooArrayList.saveSize() - value to store in objy is: " + cacheSize);
    classObject.nset_numeric(sizeName, new Numeric_Value(cacheSize));
    resetCachedSize();
  }

  protected void resetCachedSize()
  {
    cacheSize = -1;
  }

  protected long cachedSize()
  {
    if (cacheSize == -1)
    {
      cacheSize = classObject.nget_numeric(sizeName).longValue();
      // System.out.println(">>> classObject: " + classObject.objectID().getStoreString() + " <<<");
      // System.out.println("ooArrayList.privateSize() - cacheSize was -1, value from objy is: " + cacheSize);
    }
    return cacheSize;
  }

  public long size()
  {
    // System.out.println(">>> classObject: " + classObject.objectID().getStoreString() + " <<<");
    // Numeric_Value nValue = classObject.nget_numeric(sizeName);
    // System.out.println("ooArrayList.size() - nValue: " + nValue.toString());
    // return classObject.nget_numeric(sizeName).longValue();
    return cachedSize();
  }

}
