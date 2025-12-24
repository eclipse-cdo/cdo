/*
 * Copyright (c) 2011, 2012, 2015, 2019, 2023, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.collection;

import org.eclipse.net4j.util.CheckUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A wrapper for byte arrays that implements {@link #equals(Object)} and {@link #hashCode()} based on the array's contents.
 *
 * @author Eike Stepper
 * @since 3.2
 */
public final class ByteArrayWrapper
{
  private final byte[] data;

  public ByteArrayWrapper(byte[] data)
  {
    CheckUtil.checkArg(data, "data"); //$NON-NLS-1$
    this.data = data;
  }

  public byte[] getData()
  {
    return data;
  }

  @Override
  public boolean equals(Object other)
  {
    if (other instanceof ByteArrayWrapper)
    {
      return Arrays.equals(data, ((ByteArrayWrapper)other).data);
    }

    return false;
  }

  @Override
  public int hashCode()
  {
    return Arrays.hashCode(data);
  }

  @Override
  public String toString()
  {
    return data.toString();
  }

  public static Set<byte[]> toByteArray(Set<ByteArrayWrapper> wrappers)
  {
    if (wrappers == null)
    {
      return null;
    }

    Set<byte[]> result = new HashSet<>();
    for (ByteArrayWrapper wrapper : wrappers)
    {
      result.add(wrapper.getData());
    }

    return result;
  }
}
