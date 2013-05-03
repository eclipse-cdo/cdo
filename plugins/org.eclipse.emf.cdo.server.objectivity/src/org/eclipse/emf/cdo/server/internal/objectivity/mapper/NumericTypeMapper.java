/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ibrahim Sallam - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.objectivity.mapper;

import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.server.internal.objectivity.bundle.OM;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyObject;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EStructuralFeature;

import com.objy.as.app.Basic_Type;
import com.objy.as.app.Class_Object;
import com.objy.as.app.Numeric_Value;
import com.objy.as.app.Proposed_Basic_Attribute;
import com.objy.as.app.Proposed_Class;
import com.objy.as.app.Proposed_Property;
import com.objy.as.app.d_Access_Kind;
import com.objy.as.app.d_Attribute;
import com.objy.as.app.d_Type;
import com.objy.as.app.ooBaseType;

import java.util.Date;

/**
 * @author Ibrahim Sallam
 */
public abstract class NumericTypeMapper extends BasicTypeMapper implements ISingleTypeMapper
{

  abstract protected Object fromNumericValue(Numeric_Value numericValue, boolean isNull);

  abstract protected Numeric_Value toNumericValue(Object value);

  abstract protected ooBaseType getObjyBaseType();

  private static final ContextTracer TRACER_DEBUG = new ContextTracer(OM.DEBUG, NumericTypeMapper.class);

  public static NumericTypeMapper.TMBoolean TMBOOLEAN = new TMBoolean();

  public static NumericTypeMapper.TMByte TMBYTE = new TMByte();

  public static NumericTypeMapper.TMChar TMCHAR = new TMChar();

  public static NumericTypeMapper.TMDate TMDATE = new TMDate();

  public static NumericTypeMapper.TMDouble TMDOUBLE = new TMDouble();

  public static NumericTypeMapper.TMFloat TMFLOAT = new TMFloat();

  public static NumericTypeMapper.TMInteger TMINTEGER = new TMInteger();

  public static NumericTypeMapper.TMLong TMLONG = new TMLong();

  public static NumericTypeMapper.TMShort TMSHORT = new TMShort();

  // ---------------------------------
  // Schema
  // ---------------------------------
  /**
	 *
	 */
  public boolean createSchema(Proposed_Class proposedClass, EStructuralFeature feature)
  {
    if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG.trace("Adding attribute " + feature.getName() + "  " + this.getClass().getName());
    }

    proposedClass.add_basic_attribute(com.objy.as.app.d_Module.LAST, d_Access_Kind.d_PUBLIC, // Access kind
        getAttributeName(feature), // Attribute name
        1, // # elements in fixed-size array
        getObjyBaseType() // Default value
        );

    proposedClass.add_basic_attribute(com.objy.as.app.d_Module.LAST, d_Access_Kind.d_PUBLIC, // Access kind
        getNullAttributeName(feature), // Attribute name
        1, // # elements in fixed-size array
        ooBaseType.ooBOOLEAN // Default value
        );
    return false;
  }

  /**
   * TODO - this is a simple change to the attribute, make it handle more complex cases.
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
	 *
	 */
  public boolean validate(d_Attribute ooAttribute, EStructuralFeature feature)
  {
    d_Type type = ooAttribute.type_of();
    if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG.trace(getAttributeName(feature) + " " + ((Basic_Type)type).base_type() + " basic type "
          + type.is_basic_type() + " - " + getObjyBaseType());
    }
    return type.is_basic_type() && ((Basic_Type)type).base_type() == getObjyBaseType();
  }

  /**
   * The numeric attribute is an embedded class with (basic/boolean) pair. The boolean represent objects of basic types
   * with null value. TODO - I thought this should work.... find out why not!!!
   */
  // public boolean validate(d_Attribute dAttribute, EStructuralFeature feature)
  // {
  // d_Class embeddedClass = dAttribute.class_type_of();
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
  // }

  // ---------------------------------
  // Object
  // ---------------------------------

  public Object getValue(ObjyObject objyObject, EStructuralFeature feature)
  {
    Numeric_Value numericValue = null;
    // Class_Position nullPosition = getNullAttributePosition(objyObject, feature);
    String nullAttributeName = getNullAttributeName(feature);

    boolean isNull = objyObject.get_numeric(nullAttributeName/* nullPosition */).booleanValue();

    // if (isNull && feature.isUnsettable())
    // return CDORevisionData.NIL;

    if (!isNull)
    {
      // Class_Position position = getAttributePosition(objyObject, feature);
      String attributeName = getAttributeName(feature);
      numericValue = objyObject.get_numeric(attributeName/* position */);
    }

    return fromNumericValue(numericValue, isNull);
  }

  public void setValue(ObjyObject objyObject, EStructuralFeature feature, Object newValue)
  {
    boolean isNull = newValue == null || newValue == CDORevisionData.NIL;
    Numeric_Value isNullValue = isNull ? numericTrue : numericFalse;
    // Class_Position nullPosition = getNullAttributePosition(objyObject, feature);
    String nullAttributeName = getNullAttributeName(feature);

    if (!isNull)
    {
      // Class_Position position = getAttributePosition(objyObject, feature);
      String attributeName = getAttributeName(feature);
      Numeric_Value numericValue = toNumericValue(newValue);
      objyObject.set_numeric(attributeName/* position */, numericValue);
    }

    objyObject.set_numeric(nullAttributeName/* nullPosition */, isNullValue);
  }

  public Object remove(ObjyObject objyObject, EStructuralFeature feature)
  {
    throw new UnsupportedOperationException("Implement me!!");
  }

  public void delete(ObjyObject objyObject, EStructuralFeature feature)
  {
    // throw new UnsupportedOperationException("Implement me!!");
    // we just set the numeric _null to "true"
    // Class_Position position = getNullAttributePosition(objyObject, feature);
    // String nullAttributeName = getNullAttributeName(feature);
    objyObject.set_numeric(getNullAttributeName(feature)/* position */, numericTrue);
  }

  public void initialize(Class_Object classObject, EStructuralFeature feature)
  {
    // Class_Position position = classObject.type_of().position_in_class(getNullAttributeName(feature));
    classObject.nset_numeric(getNullAttributeName(feature) /* position */, numericTrue);
  }

  // various numeric types....
  // ---------------------------
  // Boolean
  // ---------------------------
  public static class TMBoolean extends NumericTypeMapper
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
  public static class TMByte extends NumericTypeMapper
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
  public static class TMChar extends NumericTypeMapper
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
  public static class TMDate extends NumericTypeMapper
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
  public static class TMDouble extends NumericTypeMapper
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
  public static class TMFloat extends NumericTypeMapper
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
  public static class TMInteger extends NumericTypeMapper
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
  public static class TMLong extends NumericTypeMapper
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
  public static class TMShort extends NumericTypeMapper
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

  // ---------------------------
  // Boolean
  // ---------------------------

}
