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

import javax.sql.DataSource;

/**
 * @author Eike Stepper
 */
public interface ISchema
{
  public String getName();

  public ITable addTable(String name);

  public ITable getTable(String name);

  public ITable[] getTables();

  public void create(IDBAdapter dbAdapter, DataSource dataSource);
}
