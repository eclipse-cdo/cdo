/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.db.mapping;

import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.ddl.IDBTable;

/**
 * @author Victor Roldan Betancort
 */
public interface InternalITypeMapping extends ITypeMapping
{
  /**
   * @return The db type which is associated with this mapping.
   */
  public DBType getDBType();

  /**
   * Sets the DBField. The name of the DBField is explicitly determined by the corresponding parameter.
   * 
   * @param table
   *          the table to add this field to.
   * @param fieldName
   *          the name for the DBField.
   */
  public void setDBField(IDBTable table, String fieldName);
}
