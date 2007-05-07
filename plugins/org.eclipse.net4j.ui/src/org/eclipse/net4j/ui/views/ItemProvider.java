/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.ui.views;

import org.eclipse.net4j.ui.StructuredContentProvider;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import java.util.ArrayList;
import java.util.List;

public abstract class ItemProvider<INPUT> extends StructuredContentProvider<INPUT> implements ITreeContentProvider,
    ILabelProvider
{
  protected static final Object[] NO_CHILDREN = new Object[0];

  private List<ILabelProviderListener> listeners = new ArrayList(0);

  public ItemProvider()
  {
  }

  public Object[] getElements(Object parent)
  {
    return getChildren(parent);
  }

  public boolean hasChildren(Object parent)
  {
    return getChildren(parent).length != 0;
  }

  public String getText(Object obj)
  {
    return obj.toString();
  }

  public Image getImage(Object obj)
  {
    String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
    return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
  }

  public boolean isLabelProperty(Object element, String property)
  {
    return true;
  }

  public void addListener(ILabelProviderListener listener)
  {
    listeners.add(listener);
  }

  public void removeListener(ILabelProviderListener listener)
  {
    listeners.remove(listener);
  }

  protected void fillContextMenu(IMenuManager manager, ITreeSelection selection)
  {
  }
}