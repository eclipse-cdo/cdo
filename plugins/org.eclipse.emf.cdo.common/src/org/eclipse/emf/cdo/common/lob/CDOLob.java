/*
 * Copyright (c) 2011, 2012, 2015, 2021, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.lob;

import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * A identifiable large object with streaming support.
 *
 * @author Eike Stepper
 * @since 4.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public abstract class CDOLob<IO> extends CDOLobInfo
{
  private CDOLobStore store;

  CDOLob(byte[] id, long size)
  {
    this.id = id;
    this.size = size;
  }

  CDOLob(IO contents, CDOLobStore store) throws IOException
  {
    this.store = Objects.requireNonNull(store);

    CDOLobInfo info = put(contents);
    id = info.getID();
    size = info.getSize();
  }

  CDOLob(ExtendedDataInput in) throws IOException
  {
    id = in.readByteArray();
    size = in.readLong();
  }

  final void write(ExtendedDataOutput out) throws IOException
  {
    out.writeByteArray(id);
    out.writeLong(size);
  }

  final void setStore(CDOLobStore store)
  {
    this.store = store;
  }

  public final CDOLobStore getStore()
  {
    return store;
  }

  /**
   * @since 4.19
   */
  public abstract File getStoreFile();

  public abstract IO getContents() throws IOException;

  /**
   * @since 4.13
   */
  public abstract String getString() throws IOException;

  /**
   * @since 4.19
   */
  public final long getByteCount()
  {
    File file = getStoreFile();
    if (file == null || !file.isFile())
    {
      throw new IllegalStateException("Lob file does not exist: " + file);
    }

    return file.length();
  }

  protected abstract CDOLobInfo put(IO contents) throws IOException;
}
