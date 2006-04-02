/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Fuggerstr. 39, 10777 Berlin, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.impl;


public interface SQLConstants
{
  //--------------------------------------------------------------------
  public static final String SYSTEM_TABLE = "CDO_SYSTEM";

  public static final String SYSTEM_SID_COLUMN = "SID";

  public static final String SYSTEM_STARTED_COLUMN = "STARTED";

  //--------------------------------------------------------------------
  public static final String PACKAGE_TABLE = "CDO_PACKAGE";

  public static final String PACKAGE_PID_COLUMN = "PID";

  public static final String PACKAGE_NAME_COLUMN = "NAME";

  //--------------------------------------------------------------------
  public static final String CLASS_TABLE = "CDO_CLASS";

  public static final String CLASS_CID_COLUMN = "CID";

  public static final String CLASS_NAME_COLUMN = "NAME";

  public static final String CLASS_PARENTNAME_COLUMN = "PARENT" + CLASS_NAME_COLUMN;

  public static final String CLASS_TABLENAME_COLUMN = "TABLENAME";

  public static final String CLASS_PID_COLUMN = PACKAGE_PID_COLUMN;

  //--------------------------------------------------------------------
  public static final String ATTRIBUTE_TABLE = "CDO_ATTRIBUTE";

  public static final String ATTRIBUTE_NAME_COLUMN = "NAME";

  public static final String ATTRIBUTE_FEATUREID_COLUMN = "FEATUREID";

  public static final String ATTRIBUTE_DATATYPE_COLUMN = "DATATYPE";

  public static final String ATTRIBUTE_COLUMNNAME_COLUMN = "COLUMNNAME";

  public static final String ATTRIBUTE_COLUMNTYPE_COLUMN = "COLUMNTYPE";

  public static final String ATTRIBUTE_CID_COLUMN = CLASS_CID_COLUMN;

  //--------------------------------------------------------------------
  public static final String OBJECT_TABLE = "CDO_OBJECT";

  public static final String OBJECT_OID_COLUMN = "OID";

  public static final String OBJECT_OCA_COLUMN = "OCA";

  public static final String OBJECT_CID_COLUMN = CLASS_CID_COLUMN;

  //--------------------------------------------------------------------
  public static final String RESOURCE_TABLE = "CDO_RESOURCE";

  public static final String RESOURCE_RID_COLUMN = "RID";

  public static final String RESOURCE_PATH_COLUMN = "PATH";

  //--------------------------------------------------------------------
  public static final String CONTENT_TABLE = "CDO_CONTENT";

  public static final String CONTENT_OID_COLUMN = OBJECT_OID_COLUMN;

  //--------------------------------------------------------------------
  public static final String REFERENCE_TABLE = "CDO_REFERENCE";

  public static final String REFERENCE_OID_COLUMN = "SOURCE" + OBJECT_OID_COLUMN;

  public static final String REFERENCE_FEATUREID_COLUMN = "FEATUREID";

  public static final String REFERENCE_CONTENT_COLUMN = "CONTENT";

  public static final String REFERENCE_ORDINAL_COLUMN = "ORDINAL";

  public static final String REFERENCE_TARGET_COLUMN = "TARGET" + OBJECT_OID_COLUMN;

  //--------------------------------------------------------------------
  public static final String SELECT_PACKAGES = "SELECT " + PACKAGE_PID_COLUMN + ", "
      + PACKAGE_NAME_COLUMN + " FROM " + PACKAGE_TABLE;

  public static final String SELECT_CLASSES = "SELECT " + CLASS_CID_COLUMN + ", "
      + CLASS_NAME_COLUMN + ", " + CLASS_PARENTNAME_COLUMN + ", " + CLASS_TABLENAME_COLUMN
      + " FROM " + CLASS_TABLE + " WHERE " + CLASS_PID_COLUMN + "=?";

