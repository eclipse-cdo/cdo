/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - https://bugs.eclipse.org/bugs/show_bug.cgi?id=259402
 */
package org.eclipse.emf.cdo.server.internal.db.mapping;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.server.db.mapping.IAttributeMapping;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.ddl.IDBField;

import org.eclipse.emf.ecore.EStructuralFeature;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Eike Stepper
 */
public abstract class AttributeMapping extends FeatureMapping implements IAttributeMapping
{
  private IDBField field;

  public AttributeMapping(ClassMapping classMapping, EStructuralFeature feature)
  {
    super(classMapping, feature);
    field = classMapping.addField(feature, classMapping.getTable());
  }

  public IDBField getField()
  {
    return field;
  }

  public void appendValue(StringBuilder builder, InternalCDORevision revision)
  {
    IDBAdapter dbAdapter = getDBAdapter();
    Object value = getRevisionValue(revision);
    dbAdapter.appendValue(builder, field, value);
  }

  public void appendValue(StringBuilder builder, Object value)
  {
    IDBAdapter dbAdapter = getDBAdapter();
    Object dbValue = convertToDBType(value);
    dbAdapter.appendValue(builder, field, dbValue);
  }

  public Object getRevisionValue(InternalCDORevision revision)
  {
    EStructuralFeature feature = getFeature();
    return ((InternalCDORevision)revision).getValue(feature);
  }

  public void extractValue(ResultSet resultSet, int column, InternalCDORevision revision)
  {
    try
    {
      Object value = getResultSetValue(resultSet, column);
      if (resultSet.wasNull())
      {
        value = null;
      }

      ((InternalCDORevision)revision).setValue(getFeature(), value);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  protected abstract Object getResultSetValue(ResultSet resultSet, int column) throws SQLException;

  /**
   * Convert a value to a DB compatible format. Needs to be overridden by those AttributeMappings for which
   * <code>value.toString()</code> is not sufficient (e.g. for CDOID).
   */
  protected Object convertToDBType(Object value)
  {
    return value;
  }

  /**
   * @author Eike Stepper
   */
  public static class AMString extends AttributeMapping
  {
    public AMString(ClassMapping classMapping, EStructuralFeature feature)
    {
      super(classMapping, feature);
    }

    @Override
    protected Object getResultSetValue(ResultSet resultSet, int column) throws SQLException
    {
      return resultSet.getString(column);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class AMShort extends AttributeMapping
  {
    public AMShort(ClassMapping classMapping, EStructuralFeature feature)
    {
      super(classMapping, feature);
    }

    @Override
    protected Object getResultSetValue(ResultSet resultSet, int column) throws SQLException
    {
      return resultSet.getShort(column);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class AMObject extends AttributeMapping
  {
    public AMObject(ClassMapping classMapping, EStructuralFeature feature)
    {
      super(classMapping, feature);
    }

    @Override
    protected Object getResultSetValue(ResultSet resultSet, int column) throws SQLException
    {
      long id = resultSet.getLong(column);
      if (resultSet.wasNull())
      {
        return null;
      }

      return CDOIDUtil.createLong(id);
    }

    @Override
    protected Object convertToDBType(Object value)
    {
      return CDOIDUtil.getLong((CDOID)value);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class AMLong extends AttributeMapping
  {
    public AMLong(ClassMapping classMapping, EStructuralFeature feature)
    {
      super(classMapping, feature);
    }

    @Override
    protected Object getResultSetValue(ResultSet resultSet, int column) throws SQLException
    {
      return resultSet.getLong(column);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class AMInteger extends AttributeMapping
  {
    public AMInteger(ClassMapping classMapping, EStructuralFeature feature)
    {
      super(classMapping, feature);
    }

    @Override
    protected Object getResultSetValue(ResultSet resultSet, int column) throws SQLException
    {
      return resultSet.getInt(column);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class AMFloat extends AttributeMapping
  {
    public AMFloat(ClassMapping classMapping, EStructuralFeature feature)
    {
      super(classMapping, feature);
    }

    @Override
    protected Object getResultSetValue(ResultSet resultSet, int column) throws SQLException
    {
      return resultSet.getFloat(column);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class AMDouble extends AttributeMapping
  {
    public AMDouble(ClassMapping classMapping, EStructuralFeature feature)
    {
      super(classMapping, feature);
    }

    @Override
    protected Object getResultSetValue(ResultSet resultSet, int column) throws SQLException
    {
      return resultSet.getDouble(column);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class AMDate extends AttributeMapping
  {
    public AMDate(ClassMapping classMapping, EStructuralFeature feature)
    {
      super(classMapping, feature);
    }

    @Override
    protected Object getResultSetValue(ResultSet resultSet, int column) throws SQLException
    {
      return resultSet.getTimestamp(column);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class AMCharacter extends AttributeMapping
  {
    public AMCharacter(ClassMapping classMapping, EStructuralFeature feature)
    {
      super(classMapping, feature);
    }

    @Override
    protected Object getResultSetValue(ResultSet resultSet, int column) throws SQLException
    {
      String str = resultSet.getString(column);
      if (resultSet.wasNull())
      {
        return null;
      }

      return str.charAt(0);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class AMByte extends AttributeMapping
  {
    public AMByte(ClassMapping classMapping, EStructuralFeature feature)
    {
      super(classMapping, feature);
    }

    @Override
    protected Object getResultSetValue(ResultSet resultSet, int column) throws SQLException
    {
      return resultSet.getByte(column);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class AMBoolean extends AttributeMapping
  {
    public AMBoolean(ClassMapping classMapping, EStructuralFeature feature)
    {
      super(classMapping, feature);
    }

    @Override
    protected Object getResultSetValue(ResultSet resultSet, int column) throws SQLException
    {
      return resultSet.getBoolean(column);
    }
  }
}
