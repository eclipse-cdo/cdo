/*
 * Copyright (c) 2007, 2011, 2012, 2015, 2025, 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.db;

import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBAdapterID;

/**
 * Describes a database adapter that can be created lazily by the adapter registry.
 *
 * <p>The descriptor's name and version form the registration identity. Names are
 * compared case-insensitively and versions are compared numerically by their
 * dot-separated components, with trailing zero components ignored. The values
 * returned by {@link #createDBAdapter()} should normally agree with this identity.
 * If they do not, the registry keeps the descriptor identity authoritative for
 * lookup and exposed adapter metadata.</p>
 *
 * <p>Service providers typically contribute descriptors through the database adapter
 * extension point and implement {@link #createDBAdapter()} to construct the actual
 * adapter only when a consumer requests it.</p>
 *
 * @author Eike Stepper
 */
public abstract class DBAdapterDescriptor implements IDBAdapterID
{
  private String name;

  private String version;

  private final String[] databaseProductNames;

  private final String databaseProductNamePattern;

  private final String databaseDriverNamePattern;

  private final String databaseURLPattern;

  public DBAdapterDescriptor(String name)
  {
    this(name, null);
  }

  public DBAdapterDescriptor(String name, String version)
  {
    this(name, version, null, null, null, null);
  }

  /**
   * Creates a lazy adapter descriptor with JDBC product recognition metadata.
   * Exact product names are trimmed and compared case-insensitively. Optional
   * regular expressions use case-insensitive full-string matching; product-name
   * matching is an alternative between exact names and the product pattern, while
   * URL and driver-name patterns are additional required conditions. A missing
   * product-name array defaults to the declared adapter name.
   *
   * @param name the registered adapter name
   * @param version the registered adapter version
   * @param databaseProductNames exact JDBC product names, or {@code null} to use the adapter name
   * @param databaseProductNamePattern an optional full-string product-name regular expression
   * @param databaseDriverNamePattern an optional full-string JDBC driver-name regular expression
   * @param databaseURLPattern an optional full-string JDBC URL regular expression
   * @since 4.15
   */
  public DBAdapterDescriptor(String name, String version, String[] databaseProductNames, String databaseProductNamePattern, String databaseDriverNamePattern,
      String databaseURLPattern)
  {
    this.name = name;
    this.version = version;
    this.databaseProductNames = databaseProductNames == null || databaseProductNames.length == 0 ? new String[] { name } : databaseProductNames.clone();
    this.databaseProductNamePattern = databaseProductNamePattern;
    this.databaseDriverNamePattern = databaseDriverNamePattern;
    this.databaseURLPattern = databaseURLPattern;
  }

  /**
   * Returns the exact JDBC product names associated with this descriptor.
   *
   * @return a defensive copy of the names
   * @since 4.15
   */
  public String[] getDatabaseProductNames()
  {
    return databaseProductNames.clone();
  }

  /**
   * Returns the optional product-name regular expression.
   *
   * @return the pattern, or {@code null}
   * @since 4.15
   */
  public String getDatabaseProductNamePattern()
  {
    return databaseProductNamePattern;
  }

  /**
   * Returns the optional JDBC driver-name regular expression.
   *
   * @return the pattern, or {@code null}
   * @since 4.15
   */
  public String getDatabaseDriverNamePattern()
  {
    return databaseDriverNamePattern;
  }

  /**
   * Returns the optional JDBC URL regular expression.
   *
   * @return the pattern, or {@code null}
   * @since 4.15
   */
  public String getDatabaseURLPattern()
  {
    return databaseURLPattern;
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public String getVersion()
  {
    return version;
  }

  @Override
  public int hashCode()
  {
    return DBAdapterID.copy(this).hashCode();
  }

  @Override
  public boolean equals(Object obj)
  {
    return obj instanceof IDBAdapterID && DBAdapterID.copy(this).equals(DBAdapterID.copy((IDBAdapterID)obj));
  }

  @Override
  public String toString()
  {
    return getName() + "-" + getVersion(); //$NON-NLS-1$
  }

  /**
   * Creates the adapter represented by this descriptor.
   *
   * <p>The registry invokes this method on demand for adapter lookups and caches the
   * resulting instance. Metadata-only queries do not invoke this method.</p>
   *
   * @return the created adapter, or {@code null} if the provider cannot create it
   */
  public abstract IDBAdapter createDBAdapter();
}
