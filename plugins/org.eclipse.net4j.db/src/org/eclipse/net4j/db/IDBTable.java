/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.db;

import org.eclipse.net4j.internal.db.DBField;

/**
 * @author Eike Stepper
 */
public interface IDBTable extends IDBElement
{
  public IDBField addField(String name, DBType type);

  public DBField addField(String name, DBType type, boolean notNull);

  public DBField addField(String name, DBType type, int precision);

  public DBField addField(String name, DBType type, int precision, boolean notNull);

  public DBField addField(String name, DBType type, int precision, int scale);

  public DBField addField(String name, DBType type, int precision, int scale, boolean notNull);

  public IDBField getField(String name);

  public IDBField getField(int index);

  public int getFieldCount();

  public IDBField[] getFields();

  public IDBIndex addIndex(IDBIndex.Type type, IDBField field);

  public IDBIndex addIndex(IDBIndex.Type type, IDBField[] fields);

  public int getIndexCount();

  public IDBIndex[] getIndices();

  public IDBIndex getPrimaryKeyIndex();

  public String sqlInsert();
}
