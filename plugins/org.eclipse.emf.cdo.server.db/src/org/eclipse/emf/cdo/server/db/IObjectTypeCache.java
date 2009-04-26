/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - 271444: [DB] Multiple refactorings https://bugs.eclipse.org/bugs/show_bug.cgi?id=271444  
 */
package org.eclipse.emf.cdo.server.db;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;

import org.eclipse.emf.ecore.EClass;

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
   * @since 2.0
   */
  public void putObjectType(IDBStoreAccessor accessor, CDOID id, EClass type);

  /**
   * @since 2.0
   */
  public void removeObjectType(IDBStoreAccessor accessor, CDOID id);

  /**
   * Return the maximum object id managed by this cache.
   * 
   * @param connection
   *          the DB connection to use.
   * @return the maximum object id.
   * @since 2.0
   */
  public long getMaxId(Connection connection);
}
