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
package org.eclipse.net4j.util.ref;

import java.lang.ref.Reference;

/**
 * @see Reference
 * @author Eike Stepper
 */
public interface KeyedReference<K, T>
{
  public ReferenceType getType();

  public K getKey();

  /**
   * @see Reference#get()
   */
  public T get();

  /**
   * @see Reference#clear()
   */
  public void clear();

  /**
   * @see Reference#isEnqueued()
   */
  public boolean isEnqueued();

  /**
   * @see Reference#enqueue()
   */
  public boolean enqueue();
}
