/*
 * Copyright (c) 2007, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
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

/**
 * @author Eike Stepper
 */
public abstract class DBAdapterDescriptor
{
  private String name;

  public DBAdapterDescriptor(String name)
  {
    this.name = name;
  }

  public String getName()
  {
    return name;
  }

  public abstract IDBAdapter createDBAdapter();
}
