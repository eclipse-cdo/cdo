/*
 * Copyright (c) 2007-2013, 2016, 2023, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - bug 289445
 */
package org.eclipse.net4j.db;

import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.ddl.delta.IDBSchemaDelta;
import org.eclipse.net4j.internal.db.DBAdapterRegistry;
import org.eclipse.net4j.spi.db.DBAdapter;
import org.eclipse.net4j.util.registry.IRegistry;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Set;

/**
 * Abstracts all aspects of a database that are vendor-specific.
 *
 * <p>Consumers normally obtain an adapter from {@link Registry#INSTANCE} and use
 * its name and version as an identity. Adapter names are compared without regard to
 * case. Versions are compared numerically by dot-separated components, with missing
 * components treated as zero; consequently, versions such as {@code 8},
 * {@code 8.0}, and {@code 8.0.0} have the same logical identity.</p>
 *
 * <p>Service providers should extend {@link DBAdapter}. They must return stable
 * identity values from {@link #getName()} and {@link #getVersion()}, and should use
 * the version to distinguish incompatible or materially different adapter behavior.
 * The identity values declared by an extension descriptor are authoritative during
 * registry lookup, even if a provider-created adapter reports different metadata.</p>
 *
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients. Subclass {@link DBAdapter} instead.
 */
public interface IDBAdapter extends IDBAdapterID
{
  /**
   * @deprecated As of 4.14, use {@link Registry#INSTANCE} instead.
   */
  @Deprecated
  public static final IRegistry<String, IDBAdapter> REGISTRY = Registry.INSTANCE;

  /**
   * Returns the provider's adapter name. Registry registration and lookup compare
   * this value case-insensitively; the value returned by this method is not lowercased
   * for the caller.
   *
   * @return the adapter name
   */
  @Override
  public String getName();

  /**
   * Returns the provider's adapter version. Registry registration and lookup compare
   * dot-separated numeric components and ignore trailing zero components, while the
   * original representation returned here remains available to callers.
   *
   * @return the adapter version
   */
  @Override
  public String getVersion();

  /**
   * @since 4.12
   */
  public boolean isCaseSensitive();

  /**
   * @since 4.12
   */
  public String getDefaultSchemaName(Connection connection);

  /**
   * @since 4.3
   */
  public IDBConnectionProvider createConnectionProvider(DataSource dataSource);

  /**
   * @since 4.5
   */
  public Connection modifyConnection(Connection connection);

  /**
   * @since 4.12
   */
  public void createSchema(Connection connection, String schemaName);

  /**
   * Opens a schema transaction through the adapter extension point.
   *
   * @since 4.14
   */
  public IDBSchemaTransaction openSchemaTransaction(IDBDatabase database, IDBConnection currentConnection);

  /**
   * @since 4.2
   */
  public IDBSchema readSchema(Connection connection, String name);

  /**
   * @since 4.2
   */
  public void readSchema(Connection connection, IDBSchema schema);

  /**
   * @since 4.2
   */
  public void updateSchema(Connection connection, IDBSchema schema, IDBSchemaDelta delta) throws DBException;

  public Set<IDBTable> createTables(Iterable<? extends IDBTable> tables, Connection connection) throws DBException;

  public boolean createTable(IDBTable table, Statement statement) throws DBException;

  public Collection<IDBTable> dropTables(Iterable<? extends IDBTable> tables, Connection connection) throws DBException;

  public boolean dropTable(IDBTable table, Statement statement);

  public String[] getReservedWords();

  public boolean isReservedWord(String word);

  /**
   * @since 2.0
   */
  public int getMaxTableNameLength();

  /**
   * @since 2.0
   */
  public int getMaxFieldNameLength();

  /**
   * Returns the column length for the given database type.
   *
   * @param type the {@link DBType} to check.
   * @return the supported column length for the type.
   * @since 4.2
   */
  public int getFieldLength(DBType type);

  public boolean isTypeIndexable(DBType type);

  /**
   * Converts a string before it is bound to a prepared statement.
   *
   * @since 4.14
   */
  public String convertString(PreparedStatement preparedStatement, int parameterIndex, String value);

  /**
   * Converts a string read from a result-set column.
   *
   * @since 4.14
   */
  public String convertString(ResultSet resultSet, int columnIndex, String value);

  /**
   * Converts a string read from a named result-set column.
   *
   * @since 4.14
   */
  public String convertString(ResultSet resultSet, String columnLabel, String value);

  /**
   * Returns the SQL-92 reserved words used by the adapter implementation.
   *
   * @since 4.14
   */
  public String[] getSQL92ReservedWords();

  /**
   * Appends the field names used by the adapter's table SQL generation.
   *
   * @since 4.14
   */
  public void appendFieldNames(Appendable appendable, IDBTable table);

  /**
   * Provide a way for the DBAdapter to override unsupported DB types with replacements. The default implementation just
   * returns the given type. Subclasses may override single types with replacements.
   *
   * @since 3.0
   */
  public DBType adaptType(DBType type);

