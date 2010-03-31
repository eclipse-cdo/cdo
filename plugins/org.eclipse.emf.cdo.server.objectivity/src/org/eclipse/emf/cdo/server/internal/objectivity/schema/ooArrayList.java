/***************************************************************************
/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
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
public abstract class ooArrayList<T>
{
  final static String sizeName = "elementCount";

  final static String arrayName = "curr";

  protected Class_Object classObject;

  private VArray_Object vArray;

  transient long cacheSize;

  transient long position;

  public static void initObject(Class_Object classObject)
  {
    // set the size to 0;
    classObject.nset_numeric(sizeName, new Numeric_Value(0));
  }

  public ooArrayList(Class_Object classObject)
  {
    this.classObject = classObject;
    this.cacheSize = -1;

  }

  public ooId getID()
  {
    return classObject.objectID();
  }

  /**
   * TODO - verify need.
   * 
   * @return
   */
  private Class_Object getClassObject()
  {
    return classObject;
  }

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
    long size = this.privateSize();

    for (long i = size - 1; i >= index; i--)
    {
      setValue(i + sizeToShift, getValue(i));
    }

    cacheSize += sizeToShift;
  }

  private void shiftLeft(int index)
  {
    long size = this.privateSize();
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
  private void grow(int item)
  {
    getVArray().resize(getVArray().size() + Math.max(item + 10, 10));
  }

  /**
	 * 
	 */
  private void prepareToInsert(int numberToAdd)
  {
    long size = privateSize();
    getVArray().update();

    if (size + numberToAdd > getVArray().size())
    {
      grow(numberToAdd);
    }
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

    saveSize();
  }

  public void addAll(int index, Object[] newValue)
  {
    prepareToInsert(newValue.length);

    shiftRight(index, newValue.length);

    for (int i = 0; i < newValue.length; i++)
    {
      basicSet(index + i, (T)newValue[i]);
    }

    saveSize();
  }

  public void remove(int index)
  {
    shiftLeft(index);
  }

  public void add(T newValue)
  {
    long size = privateSize();

    prepareToInsert(1);

    setValue(size, newValue);

    cacheSize++;

    saveSize();
  }

  public void set(long index, T newValue)
  {
    basicSet(index, newValue);

    cacheSize = -1;
  }

  protected void basicSet(long index, T newValue)
  {
    if (index >= privateSize())
    {
      throw new ArrayIndexOutOfBoundsException();
    }

    getVArray().update();

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

  private void saveSize()
  {
    // System.out.println(">>> classObject: " + classObject.objectID().getStoreString() + " <<<");
    // System.out.println("ooArrayList.saveSize() - value to store in objy is: " + cacheSize);
    classObject.nset_numeric(sizeName, new Numeric_Value(cacheSize));
    cacheSize = -1;
  }

  /**
   * @return
   */
  public long privateSize()
  {
    if (cacheSize == -1)
    {
      cacheSize = classObject.nget_numeric(sizeName).longValue();
      // System.out.println(">>> classObject: " + classObject.objectID().getStoreString() + " <<<");
      // System.out.println("ooArrayList.privateSize() - cacheSize was -1, value from objy is: " + cacheSize);
    }
    return cacheSize;
  }

  /**
   * @return
   */
  public long size()
  {
    // System.out.println(">>> classObject: " + classObject.objectID().getStoreString() + " <<<");
    // Numeric_Value nValue = classObject.nget_numeric(sizeName);
    // System.out.println("ooArrayList.size() - nValue: " + nValue.toString());
    return classObject.nget_numeric(sizeName).longValue();
  }

}