  public static final String SELECT_ATTRIBUTES = "SELECT " + ATTRIBUTE_NAME_COLUMN + ", "
      + ATTRIBUTE_FEATUREID_COLUMN + ", " + ATTRIBUTE_DATATYPE_COLUMN + ", "
      + ATTRIBUTE_COLUMNNAME_COLUMN + ", " + ATTRIBUTE_COLUMNTYPE_COLUMN + " FROM "
      + ATTRIBUTE_TABLE + " WHERE " + ATTRIBUTE_CID_COLUMN + "=?";

  public static final String INSERT_PACKAGE = "INSERT INTO " + PACKAGE_TABLE + " VALUES (?, ?)";

  public static final String INSERT_CLASS = "INSERT INTO " + CLASS_TABLE
      + " VALUES (?, ?, ?, ?, ?)";

  public static final String INSERT_ATTRIBUTE = "INSERT INTO " + ATTRIBUTE_TABLE
      + " VALUES (?, ?, ?, ?, ?, ?)";

  public static final String SELECT_MAX_PID = "SELECT MAX(" + PACKAGE_PID_COLUMN + ") FROM "
      + PACKAGE_TABLE;

  public static final String SELECT_MAX_CID = "SELECT MAX(" + CLASS_CID_COLUMN + ") FROM "
      + CLASS_TABLE;

  public static final String SELECT_MAX_RID = "SELECT MAX(" + RESOURCE_RID_COLUMN + ") FROM "
      + RESOURCE_TABLE;

  public static final String SELECT_MAX_OID_FRAGMENT = "SELECT MAX(" + OBJECT_OID_COLUMN
      + ") FROM " + OBJECT_TABLE + " WHERE " + OBJECT_OID_COLUMN + " BETWEEN ? AND ?";

  public static final String SELECT_CID_OF_OBJECT = "SELECT " + OBJECT_CID_COLUMN + " FROM "
      + OBJECT_TABLE + " WHERE " + OBJECT_OID_COLUMN + "=?";

  public static final String SELECT_CONTAINER_OF_OBJECT = "SELECT " + REFERENCE_TABLE + "."
      + REFERENCE_OID_COLUMN + ", " + OBJECT_TABLE + "." + OBJECT_CID_COLUMN + " FROM "
      + REFERENCE_TABLE + ", " + OBJECT_TABLE + " WHERE " + REFERENCE_TABLE + "."
      + REFERENCE_TARGET_COLUMN + "=? AND " + REFERENCE_TABLE + "." + REFERENCE_CONTENT_COLUMN
      + "=? AND " + REFERENCE_TABLE + "." + REFERENCE_OID_COLUMN + "=" + OBJECT_TABLE + "."
      + OBJECT_OID_COLUMN;

  public static final String SELECT_ALL_RESOURCES = "SELECT " + RESOURCE_RID_COLUMN + ", "
      + RESOURCE_PATH_COLUMN + " FROM " + RESOURCE_TABLE;

  public static final String SELECT_RID_OF_RESOURCE = "SELECT " + RESOURCE_RID_COLUMN + " FROM "
      + RESOURCE_TABLE + " WHERE " + RESOURCE_PATH_COLUMN + "=?";

  public static final String SELECT_PATH_OF_RESOURCE = "SELECT " + RESOURCE_PATH_COLUMN + " FROM "
      + RESOURCE_TABLE + " WHERE " + RESOURCE_RID_COLUMN + "=?";

  public static final String SELECT_COLLECTION_COUNT = "SELECT COUNT(" + REFERENCE_OID_COLUMN
      + ") FROM " + REFERENCE_TABLE + " WHERE " + REFERENCE_OID_COLUMN + "=? AND "
      + REFERENCE_FEATUREID_COLUMN + "=?";

  public static final String INSERT_RESOURCE = "INSERT INTO " + RESOURCE_TABLE + " VALUES (?, ?)";

  public static final String INSERT_REFERENCE = "INSERT INTO " + REFERENCE_TABLE
      + " VALUES (?, ?, ?, ?, ?)";

  public static final String REMOVE_REFERENCES = "DELETE FROM " + REFERENCE_TABLE + " WHERE "
      + REFERENCE_OID_COLUMN + "=?";

