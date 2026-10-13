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

/**
 * Staged fluent builders for SQL data manipulation. Each builder operation returns the stage that permits the next
 * valid clause; incomplete assignments and joins therefore cannot be built or compiled through the public type
 * signatures. Complete stages expose {@link org.eclipse.net4j.db.dml.fluent.SQLBuildStep#build()} for an immutable
 * adapter-neutral snapshot and {@link org.eclipse.net4j.db.dml.fluent.SQLBuildStep#compile(org.eclipse.net4j.db.IDBAdapter)}
 * for direct adapter-specific compilation. Builder stages are mutable construction cursors: reuse a retained stage
 * only while it remains current, and keep a previously returned template as the stable statement definition.
 */
package org.eclipse.net4j.db.dml.fluent;
