/*
 * Copyright (c) 2010-2013 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.internal.objectivity.bundle.OM;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyClass;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjySchema;
import org.eclipse.emf.cdo.server.internal.objectivity.mapper.ITypeMapper;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import com.objy.as.app.Class_Object;
import com.objy.as.app.Numeric_Value;
import com.objy.as.app.Proposed_Class;
import com.objy.as.app.String_Value;
import com.objy.as.app.VArray_Object;
import com.objy.as.app.d_Access_Kind;
import com.objy.as.app.d_Module;
import com.objy.as.app.ooBaseType;
import com.objy.db.app.ooId;
import com.objy.db.app.ooObj;

/**
 * @author Simon McDuff
 */
/***
 * This class we use VArray of fixed array of Strings, otherwise we have to create strigns as objects, which is
 * expensive.
 */
public class ObjyArrayListString
{
  private static final ContextTracer TRACER_DEBUG = new ContextTracer(OM.DEBUG, ObjyArrayListString.class);

  public static String ClassName = "ObjyArrayListString";

  // embedded class parts.
  private static String embeddedClassName = "oo_StringElement";

  private static String embeddedAttributeName = "value";

  private static String embeddedAttributeNull = "isNull";

  // fixed array class parts.
  private static String FixedArrayClassName = "ObjyFixedStringArray";

  private static String FixedElementsName = "elements";

  private static final long FixedElementsSize = 10;

  protected Class_Object classObject;

  private VArray_Object vArray;

  transient long cacheSize = -1;

  transient long position;

