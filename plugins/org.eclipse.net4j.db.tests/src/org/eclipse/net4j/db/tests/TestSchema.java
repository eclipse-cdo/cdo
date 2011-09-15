/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.db.tests;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.spi.db.DBSchema;

/**
 * @author Eike Stepper
 */
public class TestSchema extends DBSchema
{
  public static final TestSchema INSTANCE = new TestSchema();

  /**
   * DBTable cdo_repository
   */
  public static final IDBTable REPOSITORY = INSTANCE.addTable("cdo_repository"); //$NON-NLS-1$

  public static final IDBField REPOSITORY_NAME = //
  REPOSITORY.addField("name", DBType.VARCHAR, 255); //$NON-NLS-1$

  public static final IDBField REPOSITORY_UUID = //
  REPOSITORY.addField("uuid", DBType.VARCHAR, 64); //$NON-NLS-1$

  public static final IDBField REPOSITORY_STARTS = //
  REPOSITORY.addField("starts", DBType.BIGINT); //$NON-NLS-1$

  public static final IDBField REPOSITORY_STARTED = //
  REPOSITORY.addField("started", DBType.BIGINT); //$NON-NLS-1$

  public static final IDBField REPOSITORY_STOPPED = //
  REPOSITORY.addField("stopped", DBType.BIGINT); //$NON-NLS-1$

  public static final IDBField REPOSITORY_NEXT_CDOID = //
  REPOSITORY.addField("next_cdoid", DBType.BIGINT); //$NON-NLS-1$

  /**
   * DBTable cdo_packages
   */
  public static final IDBTable PACKAGES = INSTANCE.addTable("cdo_packages"); //$NON-NLS-1$

  public static final IDBField PACKAGES_ID = //
  PACKAGES.addField("id", DBType.INTEGER); //$NON-NLS-1$

  public static final IDBField PACKAGES_URI = //
  PACKAGES.addField("uri", DBType.VARCHAR, 255); //$NON-NLS-1$

  public static final IDBField PACKAGES_NAME = //
  PACKAGES.addField("name", DBType.VARCHAR, 255); //$NON-NLS-1$

  public static final IDBField PACKAGES_ECORE = //
  PACKAGES.addField("ecore", DBType.CLOB); //$NON-NLS-1$

  public static final IDBField PACKAGES_DYNAMIC = //
  PACKAGES.addField("dynamic", DBType.BOOLEAN); //$NON-NLS-1$

  public static final IDBField PACKAGES_RANGE_LB = //
  PACKAGES.addField("range_lb", DBType.BIGINT); //$NON-NLS-1$

  public static final IDBField PACKAGES_RANGE_UB = //
  PACKAGES.addField("range_ub", DBType.BIGINT); //$NON-NLS-1$

  public static final IDBIndex INDEX_PACKAGES_PK = //
  PACKAGES.addIndex(IDBIndex.Type.PRIMARY_KEY, PACKAGES_ID);

  public static final IDBIndex INDEX_PACKAGES_URI = //
  PACKAGES.addIndex(IDBIndex.Type.UNIQUE, PACKAGES_URI);

  /**
   * DBTable cdo_classes
   */
  public static final IDBTable CLASSES = INSTANCE.addTable("cdo_classes"); //$NON-NLS-1$

  public static final IDBField CLASSES_ID = //
  CLASSES.addField("id", DBType.INTEGER); //$NON-NLS-1$

  public static final IDBField CLASSES_PACKAGE = //
  CLASSES.addField("package", DBType.INTEGER); //$NON-NLS-1$

  public static final IDBField CLASSES_CLASSIFIER = //
  CLASSES.addField("classifier", DBType.INTEGER); //$NON-NLS-1$

  public static final IDBField CLASSES_NAME = //
  CLASSES.addField("name", DBType.VARCHAR, 255); //$NON-NLS-1$

  public static final IDBField CLASSES_ABSTRACT = //
  CLASSES.addField("abstract", DBType.BOOLEAN); //$NON-NLS-1$

  public static final IDBIndex INDEX_CLASSES_PK = //
  CLASSES.addIndex(IDBIndex.Type.PRIMARY_KEY, CLASSES_ID);

  /**
   * DBTable cdo_supertypes
   */
  public static final IDBTable SUPERTYPES = INSTANCE.addTable("cdo_supertypes"); //$NON-NLS-1$

