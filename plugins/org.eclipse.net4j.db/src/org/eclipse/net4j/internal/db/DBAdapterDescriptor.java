/*
 * Copyright (c) 2007, 2011, 2012, 2015, 2025 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.net4j.db.IDBAdapterID;

/**
 * Describes a database adapter that can be created lazily by the adapter registry.
 *
 * <p>The descriptor's name and version form the registration identity. Names are
 * compared case-insensitively and versions are compared numerically by their
 * dot-separated components, with trailing zero components ignored. The values
 * returned by {@link #createDBAdapter()} should normally agree with this identity.
 * If they do not, the registry keeps the descriptor identity authoritative for
 * lookup and exposed adapter metadata.</p>
 *
 * <p>Service providers typically contribute descriptors through the database adapter
 * extension point and implement {@link #createDBAdapter()} to construct the actual
 * adapter only when a consumer requests it.</p>
 *
 * @author Eike Stepper
 */
public abstract class DBAdapterDescriptor implements IDBAdapterID
{
  private String name;

  private String version;

  public DBAdapterDescriptor(String name)
  {
    this(name, null);
  }

  public DBAdapterDescriptor(String name, String version)
  {
    this.name = name;
    this.version = version;
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public String getVersion()
  {
    return version;
  }

  @Override
  public int hashCode()
  {
    return DBAdapterID.copy(this).hashCode();
  }

  @Override
  public boolean equals(Object obj)
  {
    return obj instanceof IDBAdapterID && DBAdapterID.copy(this).equals(DBAdapterID.copy((IDBAdapterID)obj));
  }

  @Override
  public String toString()
  {
    return getName() + "-" + getVersion(); //$NON-NLS-1$
  }

  /**
   * Creates the adapter represented by this descriptor.
   *
   * <p>The registry invokes this method on demand for adapter lookups and caches the
   * resulting instance. Metadata-only queries do not invoke this method.</p>
   *
   * @return the created adapter, or {@code null} if the provider cannot create it
   */
  public abstract IDBAdapter createDBAdapter();
}
