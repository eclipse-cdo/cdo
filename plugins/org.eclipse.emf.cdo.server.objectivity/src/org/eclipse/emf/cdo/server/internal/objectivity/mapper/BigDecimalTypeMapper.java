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

import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyObject;

import org.eclipse.emf.ecore.EStructuralFeature;

import com.objy.as.app.Class_Position;
import com.objy.as.app.Numeric_Value;
import com.objy.as.app.Proposed_Class;
import com.objy.as.app.String_Value;
import com.objy.as.app.d_Attribute;

import java.math.BigDecimal;

/**
 * @author Simon McDuff
 */
public class BigDecimalTypeMapper extends StringTypeMapper
{
  public static BigDecimalTypeMapper INSTANCE = new BigDecimalTypeMapper();

  @Override
  public Object getValue(ObjyObject objyObject, EStructuralFeature feature)
  {
    Class_Position nullPosition = getNullAttributePosition(objyObject, feature);
    boolean isNull = objyObject.get_numeric(nullPosition).booleanValue();
    Object value = null;

    if (!isNull)
    {
      Class_Position position = getAttributePosition(objyObject, feature);
      String_Value stringValue = objyObject.get_string(position);
      value = new BigDecimal(stringValue.toString());
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
    Class_Position nullPosition = getNullAttributePosition(objyObject, feature);

    boolean isNull = newValue == null || newValue == CDORevisionData.NIL;
    Numeric_Value isNullValue = isNull ? numericTrue : numericFalse;

    if (!isNull)
    {
      Class_Position position = getAttributePosition(objyObject, feature);
      String_Value stringValue = objyObject.get_string(position);
      stringValue.update();
      String strValue = ((BigDecimal)newValue).toString();
      stringValue.set((strValue == null ? "" : strValue));
    }
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
