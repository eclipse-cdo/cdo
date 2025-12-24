/*
 * Copyright (c) 2009, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
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

import org.h2.jdbcx.JdbcDataSource;

/**
 * @author Stefan Winkler
 */
public class H2Test extends AbstractCapabilityTest
{
  public H2Test()
  {
    super("h2");
    JdbcDataSource h2ds = new JdbcDataSource();
    h2ds = new JdbcDataSource();
    h2ds.setURL("jdbc:h2:file:c:/temp/h2test");
    h2ds.setUser("sa");
    provider = DBUtil.createConnectionProvider(h2ds);
  }

  IDBConnectionProvider provider = null;

  @Override
  protected IDBConnectionProvider getConnectionProvider()
  {
    return provider;
  }
}
