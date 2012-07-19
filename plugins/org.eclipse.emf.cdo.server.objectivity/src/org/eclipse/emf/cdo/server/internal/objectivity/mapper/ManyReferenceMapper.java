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

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyObject;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjySchema;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyArrayListId;
import org.eclipse.emf.cdo.server.internal.objectivity.utils.TypeConvert;

import org.eclipse.emf.ecore.EStructuralFeature;

import com.objy.as.app.Class_Object;
import com.objy.as.app.Proposed_Class;
import com.objy.as.app.d_Access_Kind;
import com.objy.as.app.d_Attribute;
import com.objy.as.app.d_Class;
import com.objy.db.ObjyRuntimeException;
import com.objy.db.app.ooId;
import com.objy.db.app.ooObj;
import com.objy.db.util.ooTreeListX;

/**
 * @author Simon McDuff
 */
public class ManyReferenceMapper extends BasicTypeMapper implements IManyTypeMapper
{
  static d_Class dClassObject = null;

  static ManyReferenceMapper INSTANCE = new ManyReferenceMapper();

  @SuppressWarnings("unused")
  private static d_Class getArrayListClass()
  {
    if (dClassObject == null)
    {
      dClassObject = ObjySchema.getTopModule().resolve_class(ObjyArrayListId.className);
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
          ObjyArrayListId.className, false); // Default value // Default value

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
    ObjyArrayListId list = getList(internal, feature);
    if (list != null)
    {
      return list.get(index);
    }

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

  public void setValue(ObjyObject internal, EStructuralFeature feature, int index, Object newValue)
  {
    ObjyArrayListId list = getList(internal, feature);

    try
    {
      if (list != null)
      {
        list.set(index, TypeConvert.toOoId(newValue));
      }
      else
      {
        throw new Exception("Trying to setValue for object while the list is null.");
      }
    }
    catch (Exception e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  public ObjyArrayListId getList(ObjyObject objyObject, EStructuralFeature feature)
  {
    // System.out.println("getList() for : " + objyObject.ooId().getStoreString() + " feature: " + feature.getName());
    // Class_Position position = getAttributePosition(objyObject, feature);
    String attributeName = getAttributeName(feature);
    ObjyArrayListId list = (ObjyArrayListId)objyObject.getFeatureList(attributeName/* position */);
    if (list == null)
    {
      try
      {
        ooId oid = objyObject.get_ooId(attributeName/* position */);
        if (!oid.isNull())
        {
          list = new ObjyArrayListId(Class_Object.class_object_from_oid(oid));
          objyObject.setFeatureList(attributeName/* position */, list);
        }
        // System.out.println("... getList() -> gotOID: " + oid.getStoreString());
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
    ObjyArrayListId list = getList(objyObject, feature);

    return (int)(list == null ? 0 : list.size());
  }

  public void add(ObjyObject objyObject, EStructuralFeature feature, int index, Object value)
  {
    ooId obj = TypeConvert.toOoId(value);
    ObjyArrayListId list = getList(objyObject, feature);
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
    ObjyArrayListId list = getList(objyObject, feature);
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
    // Class_Position position = classObject.position_in_class(getAttributeName(feature));
    // Class_Object newClassObject = Class_Object
    // .new_persistent_object(getArrayListClass(), classObject.objectID(), false);
    ooTreeListX list = new ooTreeListX(2, false);
    // ObjyObjectManager.newInternalObjCount++;
    ooObj anObj = ooObj.create_ooObj(classObject.objectID());
    anObj.cluster(list);
    classObject.nset_ooId(getAttributeName(feature), list.getOid());
    // classObject.set_ooId(position, newClassObject.objectID());
    // ObjyArrayListId.initObject(newClassObject);
  }

  public void delete(ObjyObject objyObject, EStructuralFeature feature)
  {
    // System.out.println("delete() for : " + objyObject.ooId().getStoreString() + " feature: " + feature.getName());
    // Class_Position position = getAttributePosition(objyObject, feature);
    String attributeName = getAttributeName(feature);
    ooId tobeDeleted = objyObject.get_ooId(attributeName/* position */);
    ooObj objectToDelete = ooObj.create_ooObj(tobeDeleted);
    objectToDelete.delete();
    // set the reference to null.
    objyObject.set_ooId(getAttributeName(feature)/* position */, null);
    objyObject.setFeatureList(attributeName/* position */, null);
  }

  public void clear(ObjyObject objyObject, EStructuralFeature feature)
  {
    ObjyArrayListId list = getList(objyObject, feature);
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
    ObjyArrayListId list = getList(objyObject, feature);

    if (list != null)
    {
      list.remove(index);
    }
    else
    {
      try
      {
        throw new Exception("Trying to remove element while the list is null.");
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

    if (chunkSize != CDORevision.UNCHUNKED)
    {
      size = Math.min(size, chunkSize);
    }

    // CDOList cdoList = CDOListFactory.DEFAULT.createList(size, size, 0);
    ooId[] ooIds = null;
    ObjyArrayListId list = getList(objyObject, feature);

    if (list != null)
    {
      ooIds = list.getAll(index, size);
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

  public void move(ObjyObject objyObject, EStructuralFeature feature, int targetIndex, int sourceIndex)
  {
    ObjyArrayListId list = getList(objyObject, feature);

    if (list != null)
    {
      list.move(targetIndex, sourceIndex);
    }
    else
    {
      try
      {
        throw new Exception("Trying to move element while the list is null.");
      }
      catch (Exception e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

}