  /**
   * Check if a character is valid as first character. (e.g., underscores are forbidden as first character in Derby
   * elements.
   *
   * @since 4.0
   */
  public boolean isValidFirstChar(char ch);

  /**
   * Check if an exception indicates a constraint violation (duplicate key)
   *
   * @since 4.0
   */
  public boolean isDuplicateKeyException(SQLException ex);

  /**
   * @since 4.2
   */
  public boolean isTableNotFoundException(SQLException ex);

  /**
   * @since 4.2
   */
  public boolean isColumnNotFoundException(SQLException ex);

  /**
   * @since 4.2
   */
  public String sqlRenameField(IDBField field, String oldName);

  /**
   * @since 4.2
   */
  public String sqlModifyField(IDBField field);

  /**
   * @since 4.13
   */
  public String sqlCharIndex(Object substring, Object string);

  /**
   * @since 4.13
   */
  public String sqlSubstring(Object string, Object startIndex, Object length);

  /**
   * @since 4.13
   */
  public String sqlSubstring(Object string, Object startIndex);

  /**
   * @since 4.13
   */
  public String sqlConcat(Object... strings);

  /**
   * Formats a prepared statement for diagnostic output.
   *
   * @since 4.14
   */
  public String format(PreparedStatement stmt);

  /**
   * Formats a result set for diagnostic output.
   *
   * @since 4.14
   */
  public String format(ResultSet resultSet);

  /**
   * Converts a Java value to the value used by SQL query execution.
   *
   * @since 4.14
   */
  public Object convertToSQL(Object value);

  /**
   * @deprecated As of 4.2 no longer supported because of IP issues for external build dependencies (the vendor driver libs).
   */
  @Deprecated
  public Driver getJDBCDriver();

  /**
   * @deprecated As of 4.2 no longer supported because of IP issues for external build dependencies (the vendor driver libs).
   */
  @Deprecated
  public DataSource createJDBCDataSource();

  /**
   * The registry of all registered {@link IDBAdapter adapters}.
   *
   * <p>The registry accepts eagerly created adapters and lazy extension descriptors.
   * Metadata methods such as {@link #getAdapterIDs()} do not instantiate descriptors.
   * Methods returning adapters materialize the requested descriptor on demand and
   * cache the resulting adapter for subsequent lookups.</p>
   *
   * <p>Registration is keyed by the logical pair of name and version. Names are
   * case-insensitive. Versions are dot-separated numeric values; trailing zero
   * components are insignificant. Thus registering {@code MySQL/8} prevents a
   * second registration of {@code mysql/8.0.0}. A lookup by name alone returns the
   * highest registered version. A lookup by an {@link IDBAdapterID} or by separate
   * name and version performs an exact logical lookup. The arrays returned by this
   * interface are snapshots and are ordered by name and numeric version.</p>
   *
   * @author Eike Stepper
   * @since 4.14
   */
  public interface Registry extends IRegistry<String, IDBAdapter>
  {
    public static final Registry INSTANCE = DBAdapterRegistry.INSTANCE;

    /**
     * Returns metadata for all registered adapters without instantiating lazy
     * extension descriptors.
     *
     * @return a sorted snapshot of registered adapter identities
     */
    public IDBAdapterID[] getAdapterIDs();

    /**
     * Returns metadata for all registered versions of a name without instantiating
     * lazy extension descriptors.
     *
     * @param adapterName the adapter name to match, case-insensitively
     * @return a sorted snapshot of matching adapter identities
     */
    public IDBAdapterID[] getAdapterIDs(String adapterName);

    /**
     * Returns all registered adapters, materializing lazy descriptors as necessary.
     *
     * @return a sorted snapshot of the available adapters
     */
    public IDBAdapter[] getAdapters();

    /**
     * Returns all registered variants for a name, materializing matching lazy
     * descriptors as necessary.
     *
     * @param adapterName the adapter name to match, case-insensitively
     * @return a sorted snapshot of the matching adapters
     */
    public IDBAdapter[] getAdapters(String adapterName);

    /**
     * Returns the adapter for an exact logical identity. A lazy descriptor is
     * materialized only if this lookup finds it.
     *
     * @param id the identity to match
     * @return the matching adapter, or {@code null} if none is registered
     */
    public IDBAdapter getAdapter(IDBAdapterID id);

    /**
     * Returns the adapter for an exact logical name/version pair. Name matching is
     * case-insensitive and version matching is numeric by dot-separated components.
     * A lazy descriptor is materialized only if this lookup finds it.
     *
     * @param adapterName the adapter name to match
     * @param version the adapter version to match
     * @return the matching adapter, or {@code null} if none is registered
     */
    public IDBAdapter getAdapter(String adapterName, String version);

    /**
     * Returns the highest registered version for a name. If the matching entry is a
     * lazy descriptor, it is materialized and cached before the adapter is returned.
     *
     * @param adapterName the adapter name to match, case-insensitively
     * @return the highest-version adapter, or {@code null} if no version is registered
     */
    public IDBAdapter getAdapter(String adapterName);
  }
}
