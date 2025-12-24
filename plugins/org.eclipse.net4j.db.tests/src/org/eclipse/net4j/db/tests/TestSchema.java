/*
 * Copyright (c) 2008-2013, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.db.tests;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.spi.db.ddl.InternalDBSchema;

/**
 * @author Eike Stepper
 */
public class TestSchema
{
  public static final IDBSchema INSTANCE = DBUtil.createSchema("CDO");

  /**
   * DBTable cdo_repository
   */
  public static final IDBTable REPOSITORY = INSTANCE.addTable("cdo_repository");

  public static final IDBField REPOSITORY_NAME = //
      REPOSITORY.addField("name", DBType.VARCHAR, 255);

  public static final IDBField REPOSITORY_UUID = //
      REPOSITORY.addField("uuid", DBType.VARCHAR, 64);

  public static final IDBField REPOSITORY_STARTS = //
      REPOSITORY.addField("starts", DBType.BIGINT);

  public static final IDBField REPOSITORY_STARTED = //
      REPOSITORY.addField("started", DBType.BIGINT);

  public static final IDBField REPOSITORY_STOPPED = //
      REPOSITORY.addField("stopped", DBType.BIGINT);

  public static final IDBField REPOSITORY_NEXT_CDOID = //
      REPOSITORY.addField("next_cdoid", DBType.BIGINT);

  /**
   * DBTable cdo_packages
   */
  public static final IDBTable PACKAGES = INSTANCE.addTable("cdo_packages");

  public static final IDBField PACKAGES_ID = //
      PACKAGES.addField("id", DBType.INTEGER);

  public static final IDBField PACKAGES_URI = //
      PACKAGES.addField("uri", DBType.VARCHAR, 255);

  public static final IDBField PACKAGES_NAME = //
      PACKAGES.addField("name", DBType.VARCHAR, 255);

  public static final IDBField PACKAGES_ECORE = //
      PACKAGES.addField("ecore", DBType.CLOB);

  public static final IDBField PACKAGES_DYNAMIC = //
      PACKAGES.addField("dynamic", DBType.BOOLEAN);

  public static final IDBField PACKAGES_RANGE_LB = //
      PACKAGES.addField("range_lb", DBType.BIGINT);

  public static final IDBField PACKAGES_RANGE_UB = //
      PACKAGES.addField("range_ub", DBType.BIGINT);

  public static final IDBIndex INDEX_PACKAGES_PK = //
      PACKAGES.addIndex(IDBIndex.Type.PRIMARY_KEY, PACKAGES_ID);

  public static final IDBIndex INDEX_PACKAGES_URI = //
      PACKAGES.addIndex(IDBIndex.Type.UNIQUE, PACKAGES_URI);

  /**
   * DBTable cdo_classes
   */
  public static final IDBTable CLASSES = INSTANCE.addTable("cdo_classes");

  public static final IDBField CLASSES_ID = //
      CLASSES.addField("id", DBType.INTEGER);

  public static final IDBField CLASSES_PACKAGE = //
      CLASSES.addField("package", DBType.INTEGER);

  public static final IDBField CLASSES_CLASSIFIER = //
      CLASSES.addField("classifier", DBType.INTEGER);

  public static final IDBField CLASSES_NAME = //
      CLASSES.addField("name", DBType.VARCHAR, 255);

  public static final IDBField CLASSES_ABSTRACT = //
      CLASSES.addField("abstract", DBType.BOOLEAN);

  public static final IDBIndex INDEX_CLASSES_PK = //
      CLASSES.addIndex(IDBIndex.Type.PRIMARY_KEY, CLASSES_ID);

  /**
   * DBTable cdo_supertypes
   */
  public static final IDBTable SUPERTYPES = INSTANCE.addTable("cdo_supertypes");

  public static final IDBField SUPERTYPES_TYPE = //
      SUPERTYPES.addField("type_id", DBType.INTEGER);

  public static final IDBField SUPERTYPES_SUPERTYPE_PACKAGE = //
      SUPERTYPES.addField("supertype_package", DBType.VARCHAR, 255);

  public static final IDBField SUPERTYPES_SUPERTYPE_CLASSIFIER = //
      SUPERTYPES.addField("supertype_classifier", DBType.INTEGER);

  public static final IDBIndex INDEX_SUPERTYPES_PK = //
      SUPERTYPES.addIndex(IDBIndex.Type.PRIMARY_KEY, SUPERTYPES_TYPE);

  /**
   * DBTable cdo_features
   */
  public static final IDBTable FEATURES = INSTANCE.addTable("cdo_features");

  public static final IDBField FEATURES_ID = //
      FEATURES.addField("id", DBType.INTEGER);

  public static final IDBField FEATURES_CLASS = //
      FEATURES.addField("class", DBType.INTEGER);

  public static final IDBField FEATURES_FEATURE = //
      FEATURES.addField("feature", DBType.INTEGER);

  public static final IDBField FEATURES_NAME = //
      FEATURES.addField("name", DBType.VARCHAR, 255);

  public static final IDBField FEATURES_TYPE = //
      FEATURES.addField("type", DBType.INTEGER);

  public static final IDBField FEATURES_REFERENCE_PACKAGE = //
      FEATURES.addField("reference_package", DBType.VARCHAR, 255);

  public static final IDBField FEATURES_REFERENCE_CLASSIFIER = //
      FEATURES.addField("reference_classifier", DBType.INTEGER);

  public static final IDBField FEATURES_MANY = //
      FEATURES.addField("many", DBType.BOOLEAN);

  public static final IDBField FEATURES_CONTAINMENT = //
      FEATURES.addField("containment", DBType.BOOLEAN);

  public static final IDBField FEATURES_INDEX = //
      FEATURES.addField("idx", DBType.INTEGER);

  public static final IDBIndex INDEX_FEATURES_PK = //
      FEATURES.addIndex(IDBIndex.Type.PRIMARY_KEY, FEATURES_ID);

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
   * Field names of reference tables
   */
  public static final String REFERENCES_FEATURE = "cdo_feature";

  public static final String REFERENCES_SOURCE = "cdo_source";

  public static final String REFERENCES_VERSION = "cdo_version";

  public static final String REFERENCES_IDX = "cdo_idx";

  public static final String REFERENCES_TARGET = "cdo_target";

  static
  {
    ((InternalDBSchema)INSTANCE).lock();
  }
}
