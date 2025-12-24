/*
 * Copyright (c) 2012, 2015-2017 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db.offline;

import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.offline.Bugzilla_351078_Test;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.util.Arrays;

/**
 * @author Eike Stepper
 */
public class Bugzilla_351078_DB_Test extends Bugzilla_351078_Test
{
  @Override
  protected void check(InternalRepository master, Company masterCompany, String when)
  {
    super.check(master, masterCompany, when);

    // XXX use capabilities?!
    DBOfflineConfig config = (DBOfflineConfig)getRepositoryConfig();
    if (config.withRanges())
    {
      byte[] masterTable = readTable(master);
      byte[] cloneTable = readTable(getRepository());

      assertEquals(true, Arrays.equals(masterTable, cloneTable));
    }
  }

  protected byte[] readTable(InternalRepository repository)
  {
    IDBStore store = (IDBStore)repository.getStore();
    IClassMapping classMapping = store.getMappingStrategy().getClassMapping(getModel1Package().getCompany());
    IListMapping listMapping = classMapping.getListMapping(getModel1Package().getCompany_Categories());
    IDBTable table = listMapping.getDBTables().iterator().next();

    Connection connection = null;

    try
    {
      connection = store.getConnection();

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      DBUtil.serializeTable(new ExtendedDataOutputStream(out), connection, table, null, " ORDER BY CDO_SOURCE, CDO_BRANCH, CDO_VERSION_ADDED, CDO_IDX");

      return out.toByteArray();
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
    finally
    {
      DBUtil.close(connection);
    }
  }
}
