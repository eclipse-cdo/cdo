/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Andre Dietisheim - further implementations
 */
package org.eclipse.emf.cdo.common.db;

import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.internal.db.cache.DBRevisionCache;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCache;

import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public final class CDOCommonDBUtil
{
  private CDOCommonDBUtil()
  {
  }

  /**
   * Creates and returns a new JDBC-based revision cache.
   * <p>
   * TODO Add all config parameters!
   */
  public static CDORevisionCache createDBCache(IDBAdapter dbAdapter, IDBConnectionProvider dbConnectionProvider,
      CDOIDProvider idProvider, CDOListFactory listFactory, CDOPackageRegistry packageRegistry,
      CDORevisionManager revisionManager)
  {
    DBRevisionCache cache = new DBRevisionCache();
    cache.setDBAdapter(dbAdapter);
    cache.setDBConnectionProvider(dbConnectionProvider);
    cache.setIdProvider(idProvider);
    cache.setListFactory(listFactory);
    cache.setPackageRegistry(packageRegistry);
    cache.setRevisionManager(revisionManager);

    return cache;
  }
}
