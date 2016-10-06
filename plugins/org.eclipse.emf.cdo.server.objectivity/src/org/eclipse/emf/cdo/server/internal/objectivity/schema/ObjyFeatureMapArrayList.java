/*
 * Copyright (c) 2010-2012, 2015 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.cdo.server.internal.objectivity.bundle.OM;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjySchema;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import com.objy.as.app.Class_Object;
import com.objy.as.app.Proposed_Class;
import com.objy.as.app.d_Access_Kind;
import com.objy.as.app.d_Module;
import com.objy.as.app.ooBaseType;

/**
 * @author Simon McDuff
 */
public class ObjyFeatureMapArrayList extends ObjyArrayList<ObjyFeatureMapEntry>
{
  private static final ContextTracer TRACER_DEBUG = new ContextTracer(OM.DEBUG, ObjyFeatureMapArrayList.class);

  static public String ClassName = "ObjyFeatureMapArrayList";

  // public class FeatureMapEntry
  // {
  // private org.eclipse.emf.cdo.server.internal.objectivity.schema.FeatureMapEntry data = new
  // org.eclipse.emf.cdo.server.internal.objectivity.schema.FeatureMapEntry();
  //
  // public FeatureMapEntry(String featureName, ooId object)
  // {
  // super();
  // this.data.featureName = featureName;
  // this.data.object = object;
  // }
  //
  // public String getFeatureName()
  // {
  // return data.featureName;
  // }
  //
  // public ooId getObject()
  // {
  // return data.object;
  // }
  // };

  public static void buildSchema()
  {
    // Connection.current().registerClass(MapEntryClassName);
    d_Module top_mod = ObjySchema.getTopModule();
    if (top_mod.resolve_class(ObjyFeatureMapArrayList.ClassName) == null
        && top_mod.resolve_proposed_class(ObjyFeatureMapArrayList.ClassName) == null)
    {

      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace("Schema not found for ooArrayListId. Adding ooArrayListId");
      }

      boolean inProcess = top_mod.proposed_classes().hasNext();

      ObjyFeatureMapEntry.buildSchema();

      // Proposed_Class A = new Proposed_Class(ooFeatureMapArrayList.ClassName);
      Proposed_Class A = top_mod.propose_new_class(ObjyFeatureMapArrayList.ClassName);

      A.add_base_class(com.objy.as.app.d_Module.LAST, com.objy.as.app.d_Access_Kind.d_PUBLIC, "ooObj");

      A.add_basic_attribute(com.objy.as.app.d_Module.LAST, d_Access_Kind.d_PUBLIC, // Access kind
          ObjyArrayList.sizeName, // Attribute name
          1, // # elements in fixed-size array
          ooBaseType.ooINT32 // Type of numeric data
      ); // Default value
      A.add_varray_attribute(com.objy.as.app.d_Module.LAST, d_Access_Kind.d_PUBLIC, // Access kind
          ObjyArrayList.arrayName, // Attribute name
          1, false, ObjyFeatureMapEntry.MapEntryClassName);

      // top_mod.propose_new_class(A);
      if (!inProcess)
      {
        top_mod.activate_proposals(true, true);
      }

      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace("SCHEMA changed : ooArrayListId added");
      }
    }
  }

  public ObjyFeatureMapArrayList(Class_Object classObject)
  {
    super(classObject);
  }

  @Override
  protected void setValue(long index, ObjyFeatureMapEntry featureMapEntry)
  {

    getVArray().set_ooId(index, featureMapEntry.getOid());

    // String_Value value2 = classObject.nget_string("uri");
    // System.out.println(value2);
  }

  @Override
  protected ObjyFeatureMapEntry getValue(long index)
  {
    Class_Object classObject = getVArray().get_class_obj(index);

    return new ObjyFeatureMapEntry(classObject);

    /***
     * FeatureMapEntry featureMapEntry = (FeatureMapEntry) ooObj.create_ooObj(getVArray().get_ooId(index)); return
     * featureMapEntry;
     ***/
  }

}
