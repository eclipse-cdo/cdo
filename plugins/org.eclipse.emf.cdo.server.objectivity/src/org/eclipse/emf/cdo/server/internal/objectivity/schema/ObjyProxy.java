/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Ibrahim Sallam - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.objectivity.schema;

import org.eclipse.emf.cdo.server.internal.objectivity.bundle.OM;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjySchema;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import com.objy.as.app.Class_Object;
import com.objy.as.app.Proposed_Class;
import com.objy.as.app.String_Value;
import com.objy.as.app.d_Access_Kind;
import com.objy.as.app.d_Module;
import com.objy.db.app.ooId;

public class ObjyProxy
{

  private static final ContextTracer TRACER_DEBUG = new ContextTracer(OM.DEBUG, ObjyProxy.class);

  static public String className = "ObjyProxy";

  static public String uriAttributeName = "uri";

  protected Class_Object classObject;

  public static void buildSchema()
  {
    d_Module top_mod = ObjySchema.getTopModule();
    if (top_mod.resolve_class(ObjyProxy.className) == null
        && top_mod.resolve_proposed_class(ObjyProxy.className) == null)
    {

      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace("Schema not found for ooArrayListId. Adding ooArrayListId");
      }

      boolean inProcess = top_mod.proposed_classes().hasNext();

      Proposed_Class propClass = top_mod.propose_new_class(ObjyProxy.className);

      propClass.add_base_class(com.objy.as.app.d_Module.LAST, com.objy.as.app.d_Access_Kind.d_PUBLIC, "ooObj");

      // propClass.add_varray_attribute(com.objy.as.app.d_Module.LAST, d_Access_Kind.d_PUBLIC, // Access kind
      // ooProxy.uriAttributeName, // Attribute name
      // 1, // # elements in fixed-size array
      // ooBaseType.ooCHAR// Type of string data
      // );
      //
      propClass.add_embedded_class_attribute(com.objy.as.app.d_Module.LAST, d_Access_Kind.d_PROTECTED, // access kind
          ObjyProxy.uriAttributeName, // Attribute name
          1, // # elements in fixed-size array
          "ooUtf8String" // name of embedded class
      );

      // top_mod.propose_new_class(propClass);
      if (!inProcess)
      {
        top_mod.activate_proposals(true, true);
      }

      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace("SCHEMA changed : ooProxy added");
      }
    }

  }

  // factory.
  public static ObjyProxy createObject(ooId nearObject)
  {
    Class_Object newClassObject = Class_Object.new_persistent_object(ObjySchema.getObjyClass(ObjyProxy.className)
        .getASClass(), nearObject, false);
    ObjyProxy proxyObject = new ObjyProxy(newClassObject);
    return proxyObject;
  }

  public ObjyProxy(Class_Object classObject)
  {
    this.classObject = classObject;
  }

  public void setUri(String uri)
  {
    String_Value stringValue = classObject.nget_string(ObjyProxy.uriAttributeName);
    stringValue.set(uri);
  }

  public String getUri()
  {
    String_Value stringValue = classObject.nget_string(ObjyProxy.uriAttributeName);
    return stringValue.toString();
  }

  public ooId ooId()
  {
    return classObject.objectID();
  }

}
