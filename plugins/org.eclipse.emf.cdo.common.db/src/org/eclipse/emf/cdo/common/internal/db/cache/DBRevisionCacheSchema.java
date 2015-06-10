/*
 * Copyright (c) 2009, 2011-2013, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.common.internal.db.cache;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.spi.db.ddl.InternalDBSchema;

/**
 * @author Andre Dietisheim
 */
public class DBRevisionCacheSchema
{
  public static final IDBSchema INSTANCE = DBUtil.createSchema("DBRevisionCache");

  /**
   * DBTable dbrevisioncache_revisions.
   * <p>
   * TODO Make name configurable!
   */
  public static final IDBTable REVISIONS = INSTANCE.addTable("dbrevisioncache_revisions");

  public static final IDBField REVISIONS_ID = //
      REVISIONS.addField("id", DBType.VARCHAR, 254);

  public static final IDBField REVISIONS_VERSION = //
      REVISIONS.addField("version", DBType.INTEGER);

  public static final IDBField REVISIONS_CREATED = //
      REVISIONS.addField("created", DBType.BIGINT);

  public static final IDBField REVISIONS_REVISED = //
      REVISIONS.addField("revised", DBType.BIGINT);

  public static final IDBField REVISIONS_CDOREVISION = //
      REVISIONS.addField("revision", DBType.BLOB);

  public static final IDBField REVISIONS_RESOURCENODE_NAME = //
      REVISIONS.addField("resourcenode_name", DBType.VARCHAR, false);

  public static final IDBField REVISIONS_CONTAINERID = //
      REVISIONS.addField("container_id", DBType.BIGINT, false);

  public static final IDBIndex INDEX_REVISIONS_RESOURCENODENAME = //
      REVISIONS.addIndex(IDBIndex.Type.NON_UNIQUE, REVISIONS_RESOURCENODE_NAME);

  public static final IDBIndex INDEX_REVISIONS_ID = //
      REVISIONS.addIndex(IDBIndex.Type.NON_UNIQUE, REVISIONS_ID);

  public static final IDBIndex INDEX_REVISIONS_VERSION = //
      REVISIONS.addIndex(IDBIndex.Type.NON_UNIQUE, REVISIONS_VERSION);

  public static final IDBIndex INDEX_REVISIONS_PK = //
      REVISIONS.addIndex(IDBIndex.Type.PRIMARY_KEY, REVISIONS_ID, REVISIONS_VERSION);

  public static final IDBIndex INDEX_REVISIONS_CONTAINERID = //
      REVISIONS.addIndex(IDBIndex.Type.NON_UNIQUE, REVISIONS_CONTAINERID);

  static
  {
    ((InternalDBSchema)INSTANCE).lock();
  }
}
