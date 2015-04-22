/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
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

/**
 * Mapper for composite types (featureMap, manyReference, Multiple...)
 *
 * @author ibrahim
 */
public interface IManyTypeMapper extends ITypeMapper
{

  final String embeddedAttributeName = "value";

  final String embeddedAttributeNull = "isNull";

  final int valueAttributePosition = 0; // caution, we assume position index here!!!

  final int nullAttributePosition = 1; // caution, we assume position index here!!!

  // set a single object/value at index.
  public void setValue(ObjyObject objyObject, EStructuralFeature feature, int index, Object newValue);

  // get a single object/value at index.
  public Object getValue(ObjyObject objyObject, EStructuralFeature feature, int index);

  // remove a single value at index, it will set the value to default, and mark it
  // as "null", i.e. unset.
  public Object remove(ObjyObject objyObject, EStructuralFeature feature, int index);

  // add value at index (extend the collection size).
  public void add(ObjyObject objyObject, EStructuralFeature feature, int index, Object value);

  // add all objects starting from an index. (extend the collection size).
  public void addAll(ObjyObject objyObject, EStructuralFeature feature, int index, Object[] value);

  // clear all collection.
  public void clear(ObjyObject objyObject, EStructuralFeature feature);

  // this is similar to addAll, but it replaces the existing ones.
  public void setAll(ObjyObject objyObject, EStructuralFeature feature, int index, Object[] newValue);

  // get all objects/values starting from an index.
  public Object[] getAll(ObjyObject objyObject, EStructuralFeature feature, int index, int chunkSize);

  // return the size of the collection.
  public int size(ObjyObject objyObject, EStructuralFeature feature);

  // move element in the feature from sourceIndex to targetIndex
  public void move(ObjyObject objyObject, EStructuralFeature feature, int targetIndex, int sourceIndex);
}
