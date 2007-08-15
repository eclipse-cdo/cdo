/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.net4j.db.IDBField;
import org.eclipse.net4j.db.IDBIndex;
import org.eclipse.net4j.db.IDBTable;
import org.eclipse.net4j.internal.db.DBSchema;

/**
 * @author Eike Stepper
 */
public class CDODBSchema extends DBSchema
{
  public static final CDODBSchema INSTANCE = new CDODBSchema();

  /**
   * DBTable cdo_packages
   */
  public static final IDBTable PACKAGES = INSTANCE.addTable("cdo_packages");

  public static final IDBField PACKAGES_ID = //
  PACKAGES.addField("id", IDBField.Type.INTEGER);

  public static final IDBField PACKAGES_URI = //
  PACKAGES.addField("uri", IDBField.Type.VARCHAR, 255);

  public static final IDBField PACKAGES_NAME = //
  PACKAGES.addField("name", IDBField.Type.VARCHAR, 255);

  public static final IDBField PACKAGES_ECORE = //
  PACKAGES.addField("ecore", IDBField.Type.LONGVARCHAR);

  public static final IDBField PACKAGES_DYNAMIC = //
  PACKAGES.addField("dynamic", IDBField.Type.BOOLEAN);

  public static final IDBField PACKAGES_RANGE_LB = //
  PACKAGES.addField("rangelb", IDBField.Type.BIGINT);

  public static final IDBField PACKAGES_RANGE_UB = //
  PACKAGES.addField("rangeub", IDBField.Type.BIGINT);

  public static final IDBIndex INDEX_PACKAGES_PK = //
  PACKAGES.addIndex(IDBIndex.Type.PRIMARY_KEY, PACKAGES_ID);

  public static final IDBIndex INDEX_PACKAGES_URI = //
  PACKAGES.addIndex(IDBIndex.Type.UNIQUE, PACKAGES_URI);

  /**
   * DBTable cdo_classes
   */
  public static final IDBTable CLASSES = INSTANCE.addTable("cdo_classes");

  public static final IDBField CLASSES_ID = //
  CLASSES.addField("id", IDBField.Type.INTEGER);

  public static final IDBField CLASSES_PACKAGE = //
  CLASSES.addField("package", IDBField.Type.VARCHAR, 255);

  public static final IDBField CLASSES_CLASSIFIER = //
  CLASSES.addField("classifier", IDBField.Type.INTEGER);

  public static final IDBField CLASSES_NAME = //
  CLASSES.addField("name", IDBField.Type.VARCHAR, 255);

  public static final IDBField CLASSES_ABSTRACT = //
  CLASSES.addField("abstract", IDBField.Type.BOOLEAN);

  public static final IDBIndex INDEX_CLASSES_PK = //
  CLASSES.addIndex(IDBIndex.Type.PRIMARY_KEY, CLASSES_ID);

  /**
   * DBTable cdo_supertypes
   */
  public static final IDBTable SUPERTYPES = INSTANCE.addTable("cdo_supertypes");

  public static final IDBField SUPERTYPES_TYPE = //
  SUPERTYPES.addField("type_id", IDBField.Type.INTEGER);

  public static final IDBField SUPERTYPES_SUPERTYPE_PACKAGE = //
  SUPERTYPES.addField("supertype_package", IDBField.Type.VARCHAR, 255);

  public static final IDBField SUPERTYPES_SUPERTYPE_CLASSIFIER = //
  SUPERTYPES.addField("supertype_classifier", IDBField.Type.INTEGER);

  public static final IDBIndex INDEX_SUPERTYPES_PK = //
  SUPERTYPES.addIndex(IDBIndex.Type.PRIMARY_KEY, SUPERTYPES_TYPE);

  /**
   * DBTable cdo_features
   */
  public static final IDBTable FEATURES = INSTANCE.addTable("cdo_features");

  public static final IDBField FEATURES_ID = //
  FEATURES.addField("id", IDBField.Type.INTEGER);

  public static final IDBField FEATURES_NAME = //
  FEATURES.addField("name", IDBField.Type.VARCHAR, 255);

  public static final IDBField FEATURES_TYPE = //
  FEATURES.addField("type", IDBField.Type.INTEGER);

  public static final IDBField FEATURES_REFERENCE_PACKAGE = //
  FEATURES.addField("reference_package", IDBField.Type.VARCHAR, 255);

  public static final IDBField FEATURES_REFERENCE_CLASSIFIER = //
  FEATURES.addField("reference_classifier", IDBField.Type.INTEGER);

  public static final IDBField FEATURES_MANY = //
  FEATURES.addField("many", IDBField.Type.BOOLEAN);

  public static final IDBField FEATURES_CONTAINMENT = //
  FEATURES.addField("containment", IDBField.Type.BOOLEAN);

  public static final IDBField FEATURES_INDEX = //
  FEATURES.addField("idx", IDBField.Type.INTEGER);

  public static final IDBIndex INDEX_FEATURES_PK = //
  FEATURES.addIndex(IDBIndex.Type.PRIMARY_KEY, FEATURES_ID);

  private CDODBSchema()
  {
    super("CDO");
    lock();
  }
}
