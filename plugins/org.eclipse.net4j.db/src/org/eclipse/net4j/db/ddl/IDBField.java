/*
 * Copyright (c) 2008, 2010-2013, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.db.ddl;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.util.collection.PositionProvider;

/**
 * A field (column) specification in a {@link IDBTable DB table}.
 *
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IDBField extends IDBSchemaElement, PositionProvider
{
  public static final int DEFAULT = -1;

  /**
   * @since 4.2
   */
  @Override
  public IDBTable getParent();

  public IDBTable getTable();

  public DBType getType();

  public void setType(DBType type);

  public int getPrecision();

  public void setPrecision(int precision);

  public int getScale();

  public void setScale(int scale);

  public boolean isNotNull();

  public void setNotNull(boolean notNull);

  /**
   * @since 4.9
   */
  public boolean isIndexed();

  /**
   * @since 4.9
   */
  public IDBIndex[] getIndices();

  @Override
  public String getFullName();

  public String formatPrecision();

  public String formatPrecisionAndScale();
}
