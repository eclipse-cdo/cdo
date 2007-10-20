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
package org.eclipse.net4j.db.internal.mysql;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.IDBField;
import org.eclipse.net4j.internal.db.DBAdapter;
import org.eclipse.net4j.internal.db.DBField;

import org.gjt.mm.mysql.Driver;

import java.sql.SQLException;
import java.util.Arrays;

/**
 * @author Eike Stepper
 */
public class MYSQLAdapter extends DBAdapter
{
  public MYSQLAdapter()
  {
    super("mysql", "5.1.5");
  }

  public Driver getJDBCDriver()
  {
    try
    {
      return new Driver();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }
}
