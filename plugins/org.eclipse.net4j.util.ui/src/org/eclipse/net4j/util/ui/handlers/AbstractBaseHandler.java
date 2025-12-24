/*
 * Copyright (c) 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.handlers;

import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.jface.viewers.ISelection;

import java.util.List;

/**
 * @author Eike Stepper
 * @since 3.5
 */
public abstract class AbstractBaseHandler<T> extends LongRunningHandler
{
  private final Class<T> type;

  private final Boolean multi;

  protected List<T> elements;

  public AbstractBaseHandler(Class<T> type, Boolean multi)
  {
    this.type = type;
    this.multi = multi;
  }

  @Override
  protected boolean updateSelection(ISelection selection)
  {
    elements = collectElements(selection);
    if (elements == null)
    {
      return false;
    }

    if (elements.isEmpty())
    {
      elements = null;
      return false;
    }

    if (multi != null)
    {
      if (multi != elements.size() > 1)
      {
        return false;
      }
    }

    return true;
  }

  protected List<T> collectElements(ISelection selection)
  {
    return UIUtil.adaptElements(selection, type);
  }
}
