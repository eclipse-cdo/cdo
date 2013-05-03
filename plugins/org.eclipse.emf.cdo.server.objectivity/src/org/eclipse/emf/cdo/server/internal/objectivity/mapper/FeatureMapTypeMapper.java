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

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyObject;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjySchema;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyFeatureMapArrayList;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyFeatureMapEntry;

import org.eclipse.emf.ecore.EStructuralFeature;

import com.objy.as.app.Class_Object;
import com.objy.as.app.Proposed_Class;
import com.objy.as.app.d_Access_Kind;
import com.objy.as.app.d_Attribute;
import com.objy.as.app.d_Class;
import com.objy.db.app.ooId;
import com.objy.db.app.ooObj;

/**
 * @author Simon McDuff
 */
public class FeatureMapTypeMapper extends BasicTypeMapper implements IManyTypeMapper
{
  static FeatureMapTypeMapper INSTANCE = new FeatureMapTypeMapper();

  static d_Class dClassObject = null;

  private static d_Class getArrayListClass()
  {
    if (dClassObject == null)
    {
      dClassObject = ObjySchema.getTopModule().resolve_class(ObjyFeatureMapArrayList.ClassName);
    }
    return dClassObject;
  }

  public boolean createSchema(Proposed_Class proposedClasses, EStructuralFeature feature)
  {
    proposedClasses.add_ref_attribute(com.objy.as.app.d_Module.LAST, // Access kind
        d_Access_Kind.d_PUBLIC, // Access kind
        feature.getName(), // Attribute name
        1, // # elements in fixed-size array
        ObjyFeatureMapArrayList.ClassName, false); // Default value // Default value

    return true;
  }

  public Object getValue(ObjyObject objyObject, EStructuralFeature feature, int index)
  {
    return getList(objyObject, feature).get(index);
  }

  public void setValue(ObjyObject objyObject, EStructuralFeature feature, int index, Object newValue)
  {
    getList(objyObject, feature).set(index, (ObjyFeatureMapEntry)newValue);
  }

  public ObjyFeatureMapArrayList getList(ObjyObject objyObject, EStructuralFeature feature)
  {
    // Class_Position position = getAttributePosition(objyObject, feature);
    String attributeName = getAttributeName(feature);
    ObjyFeatureMapArrayList list = (ObjyFeatureMapArrayList)objyObject.getFeatureList(attributeName/* position */);
    if (list == null)
    {
      list = new ObjyFeatureMapArrayList(objyObject.get_class_obj(attributeName/* position */));
      objyObject.setFeatureList(attributeName/* position */, list);
    }
    return list;
  }

  public int size(ObjyObject objyObject, EStructuralFeature feature)
  {
    return (int)getList(objyObject, feature).size();
  }

  public void add(ObjyObject objyObject, EStructuralFeature feature, int index, Object value)
  {
    getList(objyObject, feature).add(index, (ObjyFeatureMapEntry)value);
  }

  public void addAll(ObjyObject classObject, EStructuralFeature feature, int index, Object[] values)
  {
    getList(classObject, feature).addAll(index, values);
  }

  public boolean validate(d_Attribute ooAttribute, EStructuralFeature feature)
  {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Implement me!!");
  }

  public void initialize(Class_Object classObject, EStructuralFeature feature)
  {
    // create the reference.
    Class_Object newClassObject = Class_Object
        .new_persistent_object(getArrayListClass(), classObject.objectID(), false);
    // {
    // ObjyObjectManager.newInternalObjCount++;
    // }
    // Class_Position position = classObject.type_of().position_in_class(getAttributeName(feature));
    classObject.nset_ooId(getAttributeName(feature), newClassObject.objectID());
    // initialize the list structure.
    ObjyFeatureMapArrayList.initObject(newClassObject);
  }

  public void delete(ObjyObject objyObject, EStructuralFeature feature)
  {
    // Class_Position position = getAttributePosition(objyObject, feature);
    ooId tobeDeleted = objyObject.get_ooId(getAttributeName(feature)/* position */);
    ooObj objectToDelete = ooObj.create_ooObj(tobeDeleted);
    objectToDelete.delete();
  }

  public void clear(ObjyObject objyObject, EStructuralFeature feature)
  {
    getList(objyObject, feature).clear();
  }

  public Object remove(ObjyObject objyObject, EStructuralFeature feature, int index)
  {
    Object oldValue = getValue(objyObject, feature, index);

    getList(objyObject, feature).remove(index);

    return oldValue;
  }

  public Object[] getAll(ObjyObject objyObject, EStructuralFeature feature, int index, int chunkSize)
  {
    // throw new UnsupportedOperationException("Implement me!!");
    int size = (int)getList(objyObject, feature).size();
    if (chunkSize != CDORevision.UNCHUNKED)
    {
      size = Math.min(size, chunkSize);
    }
    Object[] objects = new Object[size - index];
    for (int i = 0; i < size; i++)
    {
      objects[i] = getValue(objyObject, feature, i + index);
    }
    return objects;
  }

  public void modifySchema(Proposed_Class proposedooClass, EStructuralFeature feature)
  {
    throw new UnsupportedOperationException("Implement me!!");
  }

  public void setAll(ObjyObject objyObject, EStructuralFeature feature, int index, Object[] newValues)
  {
    for (int i = 0; i < newValues.length; i++)
    {
      add(objyObject, feature, i, newValues[i]);
    }
  }

  public void move(ObjyObject objyObject, EStructuralFeature feature, int targetIndex, int sourceIndex)
  {
    throw new UnsupportedOperationException("Implement me!!");
  }
}
