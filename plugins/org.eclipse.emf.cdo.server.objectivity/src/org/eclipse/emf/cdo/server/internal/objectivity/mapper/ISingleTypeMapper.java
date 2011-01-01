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

import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyObject;

import org.eclipse.emf.ecore.EStructuralFeature;

public interface ISingleTypeMapper extends ITypeMapper
{

  // Instance
  public void setValue(ObjyObject objyObject, EStructuralFeature feature, Object newValue);

  public Object getValue(ObjyObject objyObject, EStructuralFeature feature);

  // remove the entry.
  // 100204:IS - Hmmm. I'm not sure what the usage...
  public Object remove(ObjyObject objyObject, EStructuralFeature feature);

}
