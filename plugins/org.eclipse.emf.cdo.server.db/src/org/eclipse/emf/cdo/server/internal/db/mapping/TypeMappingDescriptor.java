/*
 * Copyright (c) 2010-2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Stefan Winkler - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.db.mapping;

import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;

import org.eclipse.net4j.db.DBType;

import org.eclipse.emf.ecore.EClassifier;

/**
 * @author Stefan Winkler
 */
public class TypeMappingDescriptor implements ITypeMapping.Descriptor
{
  private String id;

  private String factoryType;

  private EClassifier eClassifier;

  private DBType dbType;

  public TypeMappingDescriptor(String id, String factoryType, EClassifier eClassifier, DBType dbType)
  {
    this.id = id;
    this.factoryType = factoryType;
    this.eClassifier = eClassifier;
    this.dbType = dbType;
  }

  @Override
  public String getID()
  {
    return id;
  }

  @Override
  public String getFactoryType()
  {
    return factoryType;
  }

  @Override
  public EClassifier getEClassifier()
  {
    return eClassifier;
  }

  @Override
  public DBType getDBType()
  {
    return dbType;
  }

  @Override
  public String toString()
  {
    return "TypeMappingDescriptor [" + factoryType + "]";
  }
}