  public static final IDBField SUPERTYPES_TYPE = //
  SUPERTYPES.addField("type_id", DBType.INTEGER); //$NON-NLS-1$

  public static final IDBField SUPERTYPES_SUPERTYPE_PACKAGE = //
  SUPERTYPES.addField("supertype_package", DBType.VARCHAR, 255); //$NON-NLS-1$

  public static final IDBField SUPERTYPES_SUPERTYPE_CLASSIFIER = //
  SUPERTYPES.addField("supertype_classifier", DBType.INTEGER); //$NON-NLS-1$

  public static final IDBIndex INDEX_SUPERTYPES_PK = //
  SUPERTYPES.addIndex(IDBIndex.Type.PRIMARY_KEY, SUPERTYPES_TYPE);

  /**
   * DBTable cdo_features
   */
  public static final IDBTable FEATURES = INSTANCE.addTable("cdo_features"); //$NON-NLS-1$

  public static final IDBField FEATURES_ID = //
  FEATURES.addField("id", DBType.INTEGER); //$NON-NLS-1$

  public static final IDBField FEATURES_CLASS = //
  FEATURES.addField("class", DBType.INTEGER); //$NON-NLS-1$

  public static final IDBField FEATURES_FEATURE = //
  FEATURES.addField("feature", DBType.INTEGER); //$NON-NLS-1$

  public static final IDBField FEATURES_NAME = //
  FEATURES.addField("name", DBType.VARCHAR, 255); //$NON-NLS-1$

  public static final IDBField FEATURES_TYPE = //
  FEATURES.addField("type", DBType.INTEGER); //$NON-NLS-1$

  public static final IDBField FEATURES_REFERENCE_PACKAGE = //
  FEATURES.addField("reference_package", DBType.VARCHAR, 255); //$NON-NLS-1$

  public static final IDBField FEATURES_REFERENCE_CLASSIFIER = //
  FEATURES.addField("reference_classifier", DBType.INTEGER); //$NON-NLS-1$

  public static final IDBField FEATURES_MANY = //
  FEATURES.addField("many", DBType.BOOLEAN); //$NON-NLS-1$

  public static final IDBField FEATURES_CONTAINMENT = //
  FEATURES.addField("containment", DBType.BOOLEAN); //$NON-NLS-1$

  public static final IDBField FEATURES_INDEX = //
  FEATURES.addField("idx", DBType.INTEGER); //$NON-NLS-1$

  public static final IDBIndex INDEX_FEATURES_PK = //
  FEATURES.addIndex(IDBIndex.Type.PRIMARY_KEY, FEATURES_ID);

  /**
   * Name of object table
   */
  public static final String CDO_OBJECTS = "cdo_objects"; //$NON-NLS-1$

  /**
   * Field names of attribute tables
   */
  public static final String ATTRIBUTES_ID = "cdo_id"; //$NON-NLS-1$

  public static final String ATTRIBUTES_VERSION = "cdo_version"; //$NON-NLS-1$

  public static final String ATTRIBUTES_CLASS = "cdo_class"; //$NON-NLS-1$

  public static final String ATTRIBUTES_CREATED = "cdo_created"; //$NON-NLS-1$

  public static final String ATTRIBUTES_REVISED = "cdo_revised"; //$NON-NLS-1$

  public static final String ATTRIBUTES_RESOURCE = "cdo_resource"; //$NON-NLS-1$

  public static final String ATTRIBUTES_CONTAINER = "cdo_container"; //$NON-NLS-1$

  public static final String ATTRIBUTES_FEATURE = "cdo_feature"; //$NON-NLS-1$

  /**
   * Field names of reference tables
   */
  public static final String REFERENCES_FEATURE = "cdo_feature"; //$NON-NLS-1$

  public static final String REFERENCES_SOURCE = "cdo_source"; //$NON-NLS-1$

  public static final String REFERENCES_VERSION = "cdo_version"; //$NON-NLS-1$

  public static final String REFERENCES_IDX = "cdo_idx"; //$NON-NLS-1$

  public static final String REFERENCES_TARGET = "cdo_target"; //$NON-NLS-1$

  private TestSchema()
  {
    super("CDO"); //$NON-NLS-1$
  }

  static
  {
    INSTANCE.lock();
  }
}
