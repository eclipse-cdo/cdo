/*
 * Copyright (c) 2011, 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.ui.composites;

import org.eclipse.emf.cdo.dawn.ui.views.DawnWizardPageItemProvider;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.IElementFilter;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

/**
 * @author Martin Fluegge
 * @since 1.0
 */
public class CDOResourceNodeSelectionWidget extends Composite
{
  private ContainerItemProvider<IContainer<Object>> itemProvider;

  private ShowResourcesViewFilter viewFilter;

  private TreeViewer viewer;

  public CDOResourceNodeSelectionWidget(Composite parent, int style)
  {
    super(parent, style);
    GridLayout layout = new GridLayout();
    layout.numColumns = 1;
    layout.marginWidth = 0;
    setLayout(layout);
    init();
  }

  public void setShowResources(boolean showResources)
  {
    viewFilter.setShowResources(showResources);
  }

  protected void init()
  {
    viewer = new TreeViewer(this, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
    itemProvider = createContainerItemProvider();
    viewer.setContentProvider(createContentProvider());
    viewer.setLabelProvider(createLabelProvider());
    viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
    viewer.setInput(getManagedContainer());

    viewFilter = new ShowResourcesViewFilter();
    viewer.addFilter(viewFilter);
  }

  public void addSelectionChangedListener(ISelectionChangedListener listener)
  {
    viewer.addSelectionChangedListener(listener);
  }

  public void removeSelectionChangedListener(ISelectionChangedListener listener)
  {
    viewer.removeSelectionChangedListener(listener);
  }

  protected IManagedContainer getManagedContainer()
  {
    return IPluginContainer.INSTANCE;
  }

  protected IContentProvider createContentProvider()
  {
    return itemProvider;
  }

  protected IBaseLabelProvider createLabelProvider()
  {
    ILabelDecorator labelDecorator = createLabelDecorator();
    return new DecoratingLabelProvider(itemProvider, labelDecorator);
  }

  protected ILabelDecorator createLabelDecorator()
  {
    return PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
  }

  protected ContainerItemProvider<IContainer<Object>> createContainerItemProvider()
  {
    return new DawnWizardPageItemProvider<>(new IElementFilter()
    {
      @Override
      public boolean filter(Object element)
      {
        return element instanceof CDOSession;
      }
    });
  }

  /**
   * @author Martin Fluegge
   */
  protected class ShowResourcesViewFilter extends ViewerFilter
  {
    private boolean showResources;

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element)
    {
      if (element instanceof CDOResource)
      {
        return isShowResources();
      }

      return true;
    }

    public void setShowResources(boolean showResources)
    {
      this.showResources = showResources;
    }

    public boolean isShowResources()
    {
      return showResources;
    }
  }
}
