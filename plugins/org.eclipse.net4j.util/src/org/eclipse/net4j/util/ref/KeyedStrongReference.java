/*
 * Copyright (c) 2007, 2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
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

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class KeyedStrongReference<K, T> implements KeyedReference<K, T>
{
  private K key;

  private T ref;

  public KeyedStrongReference(K key, T ref)
  {
    this.key = key;
    this.ref = ref;
  }

  @Override
  public ReferenceType getType()
  {
    return ReferenceType.STRONG;
  }

  @Override
  public K getKey()
  {
    return key;
  }

  @Override
  public T get()
  {
    return ref;
  }

  @Override
  public void clear()
  {
    ref = null;
  }

  @Override
  public boolean isEnqueued()
  {
    return false;
  }

  @Override
  public boolean enqueue()
  {
    return false;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("KeyedStrongReference[{0} -> {1}]", key, ref); //$NON-NLS-1$
  }
}
