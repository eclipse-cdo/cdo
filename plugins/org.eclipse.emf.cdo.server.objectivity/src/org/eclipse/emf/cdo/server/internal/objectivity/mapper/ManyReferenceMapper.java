/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjySchema;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ooArrayListId;
import org.eclipse.emf.cdo.server.internal.objectivity.utils.TypeConvert;

import org.eclipse.emf.ecore.EStructuralFeature;

import com.objy.as.app.Class_Object;
import com.objy.as.app.Class_Position;
import com.objy.as.app.Proposed_Class;
import com.objy.as.app.d_Access_Kind;
import com.objy.as.app.d_Attribute;
import com.objy.as.app.d_Class;
import com.objy.db.ObjyRuntimeException;
import com.objy.db.app.ooId;
import com.objy.db.app.ooObj;

/**
 * @author Simon McDuff
 */
public class ManyReferenceMapper extends BasicTypeMapper implements IManyTypeMapper
{
  static d_Class dClassObject = null;

  static ManyReferenceMapper INSTANCE = new ManyReferenceMapper();

  private static d_Class getArrayListClass()
  {
    if (dClassObject == null)
    {
      dClassObject = ObjySchema.getTopModule().resolve_class(ooArrayListId.className);
    }
    return dClassObject;
  }

  public boolean createSchema(Proposed_Class propClass, EStructuralFeature feature)
  {
    try
    {
      // ooArrayListId.buildSchema();

      propClass.add_ref_attribute(com.objy.as.app.d_Module.LAST, // Access kind
          d_Access_Kind.d_PUBLIC, // Access kind
          feature.getName(), // Attribute name
          1, // # elements in fixed-size array
          ooArrayListId.className, false); // Default value // Default value

      return true;
    }
    catch (ObjyRuntimeException ex)
    {
      ex.printStackTrace();
      return false;
    }
  }

  public Object getValue(ObjyObject internal, EStructuralFeature feature, int index)
  {
    ooArrayListId list = getList(internal, feature);
    if (list != null)
    {
      return list.get(index);
    }
    else
    {
      try
      {
        throw new Exception("Trying to getValue of object while the list is null.");
      }
      catch (Exception e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      return null;
    }
  }

  public void setValue(ObjyObject internal, EStructuralFeature feature, int index, Object newValue)
  {
    ooArrayListId list = getList(internal, feature);

    if (list != null)
    {
      list.set(index, TypeConvert.toOoId(newValue));
    }
    else
    {
      try
      {
        throw new Exception("Trying to setValue for object while the list is null.");
      }
      catch (Exception e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

  }

  public ooArrayListId getList(ObjyObject objyObject, EStructuralFeature feature)
  {
    // System.out.println("getList() for : " + objyObject.ooId().getStoreString() + " feature: " + feature.getName());
    Class_Position position = getAttributePosition(objyObject, feature);
    ooArrayListId list = (ooArrayListId)objyObject.getFeatureList(position);
    if (list == null)
    {
      try
      {
        ooId oid = objyObject.get_ooId(position);
        if (!oid.isNull())
        {
          list = new ooArrayListId(Class_Object.class_object_from_oid(oid));
          objyObject.setFeatureList(position, list);
        }
      }
      catch (ObjyRuntimeException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return list;
  }

  public int size(ObjyObject objyObject, EStructuralFeature feature)
  {
    ooArrayListId list = getList(objyObject, feature);

    return (int)(list == null ? 0 : list.size());
  }

  public void add(ObjyObject objyObject, EStructuralFeature feature, int index, Object value)
  {
    ooId obj = TypeConvert.toOoId(value);
    ooArrayListId list = getList(objyObject, feature);
    if (list != null)
    {
      list.add(index, obj);
    }
    else
    {
      try
      {
        throw new Exception("Trying to add objects while the list is null.");
      }
      catch (Exception e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  public void addAll(ObjyObject objyObject, EStructuralFeature feature, int index, Object[] value)
  {
    ooArrayListId list = getList(objyObject, feature);
    if (list != null)
    {
      list.addAll(index, value);
    }
    else
    {
      try
      {
        throw new Exception("Trying to addAll objects while the list is null.");
      }
      catch (Exception e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

  }

  public boolean validate(d_Attribute ooAttribute, EStructuralFeature feature)
  {
    // TODO Auto-generated method stub
    System.out.println("OBJYIMPL: ManyReferenceMapper.validate() - not implemented.");
    return true;
  }

  public void initialize(Class_Object classObject, EStructuralFeature feature)
  {
    Class_Position position = classObject.position_in_class(getAttributeName(feature));
    Class_Object newClassObject = Class_Object
        .new_persistent_object(getArrayListClass(), classObject.objectID(), false);
    classObject.set_ooId(position, newClassObject.objectID());
    ooArrayListId.initObject(newClassObject);
  }

  public void delete(ObjyObject objyObject, EStructuralFeature feature)
  {
    // System.out.println("delete() for : " + objyObject.ooId().getStoreString() + " feature: " + feature.getName());
    Class_Position position = getAttributePosition(objyObject, feature);
    ooId tobeDeleted = objyObject.get_ooId(position);
    ooObj objectToDelete = ooObj.create_ooObj(tobeDeleted);
    objectToDelete.delete();
    // set the reference to null.
    objyObject.set_ooId(position, null);
    objyObject.setFeatureList(position, null);
  }

  public void clear(ObjyObject objyObject, EStructuralFeature feature)
  {
    ooArrayListId list = getList(objyObject, feature);
    if (list != null)
    {
      list.clear();
    }
    else
    {
      try
      {
        throw new Exception("Trying to clear objects while the list is null.");
      }
      catch (Exception e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

  }

  public Object remove(ObjyObject objyObject, EStructuralFeature feature, int index)
  {
    // System.out.println("remove() for : " + objyObject.ooId().getStoreString() + " feature: " + feature.getName() +
    // " index: "+index);
    Object oldValue = getValue(objyObject, feature, index);
    ooArrayListId list = getList(objyObject, feature);

    if (list != null)
    {
      list.remove(index);
    }
    else
    {
      try
      {
        throw new Exception("Trying to remove object while the list is null.");
      }
      catch (Exception e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    return oldValue;
  }

  public Object[] getAll(ObjyObject objyObject, EStructuralFeature feature, int index, int chunkSize)
  {
    int size = size(objyObject, feature);

    if (chunkSize != -1)
    {
      size = Math.min(size, chunkSize);
    }

    // CDOList cdoList = CDOListFactory.DEFAULT.createList(size, size, 0);
    ooId[] ooIds = null;
    ooArrayListId list = getList(objyObject, feature);

    if (list != null)
    {
      ooIds = list.getAll(index, chunkSize);
    }
    else
    {
      try
      {
        throw new Exception("Trying to getAll objects while the list is null.");
      }
      catch (Exception e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    return ooIds;
  }

  public void modifySchema(Proposed_Class proposedooClass, EStructuralFeature feature)
  {
    throw new UnsupportedOperationException("Implement me!!");
  }

  public void setAll(ObjyObject objyObject, EStructuralFeature feature, int index, Object[] newValues)
  {
    addAll(objyObject, feature, 0, newValues);
  }

}
