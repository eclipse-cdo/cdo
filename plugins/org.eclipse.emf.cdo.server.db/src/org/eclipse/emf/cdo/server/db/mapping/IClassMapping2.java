/*
 * Copyright (c) 2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.db.mapping;

import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;

import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;

import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.Map;

/**
 * An extension interface for {@link IClassMapping class mappings} that support <i>units</i>.
 *
 * @author Eike Stepper
 * @since 4.7
 */
public interface IClassMapping2 extends IClassMapping
{
  public IDBTable getTable();

  public void setTable(IDBTable table);

  public void initTable(IDBStoreAccessor accessor);

  public IDBTable createTable(IDBSchema schema, String tableName);

  public Map<EStructuralFeature, IDBField> getUnsettableFields();

  public Map<EStructuralFeature, IDBField> getListSizeFields();

  public IFeatureMapping[] createFeatureMappings(IDBTable table, boolean updateClassMapping, EStructuralFeature... features);

  public IFeatureMapping[] getFeatureMappings();

  public IFeatureMapping getFeatureMapping(EStructuralFeature feature);
}
