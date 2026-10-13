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
package org.eclipse.net4j.db.dml.fluent;

import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnection;
import org.eclipse.net4j.db.dml.SQLStatement;
import org.eclipse.net4j.db.dml.SQLTemplate;

/**
 * A fluent stage that has enough information to produce a reusable immutable definition.
 *
 * @author Eike Stepper
 * @since 4.15
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface SQLBuildStep
{
  /**
   * Captures the operations completed so far in a new immutable template. Later calls through a retained builder stage do not alter an already built template.
   *
   * @return an immutable definition snapshot
   * @since 4.15
   */
  public SQLTemplate build();

  /**
   * Compiles this complete definition for an adapter, rendering adapter-specific identifier quoting and recording parameter
   * occurrences in generated SQL order. The returned statement can be prepared through an {@link IDBConnection} and bound
   * through its positional or parameter-identity methods.
   *
   * @param adapter the database adapter for identifier quoting and NULL type mapping
   * @return the compiled statement
   */
  public default SQLStatement compile(IDBAdapter adapter)
  {
    return build().compile(adapter);
  }
}
