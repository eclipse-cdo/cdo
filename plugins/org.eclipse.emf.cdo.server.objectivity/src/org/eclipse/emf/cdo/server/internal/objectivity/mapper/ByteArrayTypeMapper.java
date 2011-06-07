/**
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
package org.eclipse.emf.cdo.server.internal.objectivity.mapper;

import org.eclipse.emf.cdo.server.internal.objectivity.bundle.OM;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyObject;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EStructuralFeature;

import com.objy.as.app.Class_Object;
import com.objy.as.app.Numeric_Value;
import com.objy.as.app.Proposed_Class;
import com.objy.as.app.VArray_Object;
import com.objy.as.app.d_Access_Kind;
import com.objy.as.app.d_Attribute;
import com.objy.as.app.d_Type;
import com.objy.as.app.ooBaseType;

/**
 * @author Simon McDuff
 */
// This is a special class compared to the other array of simple types.
public class ByteArrayTypeMapper extends BasicTypeMapper implements ISingleTypeMapper
{
  private static final ContextTracer TRACER_DEBUG = new ContextTracer(OM.DEBUG, ByteArrayTypeMapper.class);

  static ByteArrayTypeMapper INSTANCE = new ByteArrayTypeMapper();

  protected ooBaseType getObjyBaseType()
  {
    return ooBaseType.ooINT8;
  }

  public Object getValue(ObjyObject objyObject, EStructuralFeature feature)
  {
    // Class_Position position = getAttributePosition(objyObject, feature);
    String attributeName = getAttributeName(feature);

    VArray_Object vArray = objyObject.get_varray(attributeName/* position */);

    // Class_Position nullPosition = getNullAttributePosition(objyObject, feature);
    String nullAttributeName = getNullAttributeName(feature);
    boolean isNull = objyObject.get_numeric(nullAttributeName/* nullPosition */).booleanValue();

    if (isNull)
    {
      return null;
    }

    int size = (int)vArray.size();
    byte byteArray[] = new byte[size];
    for (int i = 0; i < size; i++)
    {
      Numeric_Value value = vArray.get_numeric(i);
      byteArray[i] = value.byteValue();
    }
    return byteArray;
  }

  public void setValue(ObjyObject objyObject, EStructuralFeature feature, Object newValue)
  {
    // Class_Position position = getAttributePosition(objyObject, feature);
    String attributeName = getAttributeName(feature);

    VArray_Object vArray = objyObject.get_varray(attributeName/* position */);

    // Class_Position nullPosition = getNullAttributePosition(objyObject, feature);
    String nullAttributeName = getNullAttributeName(feature);

    Numeric_Value isNullValue = newValue == null ? numericTrue : numericFalse;

    objyObject.set_numeric(nullAttributeName /* nullPosition */, isNullValue);

    // System.out.println("OID: " + objyObject.ooId().getStoreString() + " - START work");
    if (newValue == null)
    {
      vArray.resize(0);
      return;
    }

    byte byteArray[] = (byte[])newValue;

    if (vArray.size() != byteArray.length)
    {
      vArray.resize(byteArray.length);
    }

    for (int i = 0; i < byteArray.length; i++)
    {
      Byte byteValue = byteArray[i];
      Numeric_Value numericValue = new Numeric_Value(byteValue);
      vArray.set_numeric(i, numericValue);
    }
    // System.out.println("OID: " + objyObject.ooId().getStoreString() + " - DONE.");
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

    proposedClass.add_varray_attribute(com.objy.as.app.d_Module.LAST, d_Access_Kind.d_PUBLIC, // Access kind
        getAttributeName(feature), // Attribute name
        1, // # elements in fixed-size array
        getObjyBaseType()); // Default value

    proposedClass.add_basic_attribute(com.objy.as.app.d_Module.LAST, d_Access_Kind.d_PUBLIC, // Access kind
        getNullAttributeName(feature), // Attribute name
        1, // # elements in fixed-size array
        ooBaseType.ooBOOLEAN // Default value
        );

    return false;
  }

  public boolean validate(d_Attribute ooAttribute, EStructuralFeature feature)
  {
    d_Type type = ooAttribute.type_of();

    return type.is_varray_basic_type();
  }

  // TODO - this is not the most optimized version fo the code.
  // we are trying to finish functionality for now 100202:IS
  // public void add(ObjyObject objyObject, EStructuralFeature feature,
  // Class_Position position, int index, Object value)
  // {
  // if (index < size(objyObject, feature, position))
  // {
  // throw new UnsupportedOperationException("adding object inside VArray... Implement Me!!!");
  // }
  //
  // VArray_Object vArray = objyObject.ooClassObject().get_varray(position);
  // Numeric_Value numericValue = new Numeric_Value((Byte)value);
  // vArray.extend(numericValue);
  // }

  public Object remove(ObjyObject objyObject, EStructuralFeature feature)
  {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Implement me!!");
  }

  public void delete(ObjyObject objyObject, EStructuralFeature feature)
  {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Implement me!!");
  }

  public void initialize(Class_Object classObject, EStructuralFeature feature)
  {
    // TODO Auto-generated method stub
    // throw new UnsupportedOperationException("Implement me!!");
    // at least rest the varray...
    // Class_Position position = classObject.type_of().position_in_class(feature.getName());
    VArray_Object vArray = classObject.nget_varray(feature.getName());
    vArray.resize(0);
  }

  public void modifySchema(Proposed_Class proposedooClass, EStructuralFeature feature)
  {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Implement me!!");
  }

}