  public static final String REMOVE_REFERENCE = "DELETE FROM " + REFERENCE_TABLE + " WHERE "
      + REFERENCE_OID_COLUMN + "=? AND " + REFERENCE_FEATUREID_COLUMN + "=? AND "
      + REFERENCE_ORDINAL_COLUMN + "=?";

  public static final String MOVE_REFERENCE_ABSOLUTE = "UPDATE " + REFERENCE_TABLE + " SET "
      + REFERENCE_ORDINAL_COLUMN + "=? WHERE " + REFERENCE_OID_COLUMN + "=? AND "
      + REFERENCE_FEATUREID_COLUMN + "=? AND " + REFERENCE_ORDINAL_COLUMN + "=?";

  public static final String MOVE_REFERENCES_RELATIVE = "UPDATE " + REFERENCE_TABLE + " SET "
      + REFERENCE_ORDINAL_COLUMN + "=" + REFERENCE_ORDINAL_COLUMN + "+? WHERE "
      + REFERENCE_OID_COLUMN + "=? AND " + REFERENCE_FEATUREID_COLUMN + "=? AND "
      + REFERENCE_ORDINAL_COLUMN + " BETWEEN ? AND ?";

  public static final String TRANSMIT_REFERENCES = "SELECT "
      + (REFERENCE_TABLE + "." + REFERENCE_FEATUREID_COLUMN) + ", "
      + (REFERENCE_TABLE + "." + REFERENCE_TARGET_COLUMN) + ", "
      + (OBJECT_TABLE + "." + OBJECT_CID_COLUMN) + " FROM " + REFERENCE_TABLE + ", " + OBJECT_TABLE
      + " WHERE " + (REFERENCE_TABLE + "." + REFERENCE_OID_COLUMN) + "=? AND "
      + (REFERENCE_TABLE + "." + REFERENCE_TARGET_COLUMN) + "="
      + (OBJECT_TABLE + "." + OBJECT_OID_COLUMN) + " ORDER BY "
      + (REFERENCE_TABLE + "." + REFERENCE_FEATUREID_COLUMN) + ", "
      + (REFERENCE_TABLE + "." + REFERENCE_ORDINAL_COLUMN);

  public static final String INSERT_OBJECT = "INSERT INTO " + OBJECT_TABLE + " VALUES (?, 1, ?)";

  public static final String INSERT_CONTENT = "INSERT INTO " + CONTENT_TABLE + " VALUES (?)";

  public static final String REMOVE_CONTENT = "DELETE FROM " + CONTENT_TABLE + " WHERE "
      + CONTENT_OID_COLUMN + "=?";

  public static final String TRANSMIT_CONTENT = "SELECT "
      + (OBJECT_TABLE + "." + OBJECT_OID_COLUMN) + ", " + (OBJECT_TABLE + "." + OBJECT_OCA_COLUMN)
      + ", " + (OBJECT_TABLE + "." + OBJECT_CID_COLUMN) + " FROM " + CONTENT_TABLE + ", "
      + OBJECT_TABLE + " WHERE " + (OBJECT_TABLE + "." + OBJECT_OID_COLUMN) + "="
      + (CONTENT_TABLE + "." + CONTENT_OID_COLUMN) + " AND "
      + (OBJECT_TABLE + "." + OBJECT_OID_COLUMN) + " BETWEEN ? AND ?";

  public static final String TRANSMIT_OBJECT = "SELECT " + OBJECT_OCA_COLUMN + ", "
      + OBJECT_CID_COLUMN + " FROM " + OBJECT_TABLE + " WHERE " + OBJECT_OID_COLUMN + "=?";

  public static final String DO_OPTIMISTIC_CONTROL = "UPDATE " + OBJECT_TABLE + " SET "
      + OBJECT_OCA_COLUMN + "=" + OBJECT_OCA_COLUMN + "+1 WHERE " + OBJECT_OID_COLUMN + "=?"
      + " AND " + OBJECT_OCA_COLUMN + "=?";
}
