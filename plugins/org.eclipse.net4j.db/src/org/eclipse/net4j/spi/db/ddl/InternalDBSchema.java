/*
 * Copyright (c) 2013, 2019, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.spi.db.ddl;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;

/**
 * @since 4.2
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalDBSchema extends IDBSchema, InternalDBSchemaElement
{
  public static final IDBTable[] NO_TABLES = {};

  @Override
  public IDBSchema getWrapper();

  /**
   * @since 4.12
   */
  public int compareNames(String name1, String name2);

  /**
   * @since 4.12
   */
  public boolean equalNames(String name1, String name2);

  @Override
  public IDBTable addTable(String name);

  @Override
  public IDBTable removeTable(String name);

  public String createIndexName(IDBTable table, IDBIndex.Type type, IDBField[] fields, int position);

  public boolean lock();

  public boolean unlock();

  public void assertUnlocked() throws DBException;
}
