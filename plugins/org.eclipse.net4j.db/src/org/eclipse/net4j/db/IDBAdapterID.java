/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.db;

/**
 * Identifies a database adapter by its database-system name and development version.
 *
 * <p>Names are matched case-insensitively. Versions are interpreted as dot-separated
 * numeric components, so that, for example, {@code 8}, {@code 8.0}, and
 * {@code 8.0.0} denote the same logical version. Implementations must therefore
 * expose stable name and version values for the lifetime of the identified adapter.</p>
 *
 * <p>This interface intentionally does not extend {@link Comparable}. Consumers that
 * need a canonical, sortable value can use the registry's sorted result methods or
 * the framework's internal ID implementation.</p>
 *
 * @author Eike Stepper
 * @since 4.14
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IDBAdapterID
{
  /**
   * Returns the adapter name as supplied by its provider. Lookup compares names
   * case-insensitively, but does not otherwise normalize the value exposed here.
   *
   * @return the adapter name
   */
  public String getName();

  /**
   * Returns the adapter version as supplied by its provider. Lookup compares the
   * dot-separated numeric components, ignoring trailing zero components; formatting
   * such as leading zeroes is retained in this returned value.
   *
   * @return the adapter version
   */
  public String getVersion();
}
