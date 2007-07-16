/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.util.collection;

import org.eclipse.net4j.util.collection.IHistory;
import org.eclipse.net4j.util.collection.IHistoryElement;

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

  public IHistory getHistory()
  {
    return history;
  }

  public T getData()
  {
    return data;
  }

  public String getText()
  {
    return data.toString();
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof HistoryElement)
    {
      HistoryElement that = (HistoryElement)obj;
      return this.history.equals(that.history) && this.data.equals(that.data);
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
