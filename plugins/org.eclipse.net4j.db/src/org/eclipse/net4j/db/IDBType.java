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

import org.eclipse.net4j.internal.db.DBType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Eike Stepper
 */
@Deprecated
public interface IDBType<JAVA>
{
  public static final IDBType<Boolean> BOOLEAN = DBType.BOOLEAN;

  public String getName();

  public int getCode();

  public String format(JAVA value);

  public JAVA get(ResultSet resultSet, int index) throws SQLException;

  public void set(PreparedStatement statement, int index, JAVA value) throws SQLException;
}
