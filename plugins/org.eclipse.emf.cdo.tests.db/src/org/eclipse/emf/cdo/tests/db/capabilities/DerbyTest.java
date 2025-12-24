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
import org.eclipse.net4j.util.io.TMPUtil;

import org.apache.derby.jdbc.EmbeddedDataSource;

import java.io.File;

/**
 * @author Stefan Winkler
 */
public class DerbyTest extends AbstractCapabilityTest
{
  private IDBConnectionProvider provider;

  public DerbyTest()
  {
    super("derby");
    File dbFolder = TMPUtil.createTempFolder("derby_", "_test");
    dbFolder.delete();
    EmbeddedDataSource derbyds = new EmbeddedDataSource();
    derbyds.setDatabaseName(dbFolder.getAbsolutePath());
    derbyds.setCreateDatabase("create");
    provider = DBUtil.createConnectionProvider(derbyds);
  }

  @Override
  protected IDBConnectionProvider getConnectionProvider()
  {
    return provider;
  }
}
