/*
 * Copyright (c) 2008, 2009, 2011-2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.db.tests;

import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.hsqldb.HSQLDBDataSource;

import javax.sql.DataSource;

/**
 * @author Eike Stepper
 */
public class HsqldbTest extends AbstractDBTest
{
  @Override
  protected IDBAdapter createAdapter()
  {
    return new org.eclipse.net4j.db.hsqldb.HSQLDBAdapter();
  }

  @Override
  protected DataSource createDataSource()
  {
    HSQLDBDataSource dataSource = new HSQLDBDataSource();
    dataSource.setDatabase("jdbc:hsqldb:mem:dbtest");
    dataSource.setUser("sa");
    return dataSource;
  }
}
