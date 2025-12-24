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
package org.eclipse.net4j.util.collection;

import org.eclipse.net4j.util.event.INotifier;

/**
 * @author Eike Stepper
 */
public interface IHistory<T> extends INotifier, Iterable<IHistoryElement<T>>
{
  public boolean isEmpty();

  public int size();

  public boolean clear();

  public int indexOf(T data);

  public boolean add(T data);

  public IHistoryElement<T> remove(int index);

  public IHistoryElement<T> get(int index);

  public T getMostRecent();

  public <D> D[] getData(D[] a);

  public IHistoryElement<T>[] toArray();
}
