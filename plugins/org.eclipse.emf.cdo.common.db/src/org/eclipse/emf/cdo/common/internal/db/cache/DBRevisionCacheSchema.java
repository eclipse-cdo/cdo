/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.common.internal.db.cache;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.spi.db.DBSchema;

/**
 * @author Andre Dietisheim
 */
public class DBRevisionCacheSchema extends DBSchema
{
  public static final DBRevisionCacheSchema INSTANCE = new DBRevisionCacheSchema();

  /**
   * DBTable dbrevisioncache_revisions
   */
  public static final IDBTable REVISIONS = INSTANCE.addTable("dbrevisioncache_revisions");

  public static final IDBField REVISIONS_ID = //
  REVISIONS.addField("id", DBType.BIGINT);

  public static final IDBIndex INDEX_REVISIONS_ID = //
  REVISIONS.addIndex(IDBIndex.Type.NON_UNIQUE, REVISIONS_ID);

  public static final IDBField REVISIONS_VERSION = //
  REVISIONS.addField("version", DBType.BIGINT);

  public static final IDBIndex INDEX_REVISIONS_VERSION = //
  REVISIONS.addIndex(IDBIndex.Type.NON_UNIQUE, REVISIONS_VERSION);

  public static final IDBIndex INDEX_REVISIONS_PK = //
  REVISIONS.addIndex(IDBIndex.Type.PRIMARY_KEY, REVISIONS_ID, REVISIONS_VERSION);

  public static final IDBField REVISIONS_CREATED = //
  REVISIONS.addField("created", DBType.BIGINT);

  public static final IDBField REVISIONS_REVISED = //
  REVISIONS.addField("revised", DBType.BIGINT);

  public static final IDBField REVISIONS_CDOREVISION = //
  REVISIONS.addField("cdorevision", DBType.BLOB);

  public static final IDBField REVISIONS_RESOURCENODENAME = //
  REVISIONS.addField("resourcenode_name", DBType.VARCHAR, false);

  public static final IDBIndex INDEX_REVISIONS_RESOURCENODENAME = //
  REVISIONS.addIndex(IDBIndex.Type.NON_UNIQUE, REVISIONS_RESOURCENODENAME);

  public static final IDBField REVISIONS_CONTAINERID = //
  REVISIONS.addField("container_id", DBType.BIGINT, false);

  public static final IDBIndex INDEX_REVISIONS_CONTAINERID = //
  REVISIONS.addIndex(IDBIndex.Type.NON_UNIQUE, REVISIONS_CONTAINERID);

  private DBRevisionCacheSchema()
  {
    super("dbRevisionCache");
  }

  static
  {
    INSTANCE.lock();
  }
}
