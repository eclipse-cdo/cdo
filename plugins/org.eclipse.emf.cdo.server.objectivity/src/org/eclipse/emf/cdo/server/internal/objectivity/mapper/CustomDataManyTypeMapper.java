/*
 * Copyright (c) 2010-2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ibrahim Sallam - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.objectivity.mapper;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EStructuralFeature;

/***
 * Overrides StringManyTypeMapper, and implement the two functions to convert a string to an object and vice versa.
 *
 * @author Ibrahim Sallam
 */
public class CustomDataManyTypeMapper extends StringManyTypeMapper
{

  public static CustomDataManyTypeMapper INSTANCE = new CustomDataManyTypeMapper();

  @Override
  protected String stringFromObject(EStructuralFeature feature, Object objectValue)
  {
    EDataType dataType = (EDataType)feature.getEType();
    EFactory factory = dataType.getEPackage().getEFactoryInstance();
    String stringValue = factory.convertToString(dataType, objectValue);
    return stringValue;
  }

  @Override
  protected Object objectFromString(EStructuralFeature feature, String stringValue)
  {
    EDataType dataType = (EDataType)feature.getEType();
    EFactory factory = dataType.getEPackage().getEFactoryInstance();
    Object value = null;
    value = factory.createFromString(dataType, stringValue);
    return value;
  }

}
