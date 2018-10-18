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

import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;

/**
 * Extension interface to {@link IListMapping3}.
 *
 * @author Eike Stepper
 * @since 4.7
 */
public interface IListMapping4 extends IListMapping3
{
  public IDBTable getTable();

  public void setTable(IDBTable table);

  public void initTable(IDBStoreAccessor accessor);

  public IDBTable createTable(IDBSchema schema, String tableName);
}
