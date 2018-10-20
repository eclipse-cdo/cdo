/*
 * Copyright (c) 2004-2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.db.mapping;

import org.eclipse.net4j.db.ddl.IDBField;

import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Eike Stepper
 * @since 4.7
 */
public abstract class AbstractFeatureMapping implements IFeatureMapping2
{
  private IMappingStrategy mappingStrategy;

  private EStructuralFeature feature;

  private IDBField unsettableField;

  public AbstractFeatureMapping()
  {
  }

  public final IMappingStrategy getMappingStrategy()
  {
    return mappingStrategy;
  }

  public final void setMappingStrategy(IMappingStrategy mappingStrategy)
  {
    this.mappingStrategy = mappingStrategy;
  }

  public final EStructuralFeature getFeature()
  {
    return feature;
  }

  public final void setFeature(EStructuralFeature feature)
  {
    this.feature = feature;
  }

  public final IDBField getUnsettableField()
  {
    return unsettableField;
  }

  public final void setUnsettableField(IDBField unsettableField)
  {
    this.unsettableField = unsettableField;
  }

  public static IDBField getField(IClassMapping2 classMapping, IFeatureMapping featureMapping)
  {
    if (featureMapping instanceof IFeatureMapping2)
    {
      return ((IFeatureMapping2)featureMapping).getField();
    }

    if (featureMapping instanceof ITypeMapping)
    {
      return ((ITypeMapping)featureMapping).getField();
    }

    EStructuralFeature feature = featureMapping.getFeature();
    return classMapping.getListSizeFields().get(feature);
  }

  public static IDBField getUnsettableField(IClassMapping2 classMapping, IFeatureMapping featureMapping)
  {
    if (featureMapping instanceof IFeatureMapping2)
    {
      return ((IFeatureMapping2)featureMapping).getUnsettableField();
    }

    EStructuralFeature feature = featureMapping.getFeature();
    return classMapping.getUnsettableFields().get(feature);
  }
}
