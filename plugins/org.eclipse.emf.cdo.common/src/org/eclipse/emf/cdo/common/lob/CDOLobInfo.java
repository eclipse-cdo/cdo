/*
 * Copyright (c) 2011, 2012, 2015, 2016, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.lob;

import org.eclipse.net4j.util.HexUtil;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Objects;

/**
 * Encapsulates {@link #getID() ID} and {@link #getSize() size} of a {@link CDOLob large object}.
 *
 * @author Eike Stepper
 * @since 4.0
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class CDOLobInfo
{
  byte[] id;

  long size;

  CDOLobInfo()
  {
  }

  public CDOLobInfo(byte[] id, long size)
  {
    this.id = id;
    this.size = size;
  }

  /**
   * The identifier of this large object. A SHA-1 digest of the content of this large object.
   */
  public final byte[] getID()
  {
    return id;
  }

  /**
   * A string representation of the {@link #getID() identifier} of this large object.
   *
   * @since 4.6
   */
  public final String getIDString()
  {
    return HexUtil.bytesToHex(id);
  }

  public final long getSize()
  {
    return size;
  }

  @Override
  public final int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(id);
    result = prime * result + Objects.hash(size);
    return result;
  }

  @Override
  public final boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }

    if (!(obj instanceof CDOLobInfo))
    {
      return false;
    }

    CDOLobInfo other = (CDOLobInfo)obj;
    return Arrays.equals(id, other.id) && size == other.size;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("{0}[id={1}, size={2}]", getClass().getSimpleName(), getIDString(), size);
  }
}
