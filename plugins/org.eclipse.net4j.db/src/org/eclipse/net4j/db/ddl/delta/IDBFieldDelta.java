/*
 * Copyright (c) 2013, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBSchema;

/**
 * @since 4.2
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IDBFieldDelta extends IDBDeltaWithPosition
{
  public static final String TYPE_PROPERTY = "type";

  public static final String PRECISION_PROPERTY = "precision";

  public static final String SCALE_PROPERTY = "scale";

  public static final String NOT_NULL_PROPERTY = "notNull";

  @Override
  public IDBTableDelta getParent();

  @Override
  public IDBField getSchemaElement(IDBSchema schema);
}
