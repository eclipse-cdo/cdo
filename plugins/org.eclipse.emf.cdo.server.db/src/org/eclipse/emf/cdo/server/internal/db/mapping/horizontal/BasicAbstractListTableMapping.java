/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stefan Winkler - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;
import org.eclipse.emf.cdo.server.internal.db.DBStoreAccessor;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Stefan Winkler
 */
public abstract class BasicAbstractListTableMapping implements IListMapping
{
  private IMappingStrategy mappingStrategy;

  private EClass containingClass;

  private EStructuralFeature feature;

  public BasicAbstractListTableMapping(IMappingStrategy mappingStrategy, EClass containingClass,
      EStructuralFeature feature)
  {
    this.mappingStrategy = mappingStrategy;
    this.containingClass = containingClass;
    this.feature = feature;
  }

  public final IMappingStrategy getMappingStrategy()
  {
    return mappingStrategy;
  }

  public final EClass getContainingClass()
  {
    return containingClass;
  }

  public final EStructuralFeature getFeature()
  {
    return feature;
  }

  public void addSimpleChunkWhere(DBStoreAccessor accessor, CDOID cdoid, StringBuilder builder, int index)
  {
    builder.append(CDODBSchema.LIST_IDX);
    builder.append('=');
    builder.append(index);
  }

  public void addRangedChunkWhere(DBStoreAccessor accessor, CDOID cdoid, StringBuilder builder, int fromIndex,
      int toIndex)
  {
    builder.append(CDODBSchema.LIST_IDX);
    builder.append(" BETWEEN "); //$NON-NLS-1$
    builder.append(fromIndex);
    builder.append(" AND "); //$NON-NLS-1$
    builder.append(toIndex - 1);
  }

}
