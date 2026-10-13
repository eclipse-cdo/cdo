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
package org.eclipse.net4j.db.dml;

import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnection;

/**
 * An immutable SQL definition independent of a database adapter. Compilation renders SQL with
 * adapter-specific identifier quoting and retains ordered parameter occurrences. The resulting
 * statement can be prepared through an {@link IDBConnection} and bound using
 * its positional or parameter-identity operations.
 *
 * @author Eike Stepper
 * @since 4.15
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface SQLTemplate
{
  /**
   * Compiles this immutable definition into SQL text and ordered parameter occurrence metadata.
   * The supplied adapter controls identifier quoting. The template can be compiled repeatedly
   * with different adapters without changing the template or previously compiled statements.
   *
   * @param adapter the adapter whose dialect governs compilation
   * @return an adapter-specific statement
   */
  public SQLStatement compile(IDBAdapter adapter);
}
