/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
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

public class ooProxy
{

  private static final ContextTracer TRACER_DEBUG = new ContextTracer(OM.DEBUG, ooProxy.class);

  static public String className = "ooProxy";

  static public String uriAttributeName = "uri";

  protected Class_Object classObject;

  public static void buildSchema()
  {
    d_Module top_mod = ObjySchema.getTopModule();
    if (top_mod.resolve_class(ooProxy.className) == null && top_mod.resolve_proposed_class(ooProxy.className) == null)
    {

      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace("Schema not found for ooArrayListId. Adding ooArrayListId");
      }

      boolean inProcess = top_mod.proposed_classes().hasNext();

      Proposed_Class propClass = top_mod.propose_new_class(ooProxy.className);

      propClass.add_base_class(com.objy.as.app.d_Module.LAST, com.objy.as.app.d_Access_Kind.d_PUBLIC, "ooObj");

      // propClass.add_varray_attribute(com.objy.as.app.d_Module.LAST, d_Access_Kind.d_PUBLIC, // Access kind
      // ooProxy.uriAttributeName, // Attribute name
      // 1, // # elements in fixed-size array
      // ooBaseType.ooCHAR// Type of string data
      // );
      //
      propClass.add_embedded_class_attribute(com.objy.as.app.d_Module.LAST, d_Access_Kind.d_PROTECTED, // access kind
          ooProxy.uriAttributeName, // Attribute name
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
  public static ooProxy createObject(ooId nearObject)
  {
    Class_Object newClassObject = Class_Object.new_persistent_object(ObjySchema.getObjyClass(ooProxy.className)
        .getASClass(), nearObject, false);
    ooProxy proxyObject = new ooProxy(newClassObject);
    return proxyObject;
  }

  public ooProxy(Class_Object classObject)
  {
    this.classObject = classObject;
  }

  public void setUri(String uri)
  {
    String_Value stringValue = classObject.nget_string(ooProxy.uriAttributeName);
    stringValue.set(uri);
  }

  public String getUri()
  {
    String_Value stringValue = classObject.nget_string(ooProxy.uriAttributeName);
    return stringValue.toString();
  }

  public ooId ooId()
  {
    return classObject.objectID();
  }

}
