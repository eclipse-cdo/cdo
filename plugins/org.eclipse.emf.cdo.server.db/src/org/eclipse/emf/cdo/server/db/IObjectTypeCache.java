/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - 271444: [DB] Multiple refactorings bug 271444
 */
package org.eclipse.emf.cdo.server.db;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;

import org.eclipse.emf.ecore.EClass;

import java.io.IOException;
import java.sql.Connection;

/**
 * @author Eike Stepper
 */
public interface IObjectTypeCache
{
  /**
   * @since 2.0
   */
  public CDOClassifierRef getObjectType(IDBStoreAccessor accessor, CDOID id);

  /**
   * @since 3.0
   */
  public void putObjectType(IDBStoreAccessor accessor, long timeStamp, CDOID id, EClass type);

  /**
   * @since 2.0
   */
  public void removeObjectType(IDBStoreAccessor accessor, CDOID id);

  /**
   * Return the maximum object id managed by this cache.
   * 
   * @param connection
   *          the DB connection to use.
   * @return the maximum object ID.
   * @since 3.0
   */
  public long getMaxID(Connection connection);

  /**
   * @since 3.0
   */
  public void rawExport(Connection connection, CDODataOutput out, long fromCommitTime, long toCommitTime)
      throws IOException;

  /**
   * @since 3.0
   */
  public void rawImport(Connection connection, CDODataInput in) throws IOException;
}
