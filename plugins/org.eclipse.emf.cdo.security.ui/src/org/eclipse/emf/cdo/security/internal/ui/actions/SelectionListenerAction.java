/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.security.internal.ui.actions;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Christian W. Damus (CEA LIST)
 */
public class SelectionListenerAction extends BaseSelectionListenerAction
{
  private EObject selectedObject;

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
      result = first instanceof EObject;
      if (result)
      {
        selectedObject = (EObject)first;
      }
    }

    return super.updateSelection(selection) && result;
  }

  protected EObject getSelectedObject()
  {
    return selectedObject;
  }
}
