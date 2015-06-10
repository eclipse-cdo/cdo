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
package org.eclipse.emf.cdo.server.internal.objectivity.mapper;

//import org.eclipse.emf.cdo.server.internal.objectivity.bundle.OM;
//import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * mapping between Objy AS class/type and EMF types.
 *
 * @author ibrahim
 */
public abstract class BasicTypeMapper
{

  // private static final ContextTracer TRACER_DEBUG = new ContextTracer(
  // OM.DEBUG, BasicTypeMapper.class);

  protected String getAttributeName(EStructuralFeature feature)
  {
    return feature.getName();
  }

  protected String getNullAttributeName(EStructuralFeature feature)
  {
    return feature.getName() + "_isNull";
  }

  // protected Class_Position getAttributePosition(ObjyObject objyObject, EStructuralFeature feature)
  // {
  // return objyObject.objyClass().resolve_position(getAttributeName(feature));
  // }

  // protected Class_Position getNullAttributePosition(ObjyObject objyObject, EStructuralFeature feature)
  // {
  // return objyObject.objyClass().resolve_position(getNullAttributeName(feature));
  // }

  // ---------------------------------
  // Object
  // ---------------------------------
  // public void initialize(Class_Object class_Object,
  // EStructuralFeature feature, Class_Position attr)
  // {
  //
  // }
  //
  // public void delete(ObjyObject objyObject,
  // EStructuralFeature feature, Class_Position position)
  // {
  // }
}
