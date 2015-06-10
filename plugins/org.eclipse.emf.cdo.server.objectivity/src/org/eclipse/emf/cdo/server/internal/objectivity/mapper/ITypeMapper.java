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

import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyObject;

import org.eclipse.emf.ecore.EStructuralFeature;

import com.objy.as.app.Class_Object;
import com.objy.as.app.Numeric_Value;
import com.objy.as.app.Proposed_Class;
import com.objy.as.app.d_Attribute;

/**
 * Maps between Objy types (classes and objects), and EMF types.
 *
 * @author ibrahim
 */
public interface ITypeMapper
{

  final Numeric_Value numericTrue = new Numeric_Value(true);

  final Numeric_Value numericFalse = new Numeric_Value(false);

  // Model
  public boolean validate(d_Attribute ooAttribute, EStructuralFeature feature);

  public boolean createSchema(Proposed_Class proposedClasses, EStructuralFeature feature);

  public void modifySchema(Proposed_Class proposedooClass, EStructuralFeature feature);

  // Instance
  public void initialize(Class_Object class_Object, EStructuralFeature feature);

  // delete the entry.
  // 100204:IS - Hmmm. I'm not sure what the usage...
  public void delete(ObjyObject objyObject, EStructuralFeature feature);

}
