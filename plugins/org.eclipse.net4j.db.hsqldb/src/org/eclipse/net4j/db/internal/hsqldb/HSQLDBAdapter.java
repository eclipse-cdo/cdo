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
package org.eclipse.net4j.db.internal.hsqldb;

import org.eclipse.net4j.internal.db.DBAdapter;

import org.hsqldb.jdbcDriver;

import java.sql.Driver;
import java.util.Arrays;

/**
 * @author Eike Stepper
 */
public class HSQLDBAdapter extends DBAdapter
{
  public HSQLDBAdapter()
  {
    super("hsqldb", "1.8.0.8");
  }

  public Driver getJDBCDriver()
  {
    return new jdbcDriver();
  }
}
