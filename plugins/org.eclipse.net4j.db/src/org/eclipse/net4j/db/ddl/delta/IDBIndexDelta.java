/*
 * Copyright (c) 2013, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.db.ddl.delta;

import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBSchema;

import java.util.Map;

/**
 * @since 4.2
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IDBIndexDelta extends IDBDeltaWithProperties
{
  public static final String TYPE_PROPERTY = "type";

  /**
   * @since 4.5
   */
  public static final String OPTIONAL_PROPERTY = "optional";

  @Override
  public IDBTableDelta getParent();

  public int getIndexFieldDeltaCount();

  public IDBIndexFieldDelta getIndexFieldDelta(int position);

  public IDBIndexFieldDelta getIndexFieldDelta(String name);

  public Map<String, IDBIndexFieldDelta> getIndexFieldDeltas();

  public IDBIndexFieldDelta[] getIndexFieldDeltasSortedByPosition();

  @Override
  public IDBIndex getSchemaElement(IDBSchema schema);
}
