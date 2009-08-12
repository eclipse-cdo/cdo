/**
 * Copyright (c) 2004 - 2009 Andre Dietisheim (Bern, Switzerland) and others.
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
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCache;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.h2.H2Adapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

import javax.sql.DataSource;

import java.sql.Connection;

/**
 * @author Andre Dietisheim
 */
public abstract class AbstractDBRevisionCacheTest extends AbstractCDORevisionCacheTest
{
  protected interface IDBProvider
  {
    public DataSource createDataSource();

    public void dropAllTables(Connection connection);
  }

  @Override
  protected CDORevisionCache createRevisionCache(CDOSession session) throws Exception
  {
    IDBProvider dbProvider = createDbProvider();
    DataSource dataSource = dbProvider.createDataSource();
    Connection connection = dataSource.getConnection();

    try
    {
      dbProvider.dropAllTables(connection);
    }
    finally
    {
      DBUtil.close(connection);
    }

    CDORevisionCache revisionCache = CDOCommonDBUtil.createDBCache(//
        new H2Adapter() //
        , DBUtil.createConnectionProvider(dataSource)//
        , CDOListFactory.DEFAULT//
        , session.getPackageRegistry() //
        , ((InternalCDOSession)session).getRevisionManager().getFactory());
    LifecycleUtil.activate(revisionCache);
    return revisionCache;
  }

  protected abstract IDBProvider createDbProvider();
}
