/*
 * Copyright (c) 2020, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.db.mapping;

import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBTable;

import org.eclipse.emf.ecore.EStructuralFeature;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Eike Stepper
 * @since 4.10
 */
public abstract class DelegatingTypeMapping implements ITypeMapping, ILobRefsUpdater
{
  public abstract AbstractTypeMapping getDelegate();

  @Override
  public EStructuralFeature getFeature()
  {
    return getDelegate().getFeature();
  }

  @Override
  public IDBField getField()
  {
    return getDelegate().getField();
  }

  @Override
  public DBType getDBType()
  {
    return getDelegate().getDBType();
  }

  @Override
  public void setMappingStrategy(IMappingStrategy mappingStrategy)
  {
    getDelegate().setMappingStrategy(mappingStrategy);
  }

  @Override
  public void setFeature(EStructuralFeature feature)
  {
    getDelegate().setFeature(feature);
  }

  @Override
  public void setDBType(DBType dbType)
  {
    getDelegate().setDBType(dbType);
  }

  @Override
  @SuppressWarnings("deprecation")
  public void createDBField(IDBTable table)
  {
    getDelegate().createDBField(table);
  }

  @Override
  public void createDBField(IDBTable table, String fieldName)
  {
    getDelegate().createDBField(table, fieldName);
  }

  @Override
  public void setDBField(IDBTable table, String fieldName)
  {
    getDelegate().setDBField(table, fieldName);
  }

  @Override
  public void setValue(PreparedStatement stmt, int index, Object value) throws SQLException
  {
    Object encoded = encode(value);
    getDelegate().setValue(stmt, index, encoded);
  }

  @Override
  public void setDefaultValue(PreparedStatement stmt, int index) throws SQLException
  {
    Object value = getDelegate().getDefaultValue();
    setValue(stmt, index, value);
  }

  @Override
  public void setValueFromRevision(PreparedStatement stmt, int index, InternalCDORevision revision) throws SQLException
  {
    Object value = getDelegate().getRevisionValue(revision);
    setValue(stmt, index, value);
  }

  @Override
  public Object readValue(ResultSet resultSet) throws SQLException
  {
    Object value = getDelegate().readValue(resultSet);
    return decode(value);
  }

  @Override
  public void readValueToRevision(ResultSet resultSet, InternalCDORevision revision) throws SQLException
  {
    Object decoded = readValue(resultSet);
    revision.setValue(getFeature(), decoded);
  }

  @Override
  public void updateLobRefs(Connection connection)
  {
    AbstractTypeMapping delegate = getDelegate();
    if (delegate instanceof ILobRefsUpdater)
    {
      ((ILobRefsUpdater)delegate).updateLobRefs(connection);
    }
    else
    {
      throw new LobRefsUpdateNotSupportedException();
    }
  }

  protected Object encode(Object value)
  {
    return value;
  }

  protected Object decode(Object value)
  {
    return value;
  }
}
