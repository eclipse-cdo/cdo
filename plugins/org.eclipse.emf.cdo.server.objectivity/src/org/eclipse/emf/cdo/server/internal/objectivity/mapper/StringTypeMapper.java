/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyObject;

import org.eclipse.emf.ecore.EStructuralFeature;

import com.objy.as.app.Class_Object;
import com.objy.as.app.Numeric_Value;
import com.objy.as.app.Proposed_Class;
import com.objy.as.app.String_Value;
import com.objy.as.app.d_Access_Kind;
import com.objy.as.app.d_Attribute;
import com.objy.as.app.ooBaseType;
import com.objy.db.ObjyRuntimeException;

/**
 * @author Simon McDuff
 */
public class StringTypeMapper extends BasicTypeMapper implements ISingleTypeMapper
{
  public static StringTypeMapper INSTANCE = new StringTypeMapper();

  @Override
  protected String getNullAttributeName(EStructuralFeature feature)
  {
    return feature.getName() + "_isNull";
  }

  public boolean createSchema(Proposed_Class proposedClass, EStructuralFeature feature)
  {
    try
    {

      proposedClass.add_embedded_class_attribute(com.objy.as.app.d_Module.LAST, // Access kind
          d_Access_Kind.d_PUBLIC, // Access kind
          feature.getName(), // Attribute name
          1, // # elements in fixed-size array
          "ooUtf8String"); // Default value

      proposedClass.add_basic_attribute(com.objy.as.app.d_Module.LAST, d_Access_Kind.d_PUBLIC, // Access kind
          getNullAttributeName(feature), // Attribute name
          1, // # elements in fixed-size array
          ooBaseType.ooBOOLEAN // Default value
          );
    }
    catch (ObjyRuntimeException ex)
    {
      ex.printStackTrace();
    }
    return false;
  }

  public Object getValue(ObjyObject objyObject, EStructuralFeature feature)
  {
    // Class_Position position = getAttributePosition(objyObject, feature);
    String attributeName = getAttributeName(feature);
    // Class_Position nullPosition = getNullAttributePosition(objyObject, feature);
    String nullAttributeName = getNullAttributeName(feature);
    String_Value stringValue = objyObject.get_string(attributeName/* position */);
    boolean isNull = objyObject.get_numeric(nullAttributeName/* nullPosition */).booleanValue();
    Object value = null;

    if (!isNull)
    {
      value = stringValue.toString();
      // else if (feature.isUnsettable())
      // value = CDORevisionData.NIL;
    }

    return value;
  }

  public void setValue(ObjyObject objyObject, EStructuralFeature feature, Object newValue)
  {
    boolean isNull = newValue == null || newValue == CDORevisionData.NIL;
    // Class_Position nullPosition = getNullAttributePosition(objyObject, feature);
    String nullAttributeName = getNullAttributeName(feature);

    if (!isNull)
    {
      // Class_Position position = getAttributePosition(objyObject, feature);
      String attributeName = getAttributeName(feature);
      String_Value stringValue = objyObject.get_string(attributeName/* position */);
      stringValue.update();
      stringValue.set((String)newValue);
    }
    Numeric_Value isNullValue = newValue == null ? numericTrue : numericFalse;
    objyObject.set_numeric(nullAttributeName/* nullPosition */, isNullValue);
  }

  public Object remove(ObjyObject objyObject, EStructuralFeature feature)
  {
    throw new UnsupportedOperationException("Implement me!!");
  }

  public void delete(ObjyObject objyObject, EStructuralFeature feature)
  {
    // we could set the string value to "", but it would be easier to just set
    // the numeric _null to "true"
    // Class_Position position = getNullAttributePosition(objyObject, feature);
    String nullAttributeName = getNullAttributeName(feature);
    objyObject.set_numeric(nullAttributeName/* position */, numericTrue);
    // throw new UnsupportedOperationException("Implement me!!");
  }

  public void initialize(Class_Object classObject, EStructuralFeature feature)
  {
    // Class_Position position = classObject.type_of().position_in_class(getNullAttributeName(feature));
    classObject.nset_numeric(getNullAttributeName(feature), numericTrue);
  }

  public void modifySchema(Proposed_Class proposedooClass, EStructuralFeature feature)
  {
    // TODO Auto-generated method stub

  }

  public boolean validate(d_Attribute ooAttribute, EStructuralFeature feature)
  {
    // TODO Auto-generated method stub
    System.out.println(">>>OBJYIMPL: StringTypeMapper.validate() - not implemented.");
    return true;
  }
}
