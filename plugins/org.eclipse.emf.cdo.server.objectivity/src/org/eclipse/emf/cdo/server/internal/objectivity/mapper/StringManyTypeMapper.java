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

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyObject;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjySchema;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyArrayListString;

import org.eclipse.emf.ecore.EStructuralFeature;

import com.objy.as.app.Class_Object;
import com.objy.as.app.Class_Position;
import com.objy.as.app.Proposed_Class;
import com.objy.as.app.d_Access_Kind;
import com.objy.as.app.d_Attribute;
import com.objy.db.ObjyRuntimeException;

/**
 * @author Ibrahim Sallam
 */
public class StringManyTypeMapper extends BasicTypeMapper implements IManyTypeMapper
{
  public static StringManyTypeMapper INSTANCE = new StringManyTypeMapper();

  public boolean createSchema(Proposed_Class proposedClass, EStructuralFeature feature)
  {
    try
    {

      ObjyArrayListString.buildSchema();

      proposedClass.add_ref_attribute(com.objy.as.app.d_Module.LAST, // Access kind
          d_Access_Kind.d_PUBLIC, // Access kind
          getAttributeName(feature), // Attribute name
          1, // # elements in fixed-size array
          ObjyArrayListString.ClassName, false); // Default value // Default value

    }
    catch (ObjyRuntimeException ex)
    {
      ex.printStackTrace();
    }
    return false;
  }

  public boolean validate(d_Attribute ooAttribute, EStructuralFeature feature)
  {
    // TODO Need to implement this.
    // throw new UnsupportedOperationException("Implement me!!");
    System.out.println(">>>OBJYIMPL: StringManyTypeMapper.validate() - not implemented.");
    return true;
  }

  protected String stringFromObject(EStructuralFeature feature, Object objectValue)
  {
    if (objectValue instanceof String)
    {
      return (String)objectValue;
    }

    return null;
  }

  protected Object objectFromString(EStructuralFeature feature, String stringValue)
  {
    return stringValue;
  }

  protected ObjyArrayListString getList(ObjyObject objyObject, EStructuralFeature feature)
  {
    // Class_Position position = getAttributePosition(objyObject, feature);
    String attributeName = getAttributeName(feature);

    ObjyArrayListString list = (ObjyArrayListString)objyObject.getFeatureList(attributeName/* position */);
    if (list == null)
    {
      list = new ObjyArrayListString(objyObject.get_class_obj(attributeName/* position */));
      objyObject.setFeatureList(attributeName/* position */, list);
    }
    return list;
  }

  public void add(ObjyObject objyObject, EStructuralFeature feature, int index, Object value)
  {
    assert value instanceof String;
    getList(objyObject, feature).add(stringFromObject(feature, value));
  }

  public void addAll(ObjyObject objyObject, EStructuralFeature feature, int index, Object[] values)
  {
    // CDOList list = (CDOList) value;
    String[] strings = new String[values.length];
    for (int i = 0; i < values.length; i++)
    {
      // strings[i] = stringFromObject(feature, values[i]);
      strings[i] = (String)values[i];
    }
    getList(objyObject, feature).addAll(index, strings);
  }

  public void clear(ObjyObject objyObject, EStructuralFeature feature)
  {
    getList(objyObject, feature).clear();
  }

  public Object[] getAll(ObjyObject objyObject, EStructuralFeature feature, int index, int chunkSize)
  {
    int size = size(objyObject, feature);

    if (chunkSize != CDORevision.UNCHUNKED)
    {
      size = Math.min(size, chunkSize);
    }

    String[] strings = getList(objyObject, feature).getAll(index, size);

    Object[] objects = new Object[strings.length];

    for (int i = 0; i < strings.length; i++)
    {
      objects[i] = objectFromString(feature, strings[i]);
    }

    return strings;
  }

  public Object getValue(ObjyObject objyObject, EStructuralFeature feature, int index)
  {
    return objectFromString(feature, getList(objyObject, feature).get(index));
  }

  public Object remove(ObjyObject objyObject, EStructuralFeature feature, int index)
  {
    Object oldValue = objectFromString(feature, getList(objyObject, feature).get(index));
    getList(objyObject, feature).remove(index);
    return oldValue;
  }

  public void setAll(ObjyObject objyObject, EStructuralFeature feature, int index, Object[] newValues)
  {
    addAll(objyObject, feature, 0, newValues);
  }

  public void setValue(ObjyObject objyObject, EStructuralFeature feature, int index, Object newValue)
  {
    assert newValue instanceof String;

    getList(objyObject, feature).set(index, stringFromObject(feature, newValue));
  }

  public int size(ObjyObject objyObject, EStructuralFeature feature)
  {
    return (int)getList(objyObject, feature).size();
  }

  public void delete(ObjyObject objyObject, EStructuralFeature feature)
  {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Implement me!!");
  }

  public void initialize(Class_Object classObject, EStructuralFeature feature)
  {
    Class_Position position = classObject.type_of().position_in_class(getAttributeName(feature));
    Class_Object newClassObject = Class_Object.new_persistent_object(
        ObjySchema.getObjyClass(ObjyArrayListString.ClassName).getASClass(), classObject.objectID(), false);
    // ObjyObjectManager.newInternalObjCount++;
    classObject.set_ooId(position, newClassObject.objectID());
    ObjyArrayListString.initObject(newClassObject);
  }

  public void modifySchema(Proposed_Class proposedooClass, EStructuralFeature feature)
  {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Implement me!!");
  }

  public void move(ObjyObject objyObject, EStructuralFeature feature, int targetIndex, int sourceIndex)
  {
    throw new UnsupportedOperationException("Implement me!!");
  }
}
