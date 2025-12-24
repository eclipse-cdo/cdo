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
package org.eclipse.emf.cdo.explorer.ui.checkouts.actions;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.navigator.ICommonViewerSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

/**
 * @author Eike Stepper
 */
public abstract class AbstractActionProvider<T> extends CommonActionProvider
{
  private final Class<T> selectionType;

  private final String id;

  private final String title;

  private final String appendToGroup;

  private ICommonActionExtensionSite config;

  private ICommonViewerWorkbenchSite viewSite;

  public AbstractActionProvider(Class<T> selectionType, String id, String title, String appendToGroup)
  {
    this.selectionType = selectionType;
    this.id = id;
    this.title = title;
    this.appendToGroup = appendToGroup;
  }

  public final ICommonActionExtensionSite getConfig()
  {
    return config;
  }

  public final ICommonViewerWorkbenchSite getViewSite()
  {
    return viewSite;
  }

  public final StructuredViewer getViewer()
  {
    return config.getStructuredViewer();
  }

  public final IStructuredSelection getSelection()
  {
    return (IStructuredSelection)getContext().getSelection();
  }

  protected T getSelectedElement(IStructuredSelection selection)
  {
    if (selection.size() == 1)
    {
      Object element = selection.getFirstElement();
      if (selectionType.isInstance(element))
      {
        return selectionType.cast(element);
      }
    }

    return null;
  }

  @Override
  public final void init(ICommonActionExtensionSite config)
  {
    this.config = config;

    ICommonViewerSite viewSite = config.getViewSite();
    if (viewSite instanceof ICommonViewerWorkbenchSite)
    {
      this.viewSite = (ICommonViewerWorkbenchSite)viewSite;
      init(this.viewSite);
    }
  }

  protected void init(ICommonViewerWorkbenchSite viewSite)
  {
  }

  @Override
  public final void fillContextMenu(IMenuManager menu)
  {
    if (viewSite == null)
    {
      return;
    }

    IStructuredSelection selection = getSelection();
    if (selection == null || selection.isEmpty())
    {
      return;
    }

    T selectedElement = getSelectedElement(selection);
    if (selectedElement == null)
    {
      return;
    }

    IMenuManager subMenu = new MenuManager(title, id);
    subMenu.add(new GroupMarker(ICommonMenuConstants.GROUP_TOP));

    if (fillSubMenu(viewSite, subMenu, selectedElement))
    {
      subMenu.add(new GroupMarker(ICommonMenuConstants.GROUP_ADDITIONS));
      menu.appendToGroup(appendToGroup, subMenu);
    }
  }

  protected abstract boolean fillSubMenu(ICommonViewerWorkbenchSite viewSite, IMenuManager subMenu, T selectedElement);
}
