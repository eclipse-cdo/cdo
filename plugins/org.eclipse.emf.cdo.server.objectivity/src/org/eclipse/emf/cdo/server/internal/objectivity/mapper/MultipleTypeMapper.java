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

import com.objy.as.app.Class_Object;
import com.objy.as.app.Proposed_Class;
import com.objy.as.app.d_Attribute;

import java.util.ArrayList;

// TODO - verify the need for this, and perhaps extend the implementation to the 
//        colletion of mappers.
public class MultipleTypeMapper extends BasicTypeMapper implements IManyTypeMapper
{
  ArrayList<IManyTypeMapper> mapperList = new ArrayList<IManyTypeMapper>();

  public void add(IManyTypeMapper attributeBridge)
  {
    mapperList.add(attributeBridge);
  }

  // @Override
  public boolean createSchema(Proposed_Class proposedClasses, EStructuralFeature feature)
  {
    boolean result = true;
    for (IManyTypeMapper manyMapper : mapperList)
    {
      result &= manyMapper.createSchema(proposedClasses, feature);
    }
    return result;
  }

  // @Override
  public Object getValue(ObjyObject class_Object, EStructuralFeature feature, int index)
  {
    return mapperList.get(0).getValue(class_Object, feature, index);
  }

  // @Override
  public void initialize(Class_Object class_Object, EStructuralFeature feature)
  {
    for (ITypeMapper manyMapper : mapperList)
    {
      manyMapper.initialize(class_Object, feature);
    }
  }

  // @Override
  public void modifySchema(Proposed_Class proposedooClass, EStructuralFeature feature)
  {
    for (IManyTypeMapper manyMapper : mapperList)
    {
      manyMapper.modifySchema(proposedooClass, feature);
    }
  }

  // @Override
  public void setValue(ObjyObject class_Object, EStructuralFeature feature, int index, Object newValue)
  {
    mapperList.get(0).setValue(class_Object, feature, index, newValue);
  }

  // @Override
  public boolean validate(d_Attribute ooAttribute, EStructuralFeature feature)
  {

    boolean result = true;
    for (ITypeMapper manyMapper : mapperList)
    {
      result &= manyMapper.validate(ooAttribute, feature);
    }
    return result;

  }

  // @Override
  public void add(ObjyObject classObject, EStructuralFeature feature, int index, Object value)
  {
    for (IManyTypeMapper manyMapper : mapperList)
    {
      manyMapper.add(classObject, feature, index, value);
    }

  }

  public void addAll(ObjyObject classObject, EStructuralFeature feature, int index, Object[] value)
  {
    for (IManyTypeMapper manyMapper : mapperList)
    {
      manyMapper.addAll(classObject, feature, index, value);
    }
  }

  // @Override
  public int size(ObjyObject classObject, EStructuralFeature feature)
  {
    // TODO Auto-generated method stub
    return mapperList.get(0).size(classObject, feature);
  }

  // @Override
  public void clear(ObjyObject classObject, EStructuralFeature feature)
  {
    for (IManyTypeMapper manyMapper : mapperList)
    {
      manyMapper.clear(classObject, feature);
    }

  }

  // @Override
  public Object remove(ObjyObject objyObject, EStructuralFeature feature, int index)
  {
    Object returnValue = null;
    int i = 0;
    for (IManyTypeMapper manyMapper : mapperList)
    {
      Object object = manyMapper.remove(objyObject, feature, index);
      if (i == 0)
      {
        returnValue = object;
      }
    }
    return returnValue;
  }

  public void delete(ObjyObject objyObject, EStructuralFeature feature)
  {
  }

  public Object[] getAll(ObjyObject objyObject, EStructuralFeature feature, int index, int chunkSize)
  {
    return mapperList.get(0).getAll(objyObject, feature, index, chunkSize);
  }

  public void setAll(ObjyObject objyObject, EStructuralFeature feature, int index, Object[] newValues)
  {
    mapperList.get(0).setAll(objyObject, feature, index, newValues);
  }

  public void move(ObjyObject objyObject, EStructuralFeature feature, int targetIndex, int sourceIndex)
  {
    mapperList.get(0).move(objyObject, feature, targetIndex, sourceIndex);
  }

}
