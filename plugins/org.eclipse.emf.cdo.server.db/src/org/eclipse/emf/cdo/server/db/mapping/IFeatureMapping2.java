/*
 * Copyright (c) 2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.db.mapping;

import org.eclipse.net4j.db.ddl.IDBField;

/**
 * An extension interface for {@link IFeatureMapping feature mappings}.
 *
 * @author Eike Stepper
 * @since 4.7
 */
public interface IFeatureMapping2 extends IFeatureMapping
{
  /**
   * @return The value field if this feature mapping is an {@link ITypeMapping} or the size field if this feature mapping is an {@link IListMapping}.
   */
  public IDBField getField();

  public void setField(IDBField field);

  public IDBField getUnsettableField();

  public void setUnsettableField(IDBField unsettableField);
}
