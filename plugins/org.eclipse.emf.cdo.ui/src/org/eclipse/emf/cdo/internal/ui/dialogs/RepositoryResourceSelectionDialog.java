/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.dialogs;

import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.ui.AbstractResourceSelectionDialog;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Eike Stepper
 */
public class RepositoryResourceSelectionDialog extends AbstractResourceSelectionDialog<List<CDOView>>
{
  private final List<CDOView> views;

  public RepositoryResourceSelectionDialog(Shell shell, boolean multi, List<CDOView> views)
  {
    super(shell, multi, "Select Repository Resources", "Select resources from the current repository.",
        OM.Activator.INSTANCE.loadImageDescriptor("icons/full/wizban/repo.gif"));
    this.views = views;
  }

  @Override
  protected List<CDOView> getInput()
  {
    return views;
  }

  @Override
  protected void createUI(Composite parent)
  {
    super.createUI(parent);

    UIUtil.asyncExec(parent.getDisplay(), () -> {
      TreeViewer viewer = getViewer();
      IStructuredContentProvider contentProvider = (IStructuredContentProvider)viewer.getContentProvider();

      Object[] elements = contentProvider.getElements(getInput());
      if (elements != null && elements.length == 1)
      {
        viewer.expandToLevel(elements[0], 1);
      }
    });
  }

  @Override
  protected boolean elementHasChildren(Object object, Predicate<Object> defaultHasChildren)
  {
    if (object == views)
    {
      return !views.isEmpty();
    }

    return super.elementHasChildren(object, defaultHasChildren);
  }

  @Override
  protected Object[] elementGetChildren(Object object, Function<Object, Object[]> defaultGetChildren)
  {
    if (object == views)
    {
      return views.toArray();
    }

    return super.elementGetChildren(object, defaultGetChildren);
  }

  @Override
  protected Object elementGetParent(Object object, Function<Object, Object> defaultGetParent)
  {
    if (object instanceof CDOView)
    {
      return views;
    }

    return super.elementGetParent(object, defaultGetParent);
  }
}
