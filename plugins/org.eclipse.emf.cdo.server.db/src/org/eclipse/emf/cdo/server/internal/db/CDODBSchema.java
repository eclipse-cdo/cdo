/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - 271444: [DB] Multiple refactorings bug 271444
 *    Stefan Winkler - 249610: [DB] Support external references (Implementation)
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
  public static final IDBTable REPOSITORY = INSTANCE.addTable("cdo_repository"); //$NON-NLS-1$

  public static final IDBField REPOSITORY_CREATED = //
  REPOSITORY.addField("created", DBType.BIGINT); //$NON-NLS-1$

  public static final IDBField REPOSITORY_STARTS = //
  REPOSITORY.addField("starts", DBType.BIGINT); //$NON-NLS-1$

  public static final IDBField REPOSITORY_STARTED = //
  REPOSITORY.addField("started", DBType.BIGINT); //$NON-NLS-1$

  public static final IDBField REPOSITORY_STOPPED = //
  REPOSITORY.addField("stopped", DBType.BIGINT); //$NON-NLS-1$

  public static final IDBField REPOSITORY_LAST_CDOID = //
  REPOSITORY.addField("last_cdoid", DBType.BIGINT); //$NON-NLS-1$

  public static final IDBField REPOSITORY_LAST_METAID = //
  REPOSITORY.addField("last_metaid", DBType.BIGINT); //$NON-NLS-1$

  public static final IDBField REPOSITORY_LAST_BRANCHID = //
  REPOSITORY.addField("last_branchid", DBType.INTEGER); //$NON-NLS-1$

  /**
   * DBTable cdo_package_units
   */
  public static final IDBTable PACKAGE_UNITS = INSTANCE.addTable("cdo_package_units"); //$NON-NLS-1$

  public static final IDBField PACKAGE_UNITS_ID = //
  PACKAGE_UNITS.addField("id", DBType.VARCHAR, 255); //$NON-NLS-1$

  public static final IDBField PACKAGE_UNITS_ORIGINAL_TYPE = //
  PACKAGE_UNITS.addField("original_type", DBType.INTEGER); //$NON-NLS-1$

  public static final IDBField PACKAGE_UNITS_TIME_STAMP = //
  PACKAGE_UNITS.addField("time_stamp", DBType.BIGINT); //$NON-NLS-1$

  public static final IDBField PACKAGE_UNITS_PACKAGE_DATA = //
  PACKAGE_UNITS.addField("package_data", DBType.BLOB); //$NON-NLS-1$

  public static final IDBIndex INDEX_PACKAGE_UNITS_PK = //
  PACKAGE_UNITS.addIndex(IDBIndex.Type.PRIMARY_KEY, PACKAGE_UNITS_ID);

  /**
   * DBTable cdo_packages
   */
  public static final IDBTable PACKAGE_INFOS = INSTANCE.addTable("cdo_package_infos"); //$NON-NLS-1$

  public static final IDBField PACKAGE_INFOS_URI = //
  PACKAGE_INFOS.addField("uri", DBType.VARCHAR, 255); //$NON-NLS-1$

  public static final IDBField PACKAGE_INFOS_PARENT = //
  PACKAGE_INFOS.addField("parent", DBType.VARCHAR, 255); //$NON-NLS-1$

  public static final IDBField PACKAGE_INFOS_UNIT = //
  PACKAGE_INFOS.addField("unit", DBType.VARCHAR, 255); //$NON-NLS-1$

  public static final IDBField PACKAGE_INFOS_META_LB = //
  PACKAGE_INFOS.addField("meta_lb", DBType.BIGINT); //$NON-NLS-1$

  public static final IDBField PACKAGE_INFOS_META_UB = //
  PACKAGE_INFOS.addField("meta_ub", DBType.BIGINT); //$NON-NLS-1$

  public static final IDBIndex INDEX_PACKAGE_INFOS_PK = //
  PACKAGE_INFOS.addIndex(IDBIndex.Type.PRIMARY_KEY, PACKAGE_INFOS_URI);

  public static final IDBIndex INDEX_PACKAGE_INFOS_PARENT = //
  PACKAGE_INFOS.addIndex(IDBIndex.Type.NON_UNIQUE, PACKAGE_INFOS_PARENT);

  public static final IDBIndex INDEX_PACKAGE_INFOS_UNIT = //
  PACKAGE_INFOS.addIndex(IDBIndex.Type.NON_UNIQUE, PACKAGE_INFOS_UNIT);

  /**
   * DBTable cdo_branches
   */
  public static final IDBTable BRANCHES = INSTANCE.addTable("cdo_branches"); //$NON-NLS-1$

  public static final IDBField BRANCHES_ID = //
  BRANCHES.addField("id", DBType.INTEGER); //$NON-NLS-1$

  public static final IDBField BRANCHES_NAME = //
  BRANCHES.addField("name", DBType.VARCHAR); //$NON-NLS-1$

  public static final IDBField BRANCHES_BASE_BRANCH_ID = //
  BRANCHES.addField("base_id", DBType.INTEGER); //$NON-NLS-1$

  public static final IDBField BRANCHES_BASE_TIMESTAMP = //
  BRANCHES.addField("base_time", DBType.BIGINT); //$NON-NLS-1$

  public static final IDBIndex INDEX_BRANCHES_ID = //
  BRANCHES.addIndex(IDBIndex.Type.PRIMARY_KEY, BRANCHES_ID);

  public static final String SQL_CREATE_BRANCH = "INSERT INTO " + BRANCHES + " (" + BRANCHES_ID + ", " + BRANCHES_NAME
      + ", " + BRANCHES_BASE_BRANCH_ID + ", " + BRANCHES_BASE_TIMESTAMP + ") VALUES (?, ?, ?, ?)";

  public static final String SQL_LOAD_BRANCH = "SELECT " + BRANCHES_NAME + ", " + BRANCHES_BASE_BRANCH_ID + ", "
      + BRANCHES_BASE_TIMESTAMP + " FROM " + BRANCHES + " WHERE " + BRANCHES_ID + "=?";

  public static final String SQL_LOAD_SUB_BRANCHES = "SELECT " + BRANCHES_ID + ", " + BRANCHES_NAME + ", "
      + BRANCHES_BASE_TIMESTAMP + " FROM " + BRANCHES + " WHERE " + BRANCHES_BASE_BRANCH_ID + "=?";

  /**
   * DBTable cdo_external_refs
   */
  public static final IDBTable EXTERNAL_REFS = INSTANCE.addTable("cdo_external_refs"); //$NON-NLS-1$

  public static final IDBField EXTERNAL_ID = //
  EXTERNAL_REFS.addField("id", DBType.BIGINT); //$NON-NLS-1$

  public static final IDBField EXTERNAL_URI = //
  EXTERNAL_REFS.addField("uri", DBType.VARCHAR); //$NON-NLS-1$

  public static final IDBIndex INDEX_EXTERNAL_REFS_ID = //
  EXTERNAL_REFS.addIndex(IDBIndex.Type.PRIMARY_KEY, EXTERNAL_ID);

  public static final IDBIndex INDEX_EXTERNAL_REFS_HASH = //
  EXTERNAL_REFS.addIndex(IDBIndex.Type.NON_UNIQUE, EXTERNAL_URI);

  /**
   * Name of object table
   */
  public static final String CDO_OBJECTS = "cdo_objects"; //$NON-NLS-1$

  /**
   * Field names of attribute tables
   */
  public static final String ATTRIBUTES_ID = "cdo_id"; //$NON-NLS-1$

  public static final String ATTRIBUTES_BRANCH = "cdo_branch"; //$NON-NLS-1$

  public static final String ATTRIBUTES_VERSION = "cdo_version"; //$NON-NLS-1$

  public static final String ATTRIBUTES_CLASS = "cdo_class"; //$NON-NLS-1$

  public static final String ATTRIBUTES_CREATED = "cdo_created"; //$NON-NLS-1$

  public static final String ATTRIBUTES_REVISED = "cdo_revised"; //$NON-NLS-1$

  public static final String ATTRIBUTES_RESOURCE = "cdo_resource"; //$NON-NLS-1$

  public static final String ATTRIBUTES_CONTAINER = "cdo_container"; //$NON-NLS-1$

  public static final String ATTRIBUTES_FEATURE = "cdo_feature"; //$NON-NLS-1$

  /**
   * Field names of list tables
   */
  public static final String LIST_FEATURE = "cdo_feature"; //$NON-NLS-1$

  public static final String LIST_REVISION_ID = "cdo_source"; //$NON-NLS-1$

  public static final String LIST_REVISION_VERSION = "cdo_version"; //$NON-NLS-1$

  public static final String LIST_REVISION_VERSION_ADDED = "cdo_version_added"; //$NON-NLS-1$

  public static final String LIST_REVISION_VERSION_REMOVED = "cdo_version_removed"; //$NON-NLS-1$
  
  public static final String LIST_REVISION_BRANCH = "cdo_branch"; //$NON-NLS-1$ 

  public static final String LIST_IDX = "cdo_idx"; //$NON-NLS-1$

  public static final String LIST_VALUE = "cdo_value"; //$NON-NLS-1$

  /**
   * Field names of featuremap tables
   */
  public static final String FEATUREMAP_REVISION_ID = "cdo_id"; //$NON-NLS-1$

  public static final String FEATUREMAP_VERSION = "cdo_version"; //$NON-NLS-1$

  public static final String FEATUREMAP_VERSION_ADDED = "cdo_version_added"; //$NON-NLS-1$

  public static final String FEATUREMAP_VERSION_REMOVED = "cdo_version_removed"; //$NON-NLS-1$

  public static final String FEATUREMAP_BRANCH = "cdo_branch"; //$NON-NLS-1$ 

  public static final String FEATUREMAP_IDX = "cdo_idx"; //$NON-NLS-1$

  public static final String FEATUREMAP_TAG = "cdo_tag"; //$NON-NLS-1$

  public static final String FEATUREMAP_VALUE = "cdo_value"; //$NON-NLS-1$

  private CDODBSchema()
  {
    super("CDO"); //$NON-NLS-1$
  }

  static
  {
    INSTANCE.lock();
  }
}
