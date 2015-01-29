/*
 * Copyright (c) 2009-2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.handlers;

import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.handlers.LongRunningHandler;

import org.eclipse.jface.viewers.ISelection;

import java.util.List;

/**
 * @author Eike Stepper
 */
public abstract class BaseHandler<T> extends LongRunningHandler
{
  private final Class<T> type;

  private final Boolean multi;

  protected List<T> elements;

  public BaseHandler(Class<T> type, Boolean multi)
  {
    this.type = type;
    this.multi = multi;
  }

  @Override
  protected boolean updateSelection(ISelection selection)
  {
    elements = UIUtil.adaptElements(getSelection(), type);
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
}
