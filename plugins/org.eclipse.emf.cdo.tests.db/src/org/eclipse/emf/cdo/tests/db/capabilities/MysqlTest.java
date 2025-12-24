/*
 * Copyright (c) 2009, 2011, 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
