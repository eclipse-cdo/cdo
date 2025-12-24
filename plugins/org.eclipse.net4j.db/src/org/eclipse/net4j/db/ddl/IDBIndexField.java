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
package org.eclipse.net4j.db.ddl;

import org.eclipse.net4j.util.collection.PositionProvider;

/**
 * An index field specification in a {@link IDBIndex DB index}.
 *
 * @since 4.2
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IDBIndexField extends IDBSchemaElement, PositionProvider
{
  /**
   * @since 4.2
   */
  @Override
  public IDBIndex getParent();

  public IDBIndex getIndex();

  public IDBField getField();
}
