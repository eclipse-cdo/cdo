/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - major refactoring
 *    Christopher Albert - 254455: [DB] Support FeatureMaps bug 254455  
 */
package org.eclipse.emf.cdo.server.db.mapping;

import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBTable;

import org.eclipse.emf.ecore.EStructuralFeature;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Mapping of single values to and from the database.
 * 
 * @author Eike Stepper
 * @author Stefan Winkler
 * @since 2.0
 */
public interface ITypeMapping
{
  /**
   * @return The feature which is associated with this mapping.
   */
  public EStructuralFeature getFeature();

  /**
   * @return The db field which is associated with this mapping.
   */
  public IDBField getField();

  /**
   * @return The db type which is associated with this mapping.
   * @since 3.0
   */
  public DBType getDBType();

  /**
   * Creates the DBField and adds it to the given table. The name of the DBField is derived from the feature.
   * 
   * @param table
   *          the table to add this field to.
   */
  public void createDBField(IDBTable table);

  /**
   * Creates the DBField and adds it to the given table. The name of the DBField is explicitly determined by the
   * corresponding parameter.
   * 
   * @param table
   *          the table to add this field to.
   * @param fieldName
   *          the name for the DBField.
   */
  public void createDBField(IDBTable table, String fieldName);

  /**
   * Sets the DBField. The name of the DBField is explicitly determined by the corresponding parameter.
   * 
   * @param table
   *          the table to add this field to.
   * @param fieldName
   *          the name for the DBField.
   * @since 3.0
   */
  public void setDBField(IDBTable table, String fieldName);

  /**
   * Set the given value to the JDBC {@link PreparedStatement} using an appropriate <code>setXxx</code> method.
   * 
   * @param stmt
   *          the prepared statement to set the value
   * @param index
   *          the index to use for the <code>setXxx</code> method.
   * @param value
   *          the value to set.
   * @throws SQLException
   *           if the <code>setXxx</code> throws it.
   */
  public void setValue(PreparedStatement stmt, int index, Object value) throws SQLException;

  /**
   * Set a value of the given revision to the JDBC {@link PreparedStatement} using an appropriate <code>setXxx</code>
   * method. The feature from which the value is taken is determined by {@link #getFeature()}.
   * 
   * @param stmt
   *          the prepared statement to set the value
   * @param index
   *          the index to use for the <code>setXxx</code> method.
   * @param revision
   *          the revision to get the value to set from.
   * @throws SQLException
   *           if the <code>setXxx</code> throws it.
   */
  public void setValueFromRevision(PreparedStatement stmt, int index, InternalCDORevision value) throws SQLException;

  /**
   * Read the value from a {@link ResultSet} and convert it from the DB to the CDO representation. The resultSet field
   * to read from is determined automatically by the internal {@link #getField()} name.
   * 
   * @param resultSet
   *          the result set to read from
   * @return the read value
   * @throws SQLException
   *           if reading the value throws an SQLException
   * @since 3.0
   */
  public Object readValue(ResultSet resultSet) throws SQLException;

  /**
   * Read a value from a {@link ResultSet}, convert it from the DB to the CDO representation and set it to the feature
   * of the revision. The feature is determined by getFeature() The resultSet field to read from is determined
   * automatically by the internal {@link #getField()} name.
   * 
   * @param resultSet
   *          the result set to read from
   * @param revision
   *          the revision to which the value should be set.
   * @throws SQLException
   *           if reading the value throws an SQLException
   * @since 3.0
   */
  public void readValueToRevision(ResultSet resultSet, InternalCDORevision revision) throws SQLException;
}
