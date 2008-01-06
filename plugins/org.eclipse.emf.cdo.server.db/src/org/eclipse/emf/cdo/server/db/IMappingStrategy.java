/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.db;

import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.protocol.model.CDOClassRef;

import org.eclipse.net4j.util.io.CloseableIterator;

import java.sql.Connection;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public interface IMappingStrategy
{
  public String getType();

  public IDBStore getStore();

  public void setStore(IDBStore store);

  public Map<String, String> getProperties();

  public void setProperties(Map<String, String> properties);

  public IClassMapping getClassMapping(CDOClass cdoClass);

  public CloseableIterator<CDOID> readObjectIDs(IDBStoreAccessor storeAccessor, boolean withTypes);

  public CDOClassRef readObjectType(IDBStoreAccessor storeAccessor, CDOID id);

  public CDOID readResourceID(IDBStoreAccessor storeAccessor, String path);

  public String readResourcePath(IDBStoreAccessor storeAccessor, CDOID id);

  /**
   * Must return the next CDOID value to be used for new objects.
   */
  public long repairAfterCrash(Connection connection);
}
