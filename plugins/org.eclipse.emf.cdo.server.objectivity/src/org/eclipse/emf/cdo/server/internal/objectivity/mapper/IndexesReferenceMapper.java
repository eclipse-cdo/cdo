/*
 * Copyright (c) 2010-2012 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.objy.as.app.Class_Object;
import com.objy.as.app.Proposed_Class;
import com.objy.as.app.d_Attribute;

/**
 * @author Simon McDuff
 */

// TODO - need implementation.
public class IndexesReferenceMapper implements IManyTypeMapper
{

  public boolean createSchema(Proposed_Class proposedClasses, EStructuralFeature feature)
  {
    EAnnotation annotation = feature.getEAnnotation("ooindex");
    if (annotation != null)
    {
      // String oclString = annotation.getDetails().get("key");

    }
    return true;
  }

  public Object getValue(ObjyObject internal, EStructuralFeature feature, int index)
  {
    throw new UnsupportedOperationException();
  }

  public void setValue(ObjyObject internal, EStructuralFeature feature, int index, Object newValue)
  {
    throw new UnsupportedOperationException();
  }

  public boolean validate(d_Attribute ooAttribute, EStructuralFeature feature)
  {
    // TODO Auto-generated method stub
    return true;
  }

  public void add(ObjyObject classObject, EStructuralFeature feature, int index, Object value)
  {
    // Index element!!!
    throw new UnsupportedOperationException("Implement me!!");
  }

  public void addAll(ObjyObject classObject, EStructuralFeature feature, int index, Object[] value)
  {
    throw new UnsupportedOperationException("Implement me!!");
  }

  public int size(ObjyObject classObject, EStructuralFeature feature)
  {
    throw new UnsupportedOperationException();
  }

  public void initialize(Class_Object class_Object, EStructuralFeature feature)
  {
    throw new UnsupportedOperationException("Implement me!!");
  }

  public void modifySchema(Proposed_Class proposedooClass, EStructuralFeature feature)
  {
    throw new UnsupportedOperationException("Implement me!!");
  }

  public void clear(ObjyObject classObject, EStructuralFeature feature)
  {
    throw new UnsupportedOperationException("Implement me!!");
  }

  public Object remove(ObjyObject classObject, EStructuralFeature feature, int index)
  {
    throw new UnsupportedOperationException("Implement me!!");
  }

  public void delete(ObjyObject class_Object, EStructuralFeature feature)
  {
  }

  public Object[] getAll(ObjyObject objyObject, EStructuralFeature feature, int index, int chunkSize)
  {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Implement me!!");
  }

  public void setAll(ObjyObject objyObject, EStructuralFeature feature, int index, Object[] newValue)
  {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Implement me!!");
  }

  public void move(ObjyObject objyObject, EStructuralFeature feature, int targetIndex, int sourceIndex)
  {
    throw new UnsupportedOperationException("Implement me!!");
  }

}
