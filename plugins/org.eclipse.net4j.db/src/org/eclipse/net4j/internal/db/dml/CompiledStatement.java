/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.db.dml;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnection;
import org.eclipse.net4j.db.IDBPreparedStatement;
import org.eclipse.net4j.db.IDBPreparedStatement.ReuseProbability;
import org.eclipse.net4j.db.dml.SQLParameter;
import org.eclipse.net4j.db.dml.SQLParameterBinder;
import org.eclipse.net4j.db.dml.SQLStatement;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Immutable adapter-compiled SQL text and the logical parameter identity at every placeholder.
 *
 * @author Eike Stepper
 * @since 4.15
 */
public final class CompiledStatement implements SQLStatement
{
  private final String sql;

  private final List<ParameterOccurrence> occurrences;

  private final IDBAdapter adapter;

  /**
   * Creates an immutable statement snapshot from rendered SQL and its parameters in placeholder order.
   *
   * @param sql the complete adapter-specific SQL text
   * @param occurrences the logical parameter metadata for each JDBC placeholder
   * @param adapter the adapter used during compilation, retained for occurrence-specific NULL type inference
   */
  public CompiledStatement(String sql, List<ParameterOccurrence> occurrences, IDBAdapter adapter)
  {
    this.sql = Objects.requireNonNull(sql, "sql");
    this.occurrences = Collections.unmodifiableList(new ArrayList<>(Objects.requireNonNull(occurrences, "occurrences")));
    this.adapter = Objects.requireNonNull(adapter, "adapter");
  }

  @Override
  public String getSQL()
  {
    return sql;
  }

  @Override
  public int getParameterCount()
  {
    return occurrences.size();
  }

  /**
   * Returns the logical parameter identity at each placeholder, in generated SQL order.
   *
   * @return an immutable occurrence list; repeated entries preserve repeated uses of one identity
   */
  public List<SQLParameter> getParameterOccurrences()
  {
    List<SQLParameter> parameters = new ArrayList<>(occurrences.size());

    for (ParameterOccurrence occurrence : occurrences)
    {
      parameters.add(occurrence.getParameter());
    }

    return Collections.unmodifiableList(parameters);
  }

  @Override
  public IDBPreparedStatement prepare(IDBConnection connection, ReuseProbability reuseProbability) throws SQLException
  {
    Objects.requireNonNull(connection, "connection");
    Objects.requireNonNull(reuseProbability, "reuseProbability");
    return connection.prepareStatement(sql, reuseProbability);
  }

  @Override
  public void bind(PreparedStatement stmt, Object... values) throws SQLException
  {
    Objects.requireNonNull(stmt, "stmt");
    Objects.requireNonNull(values, "values");

    if (values.length != occurrences.size())
    {
      throw new IllegalArgumentException("Expected " + occurrences.size() + " parameter values but received " + values.length);
    }

    for (int i = 0; i < values.length; i++)
    {
      bindOccurrence(stmt, i + 1, occurrences.get(i), values[i], null);
    }
  }

  @Override
  public void bindParameter(PreparedStatement stmt, int index, Object value) throws SQLException
  {
    validateIndex(stmt, index);
    bindOccurrence(stmt, index, occurrences.get(index - 1), value, null);
  }

  @Override
  public <JAVA> void bindParameter(PreparedStatement stmt, int index, JAVA value, SQLParameterBinder<? super JAVA> binder) throws SQLException
  {
    validateIndex(stmt, index);
    bindOccurrence(stmt, index, occurrences.get(index - 1), value, Objects.requireNonNull(binder, "binder"));
  }

  @Override
  public void bindParameter(PreparedStatement stmt, SQLParameter parameter, Object value) throws SQLException
  {
    Objects.requireNonNull(stmt, "stmt");
    Objects.requireNonNull(parameter, "parameter");
    boolean found = false;

    for (ParameterOccurrence occurrence : occurrences)
    {
      if (occurrence.getParameter() == parameter)
      {
        found = true;
      }
    }

    if (!found)
    {
      throw new IllegalArgumentException("Parameter identity is not present in this compiled statement");
    }

    for (int i = 0; i < occurrences.size(); i++)
    {
      ParameterOccurrence occurrence = occurrences.get(i);
      if (occurrence.getParameter() == parameter)
      {
        bindOccurrence(stmt, i + 1, occurrence, value, null);
      }
    }
  }

  @Override
  public <JAVA> void bindParameter(PreparedStatement stmt, SQLParameter parameter, JAVA value, SQLParameterBinder<? super JAVA> binder) throws SQLException
  {
    Objects.requireNonNull(stmt, "stmt");
    Objects.requireNonNull(parameter, "parameter");
    Objects.requireNonNull(binder, "binder");
    boolean found = false;

    for (ParameterOccurrence occurrence : occurrences)
    {
      found |= occurrence.getParameter() == parameter;
    }

    if (!found)
    {
      throw new IllegalArgumentException("Parameter identity is not present in this compiled statement");
    }

    for (int i = 0; i < occurrences.size(); i++)
    {
      ParameterOccurrence occurrence = occurrences.get(i);
      if (occurrence.getParameter() == parameter)
      {
        binder.bind(stmt, i + 1, value);
      }
    }
  }

  @Override
  public String toString()
  {
    return sql;
  }

  private <JAVA> void bindOccurrence(PreparedStatement stmt, int index, ParameterOccurrence occurrence, JAVA value, SQLParameterBinder<? super JAVA> binder)
      throws SQLException
  {
    SQLParameter parameter = occurrence.getParameter();
    Parameter internal = parameter instanceof Parameter ? (Parameter)parameter : null;

    if (binder != null)
    {
      binder.bind(stmt, index, value);
      return;
    }

    Object converted = value;

    if (value != null && internal != null && internal.getConverter() != null)
    {
      @SuppressWarnings("unchecked")
      Function<Object, Object> converter = (Function<Object, Object>)internal.getConverter();
      converted = converter.apply(value);
    }

    Integer explicitType = internal == null ? null : internal.getSQLType();

    if (converted == null)
    {
      int jdbcType;

      if (explicitType != null)
      {
        jdbcType = explicitType.intValue();
      }
      else
      {
        DBType inferredType = occurrence.getInferredType();
        if (inferredType == null)
        {
          throw new IllegalArgumentException("Cannot bind NULL at JDBC parameter " + index + ": specify SQL.param(sqlType) or use a specialized binder");
        }

        jdbcType = adapter.getJDBCTypeForNull(inferredType);
      }

      stmt.setNull(index, jdbcType);
    }
    else if (explicitType != null)
    {
      stmt.setObject(index, converted, explicitType.intValue());
    }
    else
    {
      stmt.setObject(index, converted);
    }
  }

  private void validateIndex(PreparedStatement stmt, int index)
  {
    Objects.requireNonNull(stmt, "stmt");

    if (index < 1 || index > occurrences.size())
    {
      throw new IllegalArgumentException("JDBC parameter index " + index + " is outside 1.." + occurrences.size());
    }
  }
}
