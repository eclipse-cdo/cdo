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
 *
 */
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.spi.db.DBSchema;

/**
 * @author Eike Stepper
 */
public class CDODBSchema extends DBSchema
{
  public static final CDODBSchema INSTANCE = new CDODBSchema();

  /**
   * DBTable cdo_repository
   */
  public static final IDBTable REPOSITORY = INSTANCE.addTable("cdo_repository");

  public static final IDBField REPOSITORY_CREATED = //
  REPOSITORY.addField("created", DBType.BIGINT);

  public static final IDBField REPOSITORY_STARTS = //
  REPOSITORY.addField("starts", DBType.BIGINT);

  public static final IDBField REPOSITORY_STARTED = //
  REPOSITORY.addField("started", DBType.BIGINT);

  public static final IDBField REPOSITORY_STOPPED = //
  REPOSITORY.addField("stopped", DBType.BIGINT);

  public static final IDBField REPOSITORY_NEXT_CDOID = //
  REPOSITORY.addField("next_cdoid", DBType.BIGINT);

  public static final IDBField REPOSITORY_NEXT_METAID = //
  REPOSITORY.addField("next_metaid", DBType.BIGINT);

  /**
   * DBTable cdo_package_units
   */
  public static final IDBTable PACKAGE_UNITS = INSTANCE.addTable("cdo_package_units");

  public static final IDBField PACKAGE_UNITS_ID = //
  PACKAGE_UNITS.addField("id", DBType.VARCHAR, 255);

  public static final IDBField PACKAGE_UNITS_ORIGINAL_TYPE = //
  PACKAGE_UNITS.addField("original_type", DBType.INTEGER);

  public static final IDBField PACKAGE_UNITS_TIME_STAMP = //
  PACKAGE_UNITS.addField("time_stamp", DBType.BIGINT);

  public static final IDBField PACKAGE_UNITS_PACKAGE_DATA = //
  PACKAGE_UNITS.addField("package_data", DBType.BLOB);

  public static final IDBIndex INDEX_PACKAGE_UNITS_PK = //
  PACKAGE_UNITS.addIndex(IDBIndex.Type.PRIMARY_KEY, PACKAGE_UNITS_ID);

  /**
   * DBTable cdo_packages
   */
  public static final IDBTable PACKAGE_INFOS = INSTANCE.addTable("cdo_package_infos");

  public static final IDBField PACKAGE_INFOS_URI = //
  PACKAGE_INFOS.addField("uri", DBType.VARCHAR, 255);

  public static final IDBField PACKAGE_INFOS_PARENT = //
  PACKAGE_INFOS.addField("parent", DBType.VARCHAR, 255);

  public static final IDBField PACKAGE_INFOS_UNIT = //
  PACKAGE_INFOS.addField("unit", DBType.VARCHAR, 255);

  public static final IDBField PACKAGE_INFOS_META_LB = //
  PACKAGE_INFOS.addField("meta_lb", DBType.BIGINT);

  public static final IDBField PACKAGE_INFOS_META_UB = //
  PACKAGE_INFOS.addField("meta_ub", DBType.BIGINT);

  public static final IDBIndex INDEX_PACKAGE_INFOS_PK = //
  PACKAGE_INFOS.addIndex(IDBIndex.Type.PRIMARY_KEY, PACKAGE_INFOS_URI);

  public static final IDBIndex INDEX_PACKAGE_INFOS_PARENT = //
  PACKAGE_INFOS.addIndex(IDBIndex.Type.NON_UNIQUE, PACKAGE_INFOS_PARENT);

  public static final IDBIndex INDEX_PACKAGE_INFOS_UNIT = //
  PACKAGE_INFOS.addIndex(IDBIndex.Type.NON_UNIQUE, PACKAGE_INFOS_UNIT);

  /**
   * Name of object table
   */
  public static final String CDO_OBJECTS = "cdo_objects";

  /**
   * Field names of attribute tables
   */
  public static final String ATTRIBUTES_ID = "cdo_id";

  public static final String ATTRIBUTES_VERSION = "cdo_version";

  public static final String ATTRIBUTES_CLASS = "cdo_class";

  public static final String ATTRIBUTES_CREATED = "cdo_created";

  public static final String ATTRIBUTES_REVISED = "cdo_revised";

  public static final String ATTRIBUTES_RESOURCE = "cdo_resource";

  public static final String ATTRIBUTES_CONTAINER = "cdo_container";

  public static final String ATTRIBUTES_FEATURE = "cdo_feature";

  /**
   * Field names of list tables
   */
  public static final String LIST_FEATURE = "cdo_feature";

  public static final String LIST_REVISION_ID = "cdo_source";

  public static final String LIST_REVISION_VERSION = "cdo_version";

  public static final String LIST_IDX = "cdo_idx";

  public static final String LIST_VALUE = "cdo_value";

  private CDODBSchema()
  {
    super("CDO");
  }

  static
  {
    INSTANCE.lock();
  }
}
