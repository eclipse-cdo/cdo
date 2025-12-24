/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.util.ObjectUtil;

/**
 * @author Eike Stepper
 */
public class HistoryElement<T> implements IHistoryElement<T>
{
  private IHistory<IHistoryElement<T>> history;

  private T data;

  public HistoryElement(IHistory<IHistoryElement<T>> history, T data)
  {
    this.history = history;
    this.data = data;
  }

  @Override
  public IHistory<IHistoryElement<T>> getHistory()
  {
    return history;
  }

  @Override
  public T getData()
  {
    return data;
  }

  @Override
  public String getText()
  {
    return data.toString();
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj instanceof IHistoryElement<?>)
    {
      @SuppressWarnings("unchecked")
      IHistoryElement<T> that = (IHistoryElement<T>)obj;
      return ObjectUtil.equals(history, that.getHistory()) && ObjectUtil.equals(data, that.getData());
    }

    return false;
  }

  @Override
  public int hashCode()
  {
    return history.hashCode() ^ data.hashCode();
  }

  @Override
  public String toString()
  {
    return getText();
  }
}
