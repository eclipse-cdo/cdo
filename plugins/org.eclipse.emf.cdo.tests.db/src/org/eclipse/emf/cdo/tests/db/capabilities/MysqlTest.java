/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db.capabilities;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBConnectionProvider;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * @author Stefan Winkler
 */
public class MysqlTest extends AbstractCapabilityTest
{
  private IDBConnectionProvider provider;

  public MysqlTest()
  {
    super("mysql");
    MysqlDataSource mysqlds = new MysqlDataSource();
    mysqlds.setUrl("jdbc:mysql://localhost:33306/winkler_r2");
    mysqlds.setUser("winkler");
    mysqlds.setPassword("winkler");
    provider = DBUtil.createConnectionProvider(mysqlds);
  }

  @Override
  protected IDBConnectionProvider getConnectionProvider()
  {
    return provider;
  }
}
