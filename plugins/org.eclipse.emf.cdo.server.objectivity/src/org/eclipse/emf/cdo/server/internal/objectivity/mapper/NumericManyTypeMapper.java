/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ibrahim Sallam - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.objectivity.mapper;

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.internal.objectivity.bundle.OM;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyObject;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjySchema;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EStructuralFeature;

import com.objy.as.app.Class_Object;
import com.objy.as.app.Class_Position;
import com.objy.as.app.Numeric_Value;
import com.objy.as.app.Proposed_Basic_Attribute;
import com.objy.as.app.Proposed_Class;
import com.objy.as.app.Proposed_Property;
import com.objy.as.app.VArray_Object;
import com.objy.as.app.d_Access_Kind;
import com.objy.as.app.d_Attribute;
import com.objy.as.app.d_Class;
import com.objy.as.app.d_Module;
import com.objy.as.app.ooBaseType;

import java.util.Date;

/**
 * @author Ibrahim Sallam
 */
public abstract class NumericManyTypeMapper extends BasicTypeMapper implements IManyTypeMapper
{

  protected abstract Object fromNumericValue(Numeric_Value numericValue, boolean isNull);

  protected abstract Numeric_Value toNumericValue(Object value);

  protected abstract ooBaseType getObjyBaseType();

  private static final ContextTracer TRACER_DEBUG = new ContextTracer(OM.DEBUG, NumericManyTypeMapper.class);

  public static NumericManyTypeMapper.TMBoolean TMBOOLEAN = new TMBoolean();

  public static NumericManyTypeMapper.TMByte TMBYTE = new TMByte();

  public static NumericManyTypeMapper.TMChar TMCHAR = new TMChar();

  public static NumericManyTypeMapper.TMDate TMDATE = new TMDate();

  public static NumericManyTypeMapper.TMDouble TMDOUBLE = new TMDouble();

  public static NumericManyTypeMapper.TMFloat TMFLOAT = new TMFloat();

  public static NumericManyTypeMapper.TMInteger TMINTEGER = new TMInteger();

  public static NumericManyTypeMapper.TMLong TMLONG = new TMLong();

  public static NumericManyTypeMapper.TMShort TMSHORT = new TMShort();

  // ---------------------------------
  // Schema
  // ---------------------------------

  private String embeddedClassName()
  {
    return "oo_" + getObjyBaseType() + "_Class";
  }

  private d_Class getEmbeddedClass()
  {
    d_Class embeddedClass = ObjySchema.getTopModule().resolve_class(embeddedClassName());
    // System.out.println("OBJY: Resolving className: " + embeddedClassName() + " - d_Class: " + embeddedClass);
    return embeddedClass;
  }

  private boolean createEmbeddedClass()
  {
    boolean bDone = true;

    d_Module top_mod = ObjySchema.getTopModule();
    boolean inProcess = top_mod.proposed_classes().hasNext();

    Proposed_Class propClass = top_mod.propose_new_class(embeddedClassName());

    propClass.add_basic_attribute(com.objy.as.app.d_Module.LAST, d_Access_Kind.d_PUBLIC, // Access kind
        embeddedAttributeName, // Attribute name
        1, // # elements in fixed-size array
        getObjyBaseType() // Type of numeric data
        ); // Default value

    propClass.add_basic_attribute(com.objy.as.app.d_Module.LAST, d_Access_Kind.d_PUBLIC, // Access kind
        embeddedAttributeNull, // Attribute name
        1, // # elements in fixed-size array
        ooBaseType.ooBOOLEAN// Type of numeric data
        ); // Default value

    // System.out.println("OBJY: Propose Creating new class: " + embeddedClassName());

    // ObjySchema.getTopModule().propose_new_class(propClass);
    if (!inProcess)
    {
      top_mod.activate_proposals(true, true);
    }

    return bDone;
  }

