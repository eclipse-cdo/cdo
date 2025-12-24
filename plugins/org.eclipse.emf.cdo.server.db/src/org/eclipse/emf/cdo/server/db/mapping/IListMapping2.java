/*
 * Copyright (c) 2011, 2012, 2015, 2016, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.db.mapping;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;

/**
 * Extension interface to {@link IListMapping}.
 *
 * @author Eike Stepper
 * @since 4.1
 */
public interface IListMapping2 extends IListMapping
{
  /**
   * @since 4.14
   */
  @Override
  public default ITypeMapping getTypeMapping()
  {
    return null;
  }

  public void addSimpleChunkWhere(IDBStoreAccessor accessor, CDOID cdoid, StringBuilder builder, int index);

  public void addRangedChunkWhere(IDBStoreAccessor accessor, CDOID cdoid, StringBuilder builder, int fromIndex, int toIndex);
}