  public static void buildSchema()
  {
    d_Module top_mod = ObjySchema.getTopModule();

    if (top_mod.resolve_class(ObjyArrayListString.ClassName) == null)
    {

      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace("Schema not found for ooArrayListString. Adding ooArrayListString");
      }

      boolean inProcess = top_mod.proposed_classes().hasNext();

      // using embedded class (string, isNull)
      // Proposed_Class embeddedClass = new Proposed_Class(embeddedClassName);
      Proposed_Class embeddedClass = top_mod.propose_new_class(embeddedClassName);

      embeddedClass.add_embedded_class_attribute(com.objy.as.app.d_Module.LAST, d_Access_Kind.d_PUBLIC, // Access kind
          embeddedAttributeName, // Attribute name
          1, // # elements in fixed-size array
          "ooUtf8String" // Type of numeric data
      ); // Default value

      embeddedClass.add_basic_attribute(com.objy.as.app.d_Module.LAST, d_Access_Kind.d_PUBLIC, // Access kind
          embeddedAttributeNull, // Attribute name
          1, // # elements in fixed-size array
          ooBaseType.ooBOOLEAN // Type of numeric data
          ); // Default value

      // top_mod.propose_new_class(embeddedClass);

      // first the child class.
      // Proposed_Class fixedArrayClass = new Proposed_Class(ooArrayListString.FixedArrayClassName);
      Proposed_Class fixedArrayClass = top_mod.propose_new_class(ObjyArrayListString.FixedArrayClassName);

      fixedArrayClass.add_base_class(com.objy.as.app.d_Module.LAST, com.objy.as.app.d_Access_Kind.d_PUBLIC, "ooObj");

      fixedArrayClass.add_embedded_class_attribute(com.objy.as.app.d_Module.LAST, d_Access_Kind.d_PUBLIC, // Access kind
          ObjyArrayListString.FixedElementsName, // Attribute name
          ObjyArrayListString.FixedElementsSize, // # elements in fixed-size array
          embeddedClassName // Type of numeric data
          ); // Default value

      // top_mod.propose_new_class(fixedArrayClass);

      // Proposed_Class StringArrayClass = new Proposed_Class(ooArrayListString.ClassName);
      Proposed_Class StringArrayClass = top_mod.propose_new_class(ObjyArrayListString.ClassName);

      StringArrayClass.add_base_class(com.objy.as.app.d_Module.LAST, com.objy.as.app.d_Access_Kind.d_PUBLIC, "ooObj");

      StringArrayClass.add_basic_attribute(com.objy.as.app.d_Module.LAST, d_Access_Kind.d_PUBLIC, // Access kind
          ObjyArrayList.sizeName, // Attribute name
          1, // # elements in fixed-size array
          ooBaseType.ooINT32 // Type of numeric data
          ); // Default value

      StringArrayClass.add_varray_attribute(com.objy.as.app.d_Module.LAST, d_Access_Kind.d_PUBLIC, // Access kind
          ObjyArrayList.arrayName, // Attribute name
          1, false, ObjyArrayListString.FixedArrayClassName);

      // top_mod.propose_new_class(StringArrayClass);
      if (!inProcess)
      {
        top_mod.activate_proposals(true, true);
      }

      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace("SCHEMA changed : ooArrayListString added");
      }
    }
  }

  public static void initObject(Class_Object classObject)
  {
    // set the size to 0;
    classObject.nset_numeric(ObjyArrayList.sizeName, new Numeric_Value(0));
  }

  public ObjyArrayListString(Class_Object classObject)
  {
    this.classObject = classObject;
  }

  // TODO - there is no check if the index is out of bound.
  protected void setValue(long index, String newValue)
  {
    // find the chunk.
    long chunkItem = index / FixedElementsSize;
    long chunkIndex = index % FixedElementsSize;
    Class_Object chunkObject = getVArray().get_class_obj(chunkItem);
    // String_Value value = chunkObject.nget_string(FixedElementsName, chunkIndex);
    Class_Object embeddedElement = chunkObject.nget_class_obj(FixedElementsName, chunkIndex);
    // TODO - I don't like using magic numbers!!! we are using "1" to index into the embedded object.
    embeddedElement.set_numeric(1, newValue == null ? ITypeMapper.numericTrue : ITypeMapper.numericFalse);
    // String_Value value = embeddedElement.get_string(0); // TODO - I don't like using magic numbers!!!
    String_Value value = embeddedElement.nget_string(embeddedAttributeName);
    value.update();
    if (newValue == null)
    {
      newValue = "";
    }
    value.set(newValue);
  }

  protected String getValue(long index)
  {
    // find the chunk.
    long chunkItem = index / FixedElementsSize;
    long chunkIndex = index % FixedElementsSize;
    Class_Object chunkObject = getVArray().get_class_obj(chunkItem);
    // String_Value value = chunkObject.nget_string(FixedElementsName, chunkIndex);
    Class_Object embeddedElement = chunkObject.nget_class_obj(FixedElementsName, chunkIndex); // TODO - I don't like
                                                                                              // using magic numbers!!!
    String_Value value = embeddedElement.nget_string(embeddedAttributeName);
    Numeric_Value isNull = embeddedElement.get_numeric(1);
    return isNull == ITypeMapper.numericTrue ? null : value.toString();
  }

  public String[] getAll(int index, int chunkSize)
  {
    int size = (int)cachedSize();

    if (chunkSize != CDORevision.UNCHUNKED)
    {
      size = Math.min(size, chunkSize);
    }

    String[] strings = new String[size];
    Class_Object currentChunkObject = null;
    long currentChunkItem = -1;

    for (int i = 0; i < size; i++)
    {
      // find the chunk.
      long chunkItem = (i + index) / FixedElementsSize;
      long chunkIndex = (i + index) % FixedElementsSize;
      if (currentChunkItem != chunkItem)
      {
        currentChunkItem = chunkItem;
        currentChunkObject = getVArray().get_class_obj(currentChunkItem);
      }
      Class_Object embeddedElement = currentChunkObject.nget_class_obj(FixedElementsName, chunkIndex);
      String_Value value = embeddedElement.nget_string(embeddedAttributeName);
      Numeric_Value isNull = embeddedElement.get_numeric(1);
      if (isNull == ITypeMapper.numericTrue)
      {
        strings[i] = null;
      }
      else
      {
        strings[i] = value.toString();
      }
    }
    return strings;
  }

  public void clear()
  {
    // remove all the fixed array objects...
    for (int i = 0; i < getVArray().size(); i++)
    {
      ooId oid = getVArray().get_ooId(i);
      ooObj.create_ooObj(oid).delete();
    }
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
    long size = cachedSize();

    for (long i = size - 1; i >= index; i--)
    {
      setValue(i + sizeToShift, getValue(i));
    }

    cacheSize += sizeToShift;
  }

  private void shiftLeft(int index)
  {
    long size = cachedSize();
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
  private void grow(int numToAdd)
  {
    long arraySize = cachedSize();
    long numChunks = (numToAdd + (int)arraySize) / FixedElementsSize + 1;
    long newChunks = numChunks - arraySize;
    if (newChunks > 0)
    {
      getVArray().resize(numChunks);
      // TODO - this could be cached somewhere...
      ObjyClass chunkClass = ObjySchema.getObjyClass(FixedArrayClassName);
      for (int i = 0; i < newChunks; i++)
      {
        // create a new chunk.
        Class_Object newChunk = Class_Object.new_persistent_object(chunkClass.getASClass(), classObject.objectID(),
            false);
        getVArray().set_ooId(arraySize + i, newChunk.objectID());
      }
      cacheSize = -1;
    }
  }

  /**
   *
   */
  private void prepareToInsert(int numberToAdd)
  {
    long size = cachedSize();
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
      vArray = classObject.nget_varray(ObjyArrayList.arrayName);
    }
    return vArray;
  }

  public void add(int index, String newValue)
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
      basicSet(index + i, (String)newValue[i]);
    }

    saveSize();
  }

  public void remove(int index)
  {
    shiftLeft(index);
  }

  public void add(String newValue)
  {
    long size = cachedSize();

    prepareToInsert(1);

    setValue(size, newValue);

    cacheSize++;

    saveSize();
  }

  public void set(long index, String newValue)
  {
    basicSet(index, newValue);
    cacheSize = -1;
  }

  protected void basicSet(long index, String newValue)
  {
    if (index >= cachedSize())
    {
      throw new ArrayIndexOutOfBoundsException();
    }

    getVArray().update();

    setValue(index, newValue);
  }

  public String get(long index)
  {
    if (index >= size())
    {
      throw new ArrayIndexOutOfBoundsException();
    }

    return getValue(index);
  }

  private void saveSize()
  {
    classObject.nset_numeric(ObjyArrayList.sizeName, new Numeric_Value(cacheSize));
    cacheSize = -1;
  }

  public long cachedSize()
  {
    if (cacheSize == -1)
    {
      cacheSize = classObject.nget_numeric(ObjyArrayList.sizeName).longValue();
    }
    return cacheSize;
  }

  public long size()
  {
    return classObject.nget_numeric(ObjyArrayList.sizeName).longValue();
  }

}