  /**
	 *
	 */
  public boolean createSchema(Proposed_Class proposedClass, EStructuralFeature feature)
  {
    if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG.trace("Adding attribute " + feature.getName() + "  " + this.getClass().getName());
    }

    // create an embedded class (attributeType, attributeIsNull).
    // TODO - we might need to move this to the .objectivity.schema, since it's
    // independent of the model classes.

    if (getEmbeddedClass() == null && !createEmbeddedClass())
    {
      return false;
    }

    // create array of embedded class type.
    proposedClass.add_varray_attribute(com.objy.as.app.d_Module.LAST, d_Access_Kind.d_PUBLIC, // Access kind
        getAttributeName(feature), // Attribute name
        1, embeddedClassName());

    return false;
  }

  /**
   * TODO - this is a simple change to the attribute, make it handle more complex cases. I also don't think it does
   * handle the arrays.
   * 
   * @param proposedooClass
   * @param feature
   */
  public void modifySchema(Proposed_Class proposedooClass, EStructuralFeature feature)
  {
    Proposed_Property prop = proposedooClass.resolve_property(getAttributeName(feature));

    if (prop instanceof Proposed_Basic_Attribute)
    {
      Proposed_Basic_Attribute attr = (Proposed_Basic_Attribute)prop;
      attr.change_base_type(getObjyBaseType());
    }
  }

  /**
   * TBD - Fixed size array attributes can't be accessed, so we can't validate This!!! OFJ (Fix it)
   */
  public boolean validate(d_Attribute dAttribute, EStructuralFeature feature)
  {
    System.out.println(">>>OBJYIMPL: NumericManyTypeMapper.validate() - not implemented.");
    return true;
    // d_Class varrayClass = dAttribute.class_type_of();
    // d_Class embeddedClass = varrayClass.
    // Class_Position position = embeddedClass.position_in_class(getAttributeName(feature));
    // //d_Type type = ooAttribute.type_of();
    // d_Type type = embeddedClass.attribute_at_position(position).type_of();
    // if (TRACER_DEBUG.isEnabled()) {
    // TRACER_DEBUG.trace(getAttributeName(feature) + " "
    // + ((Basic_Type) type).base_type() + " basic type "
    // + type.is_basic_type() + " - " + getObjyBaseType());
    // }
    // return type.is_basic_type()
    // && ((Basic_Type) type).base_type() == getObjyBaseType();
  }

  // ---------------------------------
  // Object
  // ---------------------------------

  public void setValue(ObjyObject objyObject, EStructuralFeature feature, int index, Object newValue)
  {
    // System.out.println("OBJY: Set value in VArray at index: "+ index + " - value: " + newValue);
    boolean isNull = newValue == null;
    Numeric_Value numericValue = toNumericValue(newValue);
    Numeric_Value isNullValue = isNull ? numericTrue : numericFalse;

    Class_Object embedded = getArray(objyObject, feature).get_class_obj(index);

    embedded.set_numeric(valueAttributePosition, numericValue);
    embedded.set_numeric(nullAttributePosition, isNullValue);
  }

  // get a single object/value at index.
  public Object getValue(ObjyObject objyObject, EStructuralFeature feature, int index)
  {
    Numeric_Value numericValue = null;

    Class_Object embedded = getArray(objyObject, feature).get_class_obj(index);

    numericValue = embedded.get_numeric(valueAttributePosition);
    boolean isNull = embedded.get_numeric(nullAttributePosition).booleanValue();

    return fromNumericValue(numericValue, isNull);
  }

  // remove a single value at index, it will set the value to default, and mark it
  // as "null", i.e. unset.
  public Object remove(ObjyObject objyObject, EStructuralFeature feature, int index)
  {
    // we'll just get the original value, and set the value to null.
    // Numeric_Value numericValue = null;

    long size = (int)getArray(objyObject, feature).size();
    for (int i = index; i < size - 1; i++)
    {
      setValue(objyObject, feature, i, getValue(objyObject, feature, i + 1));
    }
    // resize the array.
    getArray(objyObject, feature).resize(size - 1);
    /*
     * Class_Object embedded = getArray(objyObject, feature).get_class_obj(index); numericValue =
     * embedded.get_numeric(valueAttributePosition); boolean isNull =
     * embedded.get_numeric(nullAttributePosition).booleanValue(); Object oldValue = fromNumericValue(numericValue,
     * isNull); numericValue = toNumericValue(null); embedded.set_numeric(valueAttributePosition, numericValue);
     * embedded.set_numeric(nullAttributePosition, numericTrue);
     */
    return null;
  }

  // add value at index (extend the collection size).
  public void add(ObjyObject objyObject, EStructuralFeature feature, int index, Object value)
  {
    int arraySize = size(objyObject, feature);
    // System.out.println("OBJY: Adding object inside VArray at index: "+ index + " - value: " + value);
    if (index < arraySize - 1)
    {
      // throw new UnsupportedOperationException("adding object inside VArray?!!... Implement Me!!!");
      // resize the VArray.
      VArray_Object array = getArray(objyObject, feature);
      array.resize(arraySize + 1);
      for (int i = arraySize; i > index; i--)
      {
        Class_Object newEmbedded = array.get_class_obj(i);
        Class_Object oldEmbedded = array.get_class_obj(i - 1);
        newEmbedded.set_numeric(valueAttributePosition, oldEmbedded.get_numeric(valueAttributePosition));
        newEmbedded.set_numeric(nullAttributePosition, oldEmbedded.get_numeric(nullAttributePosition));
      }
    }
    if (index != -1 && index > arraySize)
    {
      throw new UnsupportedOperationException("adding object beyond VArray size()?!!... Implement Me!!!");
    }

    getArray(objyObject, feature).resize(arraySize + 1);

    setValue(objyObject, feature, index, value);
  }

  public// add all objects starting from an index. (extend the collection size).
  void addAll(ObjyObject objyObject, EStructuralFeature feature, int index, Object[] values)
  {
    // System.out.println("OBJY: AddAll objects inside VArray at index: "+ index + " - values: " + values);
    int arraySize = size(objyObject, feature);
    if (index < arraySize - 1)
    {
      throw new UnsupportedOperationException("adding objects inside VArray?!!... Implement Me!!!");
    }

    if (index != -1 && index > arraySize)
    {
      throw new UnsupportedOperationException("adding objects beyond VArray size()?!!... Implement Me!!!");
    }

    int newSize = arraySize + values.length;
    getArray(objyObject, feature).resize(newSize);

    for (int i = 0; i < values.length; i++)
    {
      setValue(objyObject, feature, arraySize + i, values[i]);
    }
  }

  // clear all collection.
  public void clear(ObjyObject objyObject, EStructuralFeature feature)
  {
    // set the varray size to 0.
    getArray(objyObject, feature).resize(0);
  }

  // this is similar to addAll, but it replaces the existing ones.
  public void setAll(ObjyObject objyObject, EStructuralFeature feature, int index, Object[] newValues)
  {

    VArray_Object array = getArray(objyObject, feature);

    array.resize(newValues.length);
    for (int i = 0; i < newValues.length; i++)
    {
      // TODO - we might need to optimize this!!!
      setValue(objyObject, feature, i, newValues[i]);
    }
  }

  // get all objects/values starting from an index.
  public Object[] getAll(ObjyObject objyObject, EStructuralFeature feature, int index, int chunkSize)
  {
    int size = size(objyObject, feature);

    if (chunkSize != CDORevision.UNCHUNKED)
    {
      size = Math.min(size, chunkSize);
    }

    Object[] values = new Object[size];

    // TODO - we might need to optimize this!!!!
    for (int i = 0; i < size; i++)
    {
      values[i] = getValue(objyObject, feature, i + index);
    }
    return values;
  }

  // return the size of the collection.
  public int size(ObjyObject objyObject, EStructuralFeature feature)
  {
    return (int)getArray(objyObject, feature).size();
  }

  public void delete(ObjyObject objyObject, EStructuralFeature feature)
  {
    throw new UnsupportedOperationException("Implement me!!");
  }

  public void move(ObjyObject objyObject, EStructuralFeature feature, int targetIndex, int sourceIndex)
  {
    if (targetIndex == sourceIndex)
    {
      return;
    }

    // get the object at sourceIndex.
    Object value = getValue(objyObject, feature, sourceIndex);

    // long size = (int)getArray(objyObject, feature).size();
    // TODO - check boundaries...

    if (sourceIndex > targetIndex)
    {
      for (int i = sourceIndex; i > targetIndex; i--)
      {
        setValue(objyObject, feature, i, getValue(objyObject, feature, i - 1));
      }
    }
    else if (sourceIndex < targetIndex)
    {
      for (int i = sourceIndex; i < targetIndex; i++)
      {
        setValue(objyObject, feature, i, getValue(objyObject, feature, i + 1));
      }
    }
    // set the saved value at target
    setValue(objyObject, feature, targetIndex, value);

  }

  public void initialize(Class_Object classObject, EStructuralFeature feature)
  {
    // TODO - verify if we need to do any initialization!!!
  }

  protected VArray_Object getArray(ObjyObject objyObject, EStructuralFeature feature)
  {
    Class_Position position = getAttributePosition(objyObject, feature);
    return objyObject.get_varray(position);
  }

  // ------------------------------------------------------------------------
  // ------------------- Various types --------------------------
  // ------------------------------------------------------------------------

  // ---------------------------
  // Boolean
  // ---------------------------
  public static class TMBoolean extends NumericManyTypeMapper
  {
    @Override
    protected ooBaseType getObjyBaseType()
    {
      return ooBaseType.ooBOOLEAN;
    }

    @Override
    protected Object fromNumericValue(Numeric_Value numericValue, boolean isNull)
    {
      Boolean value = null;
      if (!isNull)
      {
        value = numericValue.booleanValue();
      }
      return value;
    }

    @Override
    protected Numeric_Value toNumericValue(Object value)
    {
      if (value == null)
      {
        return new Numeric_Value(false);
      }

      return new Numeric_Value(((Boolean)value).booleanValue());
    }
  }

  // ---------------------------
  // Byte
  // ---------------------------
  public static class TMByte extends NumericManyTypeMapper
  {
    @Override
    public ooBaseType getObjyBaseType()
    {
      return ooBaseType.ooINT8;
    }

    @Override
    protected Object fromNumericValue(Numeric_Value numericValue, boolean isNull)
    {
      Byte value = null;
      if (!isNull)
      {
        value = numericValue.byteValue();
      }
      return value;

    }

    @Override
    protected Numeric_Value toNumericValue(Object value)
    {
      if (value == null)
      {
        return new Numeric_Value(0);
      }

      return new Numeric_Value(((Byte)value).byteValue());
    }
  }

  // ---------------------------
  // Char
  // ---------------------------
  public static class TMChar extends NumericManyTypeMapper
  {
    @Override
    public ooBaseType getObjyBaseType()
    {
      return ooBaseType.ooINT8;
    }

    @Override
    protected Object fromNumericValue(Numeric_Value numericValue, boolean isNull)
    {
      Character value = null;
      if (!isNull)
      {
        value = numericValue.charValue();
      }
      return value;

    }

    @Override
    protected Numeric_Value toNumericValue(Object value)
    {
      if (value == null)
      {
        return new Numeric_Value(0);
      }

      return new Numeric_Value(((Character)value).charValue());
    }
  }

  // ---------------------------
  // Date
  // ---------------------------
  public static class TMDate extends NumericManyTypeMapper
  {
    @Override
    public ooBaseType getObjyBaseType()
    {
      return ooBaseType.ooINT64;
    }

    @Override
    protected Object fromNumericValue(Numeric_Value numericValue, boolean isNull)
    {
      Date value = null;
      if (!isNull)
      {
        value = new Date(numericValue.longValue());
      }
      return value;

    }

    @Override
    protected Numeric_Value toNumericValue(Object value)
    {
      if (value == null)
      {
        return new Numeric_Value(0);
      }

      return new Numeric_Value(((Date)value).getTime());
    }
  }

  // ---------------------------
  // Double
  // ---------------------------
  public static class TMDouble extends NumericManyTypeMapper
  {
    @Override
    public ooBaseType getObjyBaseType()
    {
      return ooBaseType.ooFLOAT64;
    }

    @Override
    protected Object fromNumericValue(Numeric_Value numericValue, boolean isNull)
    {
      Double value = null;
      if (!isNull)
      {
        value = numericValue.doubleValue();
      }
      return value;

    }

    @Override
    protected Numeric_Value toNumericValue(Object value)
    {
      if (value == null)
      {
        return new Numeric_Value(0.0);
      }

      return new Numeric_Value(((Double)value).doubleValue());
    }
  }

  // ---------------------------
  // Float
  // ---------------------------
  public static class TMFloat extends NumericManyTypeMapper
  {
    @Override
    protected ooBaseType getObjyBaseType()
    {
      return ooBaseType.ooFLOAT64;
    }

    @Override
    protected Object fromNumericValue(Numeric_Value numericValue, boolean isNull)
    {
      Float value = null;
      if (!isNull)
      {
        value = numericValue.floatValue();
      }
      return value;

    }

    @Override
    protected Numeric_Value toNumericValue(Object value)
    {
      if (value == null)
      {
        return new Numeric_Value(0.0);
      }

      return new Numeric_Value(((Float)value).floatValue());
    }
  }

  // ---------------------------
  // Integer
  // ---------------------------
  public static class TMInteger extends NumericManyTypeMapper
  {
    @Override
    public ooBaseType getObjyBaseType()
    {
      return ooBaseType.ooINT32;
    }

    @Override
    protected Object fromNumericValue(Numeric_Value numericValue, boolean isNull)
    {
      Integer value = null;
      if (!isNull)
      {
        value = numericValue.intValue();
      }
      return value;
    }

    @Override
    protected Numeric_Value toNumericValue(Object value)
    {
      if (value == null)
      {
        return new Numeric_Value(0);
      }

      return new Numeric_Value(((Integer)value).intValue());
    }
  }

  // ---------------------------
  // Long
  // ---------------------------
  public static class TMLong extends NumericManyTypeMapper
  {
    @Override
    public ooBaseType getObjyBaseType()
    {
      return ooBaseType.ooINT64;
    }

    @Override
    protected Object fromNumericValue(Numeric_Value numericValue, boolean isNull)
    {
      Long value = null;
      if (!isNull)
      {
        value = numericValue.longValue();
      }
      return value;

    }

    @Override
    protected Numeric_Value toNumericValue(Object value)
    {
      if (value == null)
      {
        return new Numeric_Value(0);
      }

      return new Numeric_Value(((Long)value).longValue());
    }
  }

  // ---------------------------
  // Short
  // ---------------------------
  public static class TMShort extends NumericManyTypeMapper
  {
    @Override
    public ooBaseType getObjyBaseType()
    {
      return ooBaseType.ooINT16;
    }

    @Override
    protected Object fromNumericValue(Numeric_Value numericValue, boolean isNull)
    {
      Short value = null;
      if (!isNull)
      {
        value = numericValue.shortValue();
      }
      return value;
    }

    @Override
    protected Numeric_Value toNumericValue(Object value)
    {
      if (value == null)
      {
        return new Numeric_Value(0);
      }

      return new Numeric_Value(((Short)value).shortValue());
    }
  }

}
