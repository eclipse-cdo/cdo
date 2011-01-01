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

import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyObject;

import org.eclipse.emf.ecore.EStructuralFeature;

import com.objy.as.app.Class_Position;
import com.objy.as.app.Numeric_Value;
import com.objy.as.app.Proposed_Class;
import com.objy.as.app.String_Value;
import com.objy.as.app.d_Attribute;

/**
 * @author Ibrahim Sallam
 */
public class CustomDataTypeMapper extends StringTypeMapper
{
  public static CustomDataTypeMapper INSTANCE = new CustomDataTypeMapper();

  @Override
  public Object getValue(ObjyObject objyObject, EStructuralFeature feature)
  {
    Class_Position position = getAttributePosition(objyObject, feature);
    Class_Position nullPosition = getNullAttributePosition(objyObject, feature);
    String_Value stringValue = objyObject.get_string(position);
    boolean isNull = objyObject.get_numeric(nullPosition).booleanValue();

    // EDataType dataType = (EDataType)feature.getEType();
    // EFactory factory = dataType.getEPackage().getEFactoryInstance();
    // Object value = null;
    // if (!isNull)
    // {
    // value = factory.createFromString(dataType, stringValue.toString());
    // }
    //
    // return value;
    if (isNull)
    {
      return null;
    }

    return stringValue.toString();
  }

  @Override
  public void setValue(ObjyObject objyObject, EStructuralFeature feature, Object newValue)
  {
    Class_Position position = getAttributePosition(objyObject, feature);
    Class_Position nullPosition = getNullAttributePosition(objyObject, feature);

    String_Value stringValue = objyObject.get_string(position);
    stringValue.update();

    // EDataType dataType = (EDataType)feature.getEType();
    // EFactory factory = dataType.getEPackage().getEFactoryInstance();
    // String valueAsString = factory.convertToString(dataType, newValue);
    //
    Numeric_Value isNullValue = newValue == null ? numericTrue : numericFalse;
    // String strValue = newValue == null ? null : valueAsString;
    stringValue.set((newValue == null ? "" : newValue.toString()));
    objyObject.set_numeric(nullPosition, isNullValue);
  }

  @Override
  public Object remove(ObjyObject objyObject, EStructuralFeature feature)
  {
    throw new UnsupportedOperationException("Implement me!!");
  }

  @Override
  public void modifySchema(Proposed_Class proposedooClass, EStructuralFeature feature)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean validate(d_Attribute ooAttribute, EStructuralFeature feature)
  {
    // TODO Auto-generated method stub
    return false;
  }
}
