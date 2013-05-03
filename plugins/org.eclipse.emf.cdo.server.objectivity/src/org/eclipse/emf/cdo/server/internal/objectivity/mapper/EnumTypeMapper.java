/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyObject;

import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Simon McDuff
 */
public class EnumTypeMapper extends NumericTypeMapper.TMInteger
{
  static EnumTypeMapper INSTANCE = new EnumTypeMapper();

  @Override
  public Object getValue(ObjyObject objyObject, EStructuralFeature feature)
  {
    // Integer intValue = (Integer) super.getValue(objyObject, feature);
    // /*
    // EEnum enumType = (EEnum)feature.getEType();
    // EEnumLiteral literal = enumType.getEEnumLiteral(intValue);
    // if (literal == null)
    // {
    // throw new IllegalStateException();
    // }
    // return literal.getInstance();
    // */
    // return intValue;
    return super.getValue(objyObject, feature);
  }

  @Override
  public void setValue(ObjyObject objyObject, EStructuralFeature feature, Object newValue)
  {
    Integer enumLiteral = (Integer)newValue;
    if (enumLiteral == null)
    {
      return;
    }
    super.setValue(objyObject, feature, enumLiteral);
    // Numeric_Value numericValue = new Numeric_Value(enumLiteral.intValue());
  }
}
