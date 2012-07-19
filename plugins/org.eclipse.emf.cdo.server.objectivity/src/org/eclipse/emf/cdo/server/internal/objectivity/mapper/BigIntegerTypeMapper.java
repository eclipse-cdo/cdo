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

import com.objy.as.app.Numeric_Value;
import com.objy.as.app.Proposed_Class;
import com.objy.as.app.String_Value;
import com.objy.as.app.d_Attribute;

import java.math.BigInteger;

/**
 * @author Ibrahim Sallam
 */
public class BigIntegerTypeMapper extends StringTypeMapper
{
  public static BigIntegerTypeMapper INSTANCE = new BigIntegerTypeMapper();

  @Override
  public Object getValue(ObjyObject objyObject, EStructuralFeature feature)
  {
    // Class_Position nullPosition = getNullAttributePosition(objyObject, feature);
    String nullAttributeName = getNullAttributeName(feature);

    boolean isNull = objyObject.get_numeric(nullAttributeName/* nullPosition */).booleanValue();
    Object value = null;

    if (!isNull)
    {
      // Class_Position position = getAttributePosition(objyObject, feature);
      String attributeName = getAttributeName(feature);

      String_Value stringValue = objyObject.get_string(attributeName/* position */);
      value = new BigInteger(stringValue.toString());
    }
    // else if (feature.isUnsettable())
    // {
    // value = CDORevisionData.NIL;
    // }

    return value;
  }

  @Override
  public void setValue(ObjyObject objyObject, EStructuralFeature feature, Object newValue)
  {
    String nullAttributeName = getNullAttributeName(feature);

    boolean isNull = newValue == null || newValue == CDORevisionData.NIL;
    Numeric_Value isNullValue = newValue == null ? numericTrue : numericFalse;

    if (!isNull)
    {
      String attributeName = getAttributeName(feature);

      String_Value stringValue = objyObject.get_string(attributeName/* position */);
      stringValue.update();
      String strValue = newValue.toString();
      stringValue.set(strValue);
    }
    objyObject.set_numeric(nullAttributeName/* nullPosition */, isNullValue);
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
