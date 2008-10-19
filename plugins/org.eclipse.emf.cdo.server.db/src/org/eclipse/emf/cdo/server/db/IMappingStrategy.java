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

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOClassRef;
import org.eclipse.emf.cdo.server.IStoreReader.QueryResourcesContext;

import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.util.collection.CloseableIterator;

import java.sql.Connection;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public interface IMappingStrategy
{
  /**
   * @since 2.0
   */
  public static final String PROP_QUALIFIED_NAMES = "qualifiedNames";

  /**
   * @since 2.0
   */
  public static final String PROP_TO_MANY_REFERENCE_MAPPING = "toManyReferenceMapping";

  /**
   * @since 2.0
   */
  public static final String PROP_TO_ONE_REFERENCE_MAPPING = "toOneReferenceMapping";

  public String getType();

  public IDBStore getStore();

  public void setStore(IDBStore store);

  public Map<String, String> getProperties();

  public void setProperties(Map<String, String> properties);

  public IClassMapping getClassMapping(CDOClass cdoClass);

  /**
   * @since 2.0
   */
  public CDOClassRef getClassRef(IDBStoreReader storeReader, int classID);

  /**
   * @since 2.0
   */
  public CloseableIterator<CDOID> readObjectIDs(IDBStoreReader storeReader);

  public CDOClassRef readObjectType(IDBStoreReader storeReader, CDOID id);

  /**
   * @since 2.0
   */
  public CDOID readResourceID(IDBStoreReader storeReader, CDOID folderID, String name, long timeStamp);

  /**
   * @since 2.0
   */
  public void queryResources(IDBStoreReader storeReader, QueryResourcesContext context);

  /**
   * @since 2.0
   */
  public void createResourceTables(IDBAdapter dbAdapter, Connection connection);

  /**
   * Returns the maximum CDOID value.
   * 
   * @since 2.0
   */
  public long repairAfterCrash(IDBAdapter dbAdapter, Connection connection);
}
