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
import org.eclipse.net4j.db.dml.SQLDefaultSetter;
import org.eclipse.net4j.db.dml.SQLParameter;
import org.eclipse.net4j.db.dml.SQLParameterBinder;
import org.eclipse.net4j.db.dml.SQLStatement;
import org.eclipse.net4j.db.dml.SQLValueSetter;

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

  private final List<SQLParameter> parameters;

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

    List<SQLParameter> parameterList = new ArrayList<>(this.occurrences.size());

    for (ParameterOccurrence occurrence : this.occurrences)
    {
      parameterList.add(occurrence.getParameter());
    }

    parameters = Collections.unmodifiableList(parameterList);
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

  @Override
  public SQLParameter getParameter(int index)
  {
    if (index < 1 || index > occurrences.size())
    {
      throw new IllegalArgumentException("JDBC parameter index " + index + " is outside 1.." + occurrences.size());
    }

    return occurrences.get(index - 1).getParameter();
  }

  @Override
  public List<SQLParameter> getParameters()
  {
    return getParameterOccurrences();
  }

  /**
   * Returns the logical parameter identity at each placeholder, in generated SQL order.
   *
   * @return an immutable occurrence list; repeated entries preserve repeated uses of one identity
   */
  public List<SQLParameter> getParameterOccurrences()
  {
    return parameters;
  }

  @Override
  public IDBPreparedStatement prepare(IDBConnection connection, ReuseProbability reuseProbability)
  {
    Objects.requireNonNull(connection, "connection");
    Objects.requireNonNull(reuseProbability, "reuseProbability");
    return connection.prepareStatement(sql, reuseProbability);
  }

  @Override
  public IDBPreparedStatement prepare(IDBConnection connection, int resultSetType, int resultSetConcurrency, ReuseProbability reuseProbability)
  {
    Objects.requireNonNull(connection, "connection");
    Objects.requireNonNull(reuseProbability, "reuseProbability");
    return connection.prepareStatement(sql, resultSetType, resultSetConcurrency, reuseProbability);
  }

  @Override
  public int bind(PreparedStatement stmt, Object... values) throws SQLException
  {
    return bindFrom(stmt, 1, values);
  }

  @Override
  public int bindFrom(PreparedStatement stmt, int startIndex, Object... values) throws SQLException
  {
    Objects.requireNonNull(stmt, "stmt");
    Objects.requireNonNull(values, "values");
    validateRange(startIndex, values.length);

    for (int i = 0; i < values.length; i++)
    {
      bindOccurrence(stmt, startIndex + i, occurrences.get(startIndex + i - 1), values[i], null, null);
    }

    return values.length;
  }

  @Override
  public int bindAll(PreparedStatement stmt, Object... values) throws SQLException
  {
    Objects.requireNonNull(stmt, "stmt");
    Objects.requireNonNull(values, "values");

    if (values.length != occurrences.size())
    {
      throw new IllegalArgumentException("Expected " + occurrences.size() + " parameter values but received " + values.length);
    }

    return bindFrom(stmt, 1, values);
  }

  @Override
  public SQLParameterBinder binder(PreparedStatement stmt)
  {
    return binderFrom(stmt, 1);
  }

  @Override
  public SQLParameterBinder binderFrom(PreparedStatement stmt, int startIndex)
  {
    Objects.requireNonNull(stmt, "stmt");
    validateRange(startIndex, 0);
    return new ParameterBinder(stmt, startIndex);
  }

  @Override
  public void bindParameter(PreparedStatement stmt, int index, Object value) throws SQLException
  {
    validateIndex(stmt, index);
    bindOccurrence(stmt, index, occurrences.get(index - 1), value, null, null);
  }

  @Override
  public void bindDefault(PreparedStatement stmt, int index, SQLDefaultSetter setter) throws SQLException
  {
    validateIndex(stmt, index);
    Objects.requireNonNull(setter, "setter").setDefaultValue(stmt, index);
  }

  @Override
  public <JAVA> void bindParameter(PreparedStatement stmt, int index, JAVA value, SQLValueSetter<? super JAVA> setter) throws SQLException
  {
    validateIndex(stmt, index);
    bindOccurrence(stmt, index, occurrences.get(index - 1), value, Objects.requireNonNull(setter, "setter"), null);
  }

  @Override
  public <JAVA, JDBC> void bindParameter(PreparedStatement stmt, int index, JAVA value, Function<JAVA, JDBC> converter) throws SQLException
  {
    validateIndex(stmt, index);
    bindOccurrence(stmt, index, occurrences.get(index - 1), value, null, Objects.requireNonNull(converter, "converter"));
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
        bindOccurrence(stmt, i + 1, occurrence, value, null, null);
      }
    }
  }

  @Override
  public void bindDefault(PreparedStatement stmt, SQLParameter parameter, SQLDefaultSetter setter) throws SQLException
  {
    Objects.requireNonNull(stmt, "stmt");
    Objects.requireNonNull(parameter, "parameter");
    Objects.requireNonNull(setter, "setter");
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
        setter.setDefaultValue(stmt, i + 1);
      }
    }
  }

  @Override
  public <JAVA> void bindParameter(PreparedStatement stmt, SQLParameter parameter, JAVA value, SQLValueSetter<? super JAVA> setter) throws SQLException
  {
    Objects.requireNonNull(stmt, "stmt");
    Objects.requireNonNull(parameter, "parameter");
    Objects.requireNonNull(setter, "setter");
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
        setter.setValue(stmt, i + 1, value);
      }
    }
  }

  @Override
  public <JAVA, JDBC> void bindParameter(PreparedStatement stmt, SQLParameter parameter, JAVA value, Function<JAVA, JDBC> converter) throws SQLException
  {
    Objects.requireNonNull(stmt, "stmt");
    Objects.requireNonNull(parameter, "parameter");
    Objects.requireNonNull(converter, "converter");
    boolean found = false;

    for (SQLParameter occurrenceParameter : parameters)
    {
      found |= occurrenceParameter == parameter;
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
        bindOccurrence(stmt, i + 1, occurrence, value, null, converter);
      }
    }
  }

  @Override
  public String toString()
  {
    return sql;
  }

  private <JAVA> void bindOccurrence(PreparedStatement stmt, int index, ParameterOccurrence occurrence, JAVA value, SQLValueSetter<? super JAVA> setter,
      Function<JAVA, ?> converterOverride) throws SQLException
  {
    SQLParameter parameter = occurrence.getParameter();
    Parameter internal = parameter instanceof Parameter ? (Parameter)parameter : null;

    if (setter != null)
    {
      setter.setValue(stmt, index, value);
      return;
    }

    Object converted = value;

    if (converterOverride != null)
    {
      if (value != null)
      {
        converted = converterOverride.apply(value);
      }
    }
    else if (internal != null && internal.getSetter() != null)
    {
      @SuppressWarnings("unchecked")
      SQLValueSetter<JAVA> configuredSetter = (SQLValueSetter<JAVA>)internal.getSetter();
      configuredSetter.setValue(stmt, index, value);
      return;
    }
    else if (value != null && internal != null && internal.getConverter() != null)
    {
      @SuppressWarnings("unchecked")
      Function<Object, Object> converter = (Function<Object, Object>)internal.getConverter();
      converted = converter.apply(value);
    }

    Integer explicitType = internal == null ? null : internal.getSQLType();

    if (explicitType != null)
    {
      if (converted == null)
      {
        stmt.setNull(index, explicitType.intValue());
      }
      else
      {
        stmt.setObject(index, converted, explicitType.intValue());
      }
    }
    else
    {
      DBType inferredType = occurrence.getInferredType();

      if (converted == null && inferredType == null)
      {
        throw new IllegalArgumentException("Cannot bind NULL at JDBC parameter " + index + ": specify SQL.param(sqlType) or use an explicit SQLValueSetter");
      }

      adapter.bindParameter(stmt, index, inferredType, converted);
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

  private void validateRange(int startIndex, int count)
  {
    int parameterCount = occurrences.size();

    if (startIndex < 1 || startIndex > parameterCount + 1)
    {
      throw new IllegalArgumentException("JDBC start index " + startIndex + " is outside 1.." + (parameterCount + 1));
    }

    if (count > parameterCount - (startIndex - 1))
    {
      throw new IllegalArgumentException("Binding " + count + " values from JDBC index " + startIndex + " exceeds " + parameterCount + " parameters");
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class ParameterBinder implements SQLParameterBinder
  {
    private final PreparedStatement stmt;

    private int nextIndex;

    private ParameterBinder(PreparedStatement stmt, int nextIndex)
    {
      this.stmt = stmt;
      this.nextIndex = nextIndex;
    }

    @Override
    public SQLParameterBinder bind(Object... values) throws SQLException
    {
      Objects.requireNonNull(values, "values");
      validateRange(nextIndex, values.length);
      bindFrom(stmt, nextIndex, values);
      nextIndex += values.length;
      return this;
    }

    @Override
    public <JAVA> SQLParameterBinder bindParameter(JAVA value, SQLValueSetter<? super JAVA> setter) throws SQLException
    {
      validateRange(nextIndex, 1);
      CompiledStatement.this.bindParameter(stmt, nextIndex, value, setter);
      ++nextIndex;
      return this;
    }

    @Override
    public SQLParameterBinder bindParameter(SQLDefaultSetter setter) throws SQLException
    {
      Objects.requireNonNull(setter, "setter");
      validateRange(nextIndex, 1);
      setter.setDefaultValue(stmt, nextIndex);
      ++nextIndex;
      return this;
    }

    @Override
    public <JAVA, JDBC> SQLParameterBinder bindParameter(JAVA value, Function<JAVA, JDBC> converter) throws SQLException
    {
      validateRange(nextIndex, 1);
      CompiledStatement.this.bindParameter(stmt, nextIndex, value, converter);
      ++nextIndex;
      return this;
    }

    @Override
    public int getNextIndex()
    {
      return nextIndex;
    }

    @Override
    public SQLParameterBinder from(int index)
    {
      validateRange(index, 0);
      nextIndex = index;
      return this;
    }

    @Override
    public SQLParameterBinder skip()
    {
      return skip(1);
    }

    @Override
    public SQLParameterBinder skip(int count)
    {
      if (count < 0)
      {
        throw new IllegalArgumentException("Skip count must not be negative: " + count);
      }

      long destination = (long)nextIndex + count;
      if (destination > Integer.MAX_VALUE)
      {
        throw new IllegalArgumentException("Skipping " + count + " positions from JDBC index " + nextIndex + " overflows the valid range");
      }

      int index = (int)destination;
      validateRange(index, 0);
      nextIndex = index;
      return this;
    }
  }
}
