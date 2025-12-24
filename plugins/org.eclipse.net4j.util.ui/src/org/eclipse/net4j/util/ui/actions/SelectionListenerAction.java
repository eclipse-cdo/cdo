/*
 * Copyright (c) 2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.net4j.util.ui.actions;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Christian W. Damus (CEA LIST)
 * @since 3.4
 */
public abstract class SelectionListenerAction<T> extends BaseSelectionListenerAction
{
  private T selectedObject;

  public SelectionListenerAction(String text)
  {
    super(text);
  }

  public SelectionListenerAction(String text, ImageDescriptor imageDescriptor)
  {
    this(text);
    setImageDescriptor(imageDescriptor);
  }

  @Override
  protected boolean updateSelection(IStructuredSelection selection)
  {
    boolean result = !selection.isEmpty();
    if (result)
    {
      Object first = selection.getFirstElement();
      Class<T> type = getType();
      result = type.isInstance(first);
      if (result)
      {
        selectedObject = type.cast(first);
      }
    }

    return super.updateSelection(selection) && result;
  }

  protected T getSelectedObject()
  {
    return selectedObject;
  }

  protected abstract Class<T> getType();
}
