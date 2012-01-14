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
package org.eclipse.emf.cdo.server.internal.objectivity.schema;

import org.eclipse.emf.cdo.common.revision.CDORevision;

import com.objy.as.app.Class_Object;
import com.objy.db.app.Session;
import com.objy.db.app.ooId;
import com.objy.db.app.ooObj;
import com.objy.db.util.ooTreeListX;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon McDuff
 * @author ibrahim 100920 - IS: modified to use ooTreeListX instead of ObjyArrayList<ooId>. minimize the changes for the
 *         3.0.x build. 100927 - IS: changed to wrap ooTreeListX to avoid using extra class/Class_Object creation
 */
public class ObjyArrayListId
{

  static public String className = "ooTreeListX";

  private ooTreeListX list = null;

  // private int size = 0;

  public static void buildSchema()
  {
    return;
    // d_Module top_mod = ObjySchema.getTopModule();
    // if (top_mod.resolve_class(ObjyArrayListId.className) == null
    // && top_mod.resolve_proposed_class(ObjyArrayListId.className) == null)
    // {
    //
    // if (TRACER_DEBUG.isEnabled())
    // {
    // TRACER_DEBUG.trace("Schema not found for ooArrayListId. Adding ooArrayListId");
    // }
    //
    // boolean inProcess = top_mod.proposed_classes().hasNext();
    //
    // // Proposed_Class A = new Proposed_Class(ooArrayListId.ClassName);
    // Proposed_Class A = top_mod.propose_new_class(ObjyArrayListId.className);
    //
    // A.add_base_class(com.objy.as.app.d_Module.LAST, com.objy.as.app.d_Access_Kind.d_PUBLIC, "ooObj" /* "ooObj" */);
    //
    // A.add_basic_attribute(com.objy.as.app.d_Module.LAST, d_Access_Kind.d_PUBLIC, // Access kind
    // ObjyArrayList.sizeName, // Attribute name
    // 1, // # elements in fixed-size array
    // ooBaseType.ooINT32 // Type of numeric data
    // ); // Default value
    //
    // A.add_ref_attribute(com.objy.as.app.d_Module.LAST, d_Access_Kind.d_PUBLIC, // Access kind
    // ObjyArrayList.arrayName, // Attribute name
    // 1, // # elements in fixed-size array
    // "ooTreeListX", false);
    //
    // // top_mod.propose_new_class(A);
    // if (!inProcess)
    // {
    // top_mod.activate_proposals(true, true);
    // }
    //
    // if (TRACER_DEBUG.isEnabled())
    // {
    // TRACER_DEBUG.trace("SCHEMA changed : ooArrayListId added");
    // }
    // }

  }

  public static void initObject(Class_Object classObject)
  {
    return;
    // // set the size to 0;
    // classObject.nset_numeric(ObjyArrayList.sizeName, new Numeric_Value(0));
    // // create the ooTreeListX.
    // ooTreeListX list = new ooTreeListX();
    // ooObj anObj = ooObj.create_ooObj(classObject.objectID());
    // anObj.cluster(list);
    // // System.out.println("initObject: " + anObj.getOid().getStoreString() + " treeListX: "
    // // + list.getOid().getStoreString());
    // classObject.nset_ooId(ObjyArrayList.arrayName, list.getOid());
  }

  public ObjyArrayListId(Class_Object classObject)
  {
    // get the ooTreeList object.
    ooId listId = classObject.objectID();
    list = (ooTreeListX)Session.getCurrent().getFD().objectFrom(listId);
  }

  protected void setValue(long index, ooObj newValue)
  {
    list.set((int)index, newValue);
  }

  protected ooObj getValue(long index)
  {
    return (ooObj)list.get((int)index);
  }

  protected ooId getOidValue(long index)
  {
    // System.out.println(" - ooArrayListId.getValue() at index: " + index +
    // "  for classObject: " + classObject.objectID().getStoreString() );
    return getValue(index).getOid();
  }

  public ooId get(long index)
  {
    if (index >= size())
    {
      throw new ArrayIndexOutOfBoundsException();
    }

    return getOidValue(index);
  }

  public void set(long index, ooId newValue)
  {
    setValue(index, ooObj.create_ooObj(newValue));
    setSize();
  }

  public ooId[] getAll(int index, int chunkSize)
  {
    long size = size();

    if (chunkSize != CDORevision.UNCHUNKED)
    {
      size = Math.min(size, chunkSize);
    }
    if (size == 0)
    {
      return null;
    }

    ooId[] ooIds = new ooId[(int)size];
    for (int i = 0; i < size; i++)
    {
      ooIds[i] = getOidValue(i + index);
    }
    return ooIds;
  }

  public void add(ooId newValue)
  {
    list.add(ooObj.create_ooObj(newValue));
    // size++;
  }

  public void add(int index, ooId newValue)
  {
    try
    {
      list.add(index, ooObj.create_ooObj(newValue));
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    // size++;
  }

  public void addAll(int index, Object[] newValue)
  {
    List<Object> objList = new ArrayList<Object>();
    for (int i = 0; i < newValue.length; i++)
    {
      objList.add(ooObj.create_ooObj((ooId)newValue[i]));
    }

    list.addAll(index, objList);
    // setSize();
  }

  public void move(long newPosition, long oldPosition)
  {
    if (oldPosition == newPosition)
    {
      return;
    }

    // get the object at oldPosition.
    ooObj value = getValue(oldPosition);

    // remove the oldPosition.
    remove((int)oldPosition);
    list.add((int)newPosition, value);
  }

  public void remove(int index)
  {
    list.remove(index);
    // setSize();
  }

  public void clear()
  {
    list.clear();
    // size = 0;
    // setSize();
  }

  protected void setSize()
  {
    // System.out.println(">>> classObject: " + classObject.objectID().getStoreString() + " <<<");
    // System.out.println("ooArrayList.saveSize() - value to store in objy is: " + cacheSize);
    // classObject.nset_numeric(ObjyArrayList.sizeName, new Numeric_Value(size));
  }

  public long size()
  {
    // size = (int)classObject.nget_numeric(ObjyArrayList.sizeName).longValue();
    int size = list.size();
    return size;
  }

  public ooObj copy(ooObj nearObj)
  {

    ooTreeListX newList = new ooTreeListX(2/* (int)size() */, false);
    // ObjyObjectManager.newInternalObjCount++;

    nearObj.cluster(newList);
    if (size() > 0)
    {
      newList.addAll(list);
    }

    // Class_Object newClassObject = new Class_Object(newObj);
    // newClassObject.nset_ooId(ObjyArrayList.arrayName, newList.getOid());

    return newList;
  }

}
