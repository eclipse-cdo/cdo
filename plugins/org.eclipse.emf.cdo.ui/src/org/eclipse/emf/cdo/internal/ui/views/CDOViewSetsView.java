/*
 * Copyright (c) 2009-2012, 2015, 2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.views;

import org.eclipse.emf.cdo.ui.CDOItemProvider;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewProvider;
import org.eclipse.emf.cdo.view.CDOViewProvider.CDOViewProvider2;
import org.eclipse.emf.cdo.view.CDOViewSet;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ContainerView;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Eike Stepper
 */
public class CDOViewSetsView extends ContainerView.Default<IContainer<CDOViewSet>>
{
  public static final String ID = "org.eclipse.emf.cdo.ui.CDOViewSetsView"; //$NON-NLS-1$

  public CDOViewSetsView()
  {
    setContainer(CDOViewSet.REGISTRY);
  }

  @Override
  public void dispose()
  {
    // getSite().getWorkbenchWindow().getSelectionService().removePostSelectionListener(selectionListener);
    super.dispose();
  }

  @Override
  protected ContainerItemProvider<IContainer<Object>> createContainerItemProvider()
  {
    return new CDOItemProvider(getSite().getPage())
    {
      @Override
      protected void elementAdded(Object element, Object parent)
      {
        super.elementAdded(element, parent);

        if (element instanceof CDOViewSet)
        {
          expandElement(element, 1);
        }
      }

      @Override
      public String getText(Object obj)
      {
        if (obj instanceof CDOView)
        {
          CDOView view = (CDOView)obj;

          StringBuilder builder = new StringBuilder();

          CDOViewProvider provider = view.getProvider();
          if (provider != null)
          {
            if (provider instanceof CDOViewProvider2)
            {
              CDOViewProvider2 provider2 = (CDOViewProvider2)provider;
              builder.append(provider2.getViewURI(view));
            }
            else
            {
              builder.append(provider.getRegex());
            }

            builder.append(" -> ");
          }

          builder.append(view);
          builder.append(" -> ");
          builder.append(view.getSession());
          return builder.toString();
        }

        return super.getText(obj);
      }
    };
  }

  @Override
  @SuppressWarnings("deprecation")
  protected org.eclipse.jface.viewers.ViewerSorter createViewerSorter()
  {
    return null;
  }

  @Override
  protected Control createUI(Composite parent)
  {
    Control control = super.createUI(parent);
    // getViewer().getControl().setEnabled(false);
    // getSite().getWorkbenchWindow().getSelectionService().addPostSelectionListener(selectionListener);
    return control;
  }
}
