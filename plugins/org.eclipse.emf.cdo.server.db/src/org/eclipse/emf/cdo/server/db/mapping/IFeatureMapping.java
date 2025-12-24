/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.db.mapping;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.ddl.IDBField;

import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * Mapping of {@link EStructuralFeature feature} values to and from the database.
 * <p>
 * There are only two kinds of feature mappings:
 * <ul>
 * <li> {@link ITypeMapping Type mappings} for single-valued features</li>
 * <li> {@link IListMapping List mappings} for many-valued features</li>
 * </ul>
 *
 * @author Eike Stepper
 * @since 4.14
 */
public interface IFeatureMapping
{
  /**
   * @return The feature which is associated with this feature mapping.
   */
  public EStructuralFeature getFeature();

  /**
   * @return The type mapping which is used for this feature mapping.
   */
  public ITypeMapping getTypeMapping();

  /**
   * @return The value field which is associated with this feature mapping.
   */
  public IDBField getField();

  /**
   * @return The value type which is associated with this feature mapping.
   */
  public DBType getDBType();
}
