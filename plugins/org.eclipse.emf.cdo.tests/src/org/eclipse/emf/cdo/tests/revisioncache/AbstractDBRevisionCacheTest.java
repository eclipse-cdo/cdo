/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.revisioncache;

import org.eclipse.emf.cdo.common.db.CDOCommonDBUtil;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisionCache;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionCache;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Andre Dietisheim
 */
public abstract class AbstractDBRevisionCacheTest extends AbstractCDORevisionCacheTest
{
  private DataSource dataSource;

  @Override
  protected InternalCDORevisionCache createRevisionCache(CDOSession session) throws Exception
  {
    DataSource dataSource = getDataSource();

    clearDb(dataSource);

    CDORevisionCache revisionCache = CDOCommonDBUtil.createDBCache(//
        getAdapter() //
        , DBUtil.createConnectionProvider(dataSource)//
        , CDOListFactory.DEFAULT//
        , session.getPackageRegistry() //
        , ((InternalCDOSession)session).getRevisionManager().getFactory());
    LifecycleUtil.activate(revisionCache);
    return (InternalCDORevisionCache)revisionCache;
  }

  private DataSource getDataSource()
  {
    if (dataSource == null)
    {
      dataSource = createDataSource();
    }

    return dataSource;
  }

  private void clearDb(DataSource dataSource) throws SQLException
  {
    Connection connection = dataSource.getConnection();
    try
    {
      dropAllTables(connection);
    }
    finally
    {
      DBUtil.close(connection);
    }
  }

  protected abstract DataSource createDataSource();

  protected abstract void dropAllTables(Connection connection);

  protected abstract IDBAdapter getAdapter();
}
