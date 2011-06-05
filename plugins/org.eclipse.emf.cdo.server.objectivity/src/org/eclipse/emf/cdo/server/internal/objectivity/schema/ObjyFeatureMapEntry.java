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
package org.eclipse.emf.cdo.server.internal.objectivity.schema;

import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjySchema;

import com.objy.as.app.Class_Object;
import com.objy.as.app.Numeric_Value;
import com.objy.as.app.Proposed_Class;
import com.objy.as.app.d_Access_Kind;
import com.objy.as.app.d_Module;
import com.objy.as.app.ooBaseType;
import com.objy.db.app.ooId;

public class ObjyFeatureMapEntry
{
  protected int tagId;

  protected ooId object;

  protected Class_Object classObject;

  public static final String MapEntryClassName = "ObjyFeatureMapEntry";

  public static final String EntryName = "tagId";

  public static final String EntryObject = "object";

  public static void buildSchema()
  {
    d_Module top_mod = ObjySchema.getTopModule();
    if (top_mod.resolve_class(MapEntryClassName) == null && top_mod.resolve_proposed_class(MapEntryClassName) == null)
    {
      // Proposed_Class B = new Proposed_Class(MapEntryClassName);
      Proposed_Class B = top_mod.propose_new_class(MapEntryClassName);
      B.add_base_class(com.objy.as.app.d_Module.LAST, com.objy.as.app.d_Access_Kind.d_PUBLIC, "ooObj");

      B.add_basic_attribute(com.objy.as.app.d_Module.LAST, // Access kind
          d_Access_Kind.d_PUBLIC, // Access kind
          ObjyFeatureMapEntry.EntryName, // Attribute name
          1, // # elements in fixed-size array
          ooBaseType.ooINT32); // type
      B.add_ref_attribute(com.objy.as.app.d_Module.LAST, d_Access_Kind.d_PUBLIC, // Access kind
          ObjyFeatureMapEntry.EntryObject, // Attribute name
          1, // # elements in fixed-size array
          "ooObj", // Type of numeric data
          false); // Short reference

      // top_mod.propose_new_class(B);
    }
  }

  /****
   * Factory.
   * 
   * @param tagId
   * @param oid
   */
  public ObjyFeatureMapEntry(int tagId, ooId oid, ooId near)
  {
    this.tagId = tagId;
    object = oid;

    classObject = Class_Object.new_persistent_object(ObjySchema.getObjyClass(MapEntryClassName).getASClass(), near,
        false);

    classObject.nset_ooId(EntryObject, object);
    classObject.nset_numeric(EntryName, new Numeric_Value(tagId));
  }

  public ObjyFeatureMapEntry(Class_Object classObject)
  {
    this.classObject = classObject;
    tagId = classObject.nget_numeric(EntryName).intValue();
    object = classObject.nget_ooId(EntryObject);
  }

  protected void fetchObject()
  {
    tagId = classObject.nget_numeric(EntryName).intValue();
    object = classObject.nget_ooId(EntryObject);
  }

  public int getTagId()
  {
    fetchObject();
    return tagId;
  }

  public void setTagId(int tagId)
  {
    classObject.nset_numeric(EntryName, new Numeric_Value(tagId));
    this.tagId = tagId;
  }

  public ooId getObject()
  {
    fetchObject();
    return object;
  }

  public void setObject(ooId object)
  {
    classObject.nset_ooId(EntryObject, object);
    this.object = object;
  }

  public ooId getOid()
  {
    return classObject.objectID();
  }
}
