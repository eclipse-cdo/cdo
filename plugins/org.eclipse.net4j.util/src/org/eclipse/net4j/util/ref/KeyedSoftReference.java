/*
 * Copyright (c) 2007, 2009, 2011, 2012, 2015, 2016, 2019, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ref;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class KeyedSoftReference<K, T> extends SoftReference<T> implements KeyedReference<K, T>
{
  private K key;

  public KeyedSoftReference(K key, T ref)
  {
    super(ref);
    this.key = key;
  }

  public KeyedSoftReference(K key, T ref, ReferenceQueue<T> queue)
  {
    super(ref, queue);
    this.key = key;
  }

  @Override
  public ReferenceType getType()
  {
    return ReferenceType.SOFT;
  }

  @Override
  public K getKey()
  {
    return key;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("KeyedSoftReference[{0} -> {1}]", key, get()); //$NON-NLS-1$
  }
}
