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
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.internal.db.ServerInfo;

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
   * Name of the integer property that configures the maximum length for table names. A value of zero indicates the
   * value of the {@link IDBAdapter#getMaxTableNameLength() db adapter} to be used.
   * 
   * @since 2.0
   */
  public static final String PROP_MAX_TABLE_NAME_LENGTH = "maxTableNameLength";

  /**
   * Name of the integer property that configures the maximum length for column names. A value of zero indicates the
   * value of the {@link IDBAdapter#getMaxFieldNameLength() db adapter} to be used.
   * 
   * @since 2.0
   */
  public static final String PROP_MAX_FIELD_NAME_LENGTH = "maxFieldNameLength";

  /**
   * Name of the String property that specifies a common prefix for table names.
   * 
   * @since 2.0
   */
  public static final String PROP_TABLE_NAME_PREFIX = "tableNamePrefix";

  /**
   * Name of the boolean property that configures whether the table names are made of simple class names or of qualified
   * class names.
   * 
   * @since 2.0
   */
  public static final String PROP_QUALIFIED_NAMES = "qualifiedNames";

  /**
   * Name of the boolean property that configures whether table names and column names are always suffixed with the
   * internal DBID or only in cases where generated names violate the naming constraints of the underlying backend.
   * 
   * @since 2.0
   */
  public static final String PROP_FORCE_NAMES_WITH_ID = "forceNamesWithID";

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
  public CDOClassRef getClassRef(IDBStoreAccessor accessor, int classID);

  /**
   * @since 2.0
   */
  public CloseableIterator<CDOID> readObjectIDs(IDBStoreAccessor accessor);

  /**
   * @since 2.0
   */
  public CDOClassRef readObjectType(IDBStoreAccessor accessor, CDOID id);

  /**
   * @since 2.0
   */
  public void queryResources(IDBStoreAccessor accessor, IStoreAccessor.QueryResourcesContext context);

  /**
   * Responsible for creation of the resource tables (if dbAdapter and connection are not <code>null</code>) and for the
   * association with the static DBIDs (see {@link ServerInfo} with the respective model elements.
   * 
   * @since 2.0
   */
  public void mapResourceTables(IDBAdapter dbAdapter, Connection connection);

  /**
   * Returns the maximum CDOID value.
   * 
   * @since 2.0
   */
  public long repairAfterCrash(IDBAdapter dbAdapter, Connection connection);
}
