/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.ui.properties;

import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.lm.client.IAssemblyDescriptor;
import org.eclipse.emf.cdo.lm.client.IAssemblyDescriptor.AvailableUpdatesChangedEvent;
import org.eclipse.emf.cdo.lm.client.IAssemblyDescriptor.UpdateStateChangedEvent;
import org.eclipse.emf.cdo.lm.client.IAssemblyManager;
import org.eclipse.emf.cdo.lm.modules.DependencyDefinition;
import org.eclipse.emf.cdo.lm.ui.bundle.OM;
import org.eclipse.emf.cdo.lm.ui.views.AssembliesView;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.IElementFilter;

import org.eclipse.emf.common.util.EList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PropertyPage;

/**
 * @author Eike Stepper
 */
public class ModulePropertyPage extends PropertyPage
{
  private IAssemblyDescriptor assemblyDescriptor;

  private ContainerItemProvider<IContainer<Object>> itemProvider;

  private TreeViewer viewer;

  private Font bold;

  private Button installUpdatesButton;

  public ModulePropertyPage()
  {
    noDefaultAndApplyButton();
  }

  @Override
  public void dispose()
  {
    UIUtil.dispose(bold);
    super.dispose();
  }

  @Override
  protected Control createContents(Composite parent)
  {
    bold = UIUtil.getBoldFont(parent);
    Display display = parent.getDisplay();

    Composite container = new Composite(parent, SWT.NULL);
    GridLayout containerGridLayout = new GridLayout();
    container.setLayout(containerGridLayout);

    Composite composite = new Composite(container, SWT.NONE);
    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    composite.setLayout(new FillLayout());

    assemblyDescriptor = IAssemblyManager.INSTANCE.getDescriptor((CDOCheckout)getElement());
    IElementFilter rootElementFilter = element -> element == assemblyDescriptor;

    itemProvider = new ContainerItemProvider<>(rootElementFilter)
    {
      @Override
      protected void handleElementEvent(IEvent event)
      {
        if (event instanceof UpdateStateChangedEvent)
        {
          UIUtil.asyncExec(() -> updateStateChanged());
        }
        else if (event instanceof AvailableUpdatesChangedEvent)
        {
          UIUtil.asyncExec(() -> availableUpdatesChanged());
        }
      }

      @Override
      protected Object[] getContainerChildren(ContainerItemProvider<IContainer<Object>>.AbstractContainerNode containerNode, IContainer<?> container)
      {
        Object[] children = super.getContainerChildren(containerNode, container);
        children = AssembliesView.addNewModules(container, children);
        return children;
      }

      @Override
      protected Node createNode(Node parent, Object element)
      {
        EList<DependencyDefinition> dependencies = AssembliesView.getDependencies(parent, element);
        if (dependencies != null)
        {
          return new FixedChildrenNode(parent, element, dependencies);
        }

        return super.createNode(parent, element);
      }

      @Override
      public String getText(Object element)
      {
        String text = AssembliesView.getText(element);
        if (text != null)
        {
          return text;
        }

        return super.getText(element);
      }

      @Override
      public Image getImage(Object element)
      {
        Image image = AssembliesView.getImage(element);
        if (image != null)
        {
          return image;
        }

        return super.getImage(element);
      }

      @Override
      public Font getFont(Object element)
      {
        Font font = AssembliesView.getFont(element, bold, this);
        if (font != null)
        {
          return font;
        }

        return super.getFont(element);
      }

      @Override
      public Color getForeground(Object element)
      {
        Color foreground = AssembliesView.getForeground(element, display);
        if (foreground != null)
        {
          return foreground;
        }

        return super.getForeground(element);
      }
    };

    viewer = new TreeViewer(composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
    viewer.setContentProvider(itemProvider);
    viewer.setLabelProvider(new DecoratingStyledCellLabelProvider(itemProvider, PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator(), null));
    viewer.setInput(IAssemblyManager.INSTANCE);
    UIUtil.asyncExec(display, () -> viewer.expandAll());

    installUpdatesButton = new Button(container, SWT.PUSH);
    installUpdatesButton.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
    installUpdatesButton.setText("Install Updates...");
    installUpdatesButton.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
      {
        installUpdates();
      }
    });

    updateStateChanged();

    return container;
  }

  private void updateStateChanged()
  {
    installUpdatesButton.setEnabled(assemblyDescriptor.hasUpdatesAvailable());
  }

  private void availableUpdatesChanged()
  {
    itemProvider.clearNodesCache();
    UIUtil.refreshViewer(viewer);
  }

  private void installUpdates()
  {
    if (installUpdatesButton.isEnabled())
    {
      new Job(installUpdatesButton.getText())
      {
        @Override
        protected IStatus run(IProgressMonitor progressMonitor)
        {
          try
          {
            assemblyDescriptor.update();
            return Status.OK_STATUS;
          }
          catch (Exception ex)
          {
            return new Status(IStatus.ERROR, OM.BUNDLE_ID, ex.getMessage(), ex);
          }
        }
      }.schedule();
    }
  }
}
