/*
 * Copyright (c) 2022-2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.ui.views;

import org.eclipse.emf.cdo.lm.Baseline;
import org.eclipse.emf.cdo.lm.Change;
import org.eclipse.emf.cdo.lm.Delivery;
import org.eclipse.emf.cdo.lm.Drop;
import org.eclipse.emf.cdo.lm.DropType;
import org.eclipse.emf.cdo.lm.FixedBaseline;
import org.eclipse.emf.cdo.lm.Module;
import org.eclipse.emf.cdo.lm.ModuleElement;
import org.eclipse.emf.cdo.lm.Stream;
import org.eclipse.emf.cdo.lm.System;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor.State;
import org.eclipse.emf.cdo.lm.client.ISystemManager;
import org.eclipse.emf.cdo.lm.ui.actions.CheckoutAction;
import org.eclipse.emf.cdo.lm.ui.actions.DeleteChangeAction;
import org.eclipse.emf.cdo.lm.ui.actions.DeleteModuleAction;
import org.eclipse.emf.cdo.lm.ui.actions.NewChangeAction;
import org.eclipse.emf.cdo.lm.ui.actions.NewDeliveryAction;
import org.eclipse.emf.cdo.lm.ui.actions.NewDropAction;
import org.eclipse.emf.cdo.lm.ui.actions.NewModuleAction;
import org.eclipse.emf.cdo.lm.ui.actions.NewStreamAction;
import org.eclipse.emf.cdo.lm.ui.actions.RenameChangeAction;
import org.eclipse.emf.cdo.lm.ui.bundle.OM;
import org.eclipse.emf.cdo.lm.ui.providers.SystemContentProvider;
import org.eclipse.emf.cdo.lm.ui.providers.SystemLabelProvider;
import org.eclipse.emf.cdo.ui.DecoratingStyledLabelProvider;

import org.eclipse.net4j.ui.shared.SharedIcons;
import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.ui.MenuFiller;
import org.eclipse.net4j.util.ui.OpenHandler;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.actions.SafeAction;
import org.eclipse.net4j.util.ui.views.MultiViewersView;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.DelegatingStyledCellLabelProvider;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * @author Eike Stepper
 */
public class SystemsView extends MultiViewersView
{
  public static final String ID = "org.eclipse.emf.cdo.lm.SystemsView";

  protected final ComposedAdapterFactory adapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);

  private final IListener systemManagerListener = new ContainerEventAdapter<ISystemDescriptor>()
  {
    @Override
    protected void onAdded(IContainer<ISystemDescriptor> container, ISystemDescriptor descriptor)
    {
      refreshViewer(ISystemManager.INSTANCE);
    }

    @Override
    protected void onRemoved(IContainer<ISystemDescriptor> container, ISystemDescriptor descriptor)
    {
      refreshViewer(ISystemManager.INSTANCE);
    }

    @Override
    protected void notifyOtherEvent(IEvent event)
    {
      refreshViewer(ISystemManager.INSTANCE);
    }

    private void refreshViewer(Object element)
    {
      if (treeViewer != null)
      {
        UIUtil.refreshElement(treeViewer, element, true);
      }
    }
  };

  private TreeViewer treeViewer;

  public SystemsView()
  {
  }

  @Override
  public void dispose()
  {
    ISystemManager.INSTANCE.removeListener(systemManagerListener);
    adapterFactory.dispose();
    super.dispose();
  }

  @Override
  protected Control createUI(Composite parent)
  {
    treeViewer = new TreeViewer(parent, SWT.NONE);
    treeViewer.setContentProvider(createContentProvider());
    treeViewer.setLabelProvider(createLabelProvider());
    treeViewer.setInput(ISystemManager.INSTANCE);

    ISystemManager.INSTANCE.addListener(systemManagerListener);

    setCurrentViewer(treeViewer);
    return treeViewer.getTree();
  }

  protected ITreeContentProvider createContentProvider()
  {
    return new SystemContentProvider(adapterFactory);
  }

  protected ILabelProvider createLabelProvider()
  {
    ILabelProvider provider = new SystemLabelProvider(adapterFactory, treeViewer);
    ILabelDecorator decorator = createLabelDecorator();
    return new DelegatingStyledCellLabelProvider(new DecoratingStyledLabelProvider(provider, decorator));
  }

  protected ILabelDecorator createLabelDecorator()
  {
    return PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
  }

  @Override
  protected void fillLocalPullDown(IMenuManager manager)
  {
    Action showModuleHistoryAction = new ShowModuleHistoryAction();
    showModuleHistoryAction.setChecked(OM.PREF_SHOW_MODULE_HISTORY.getValue());

    manager.add(showModuleHistoryAction);
    manager.add(new RefreshAction());

    super.fillLocalPullDown(manager);
  }

  @Override
  protected void fillContextMenu(IMenuManager manager, StructuredViewer viewer, IStructuredSelection selection)
  {
    super.fillContextMenu(manager, viewer, selection);
    IWorkbenchPage page = getSite().getPage();

    Object element = selection.getFirstElement();
    if (element instanceof ISystemDescriptor)
    {
      ISystemDescriptor descriptor = (ISystemDescriptor)element;

      if (descriptor.getState() == State.Closed)
      {
        manager.add(new Action("Open")
        {
          @Override
          public void run()
          {
            descriptor.open();
          }
        });
      }
    }
    else if (element instanceof System)
    {
      System system = (System)element;
      manager.add(new NewModuleAction(page, treeViewer, system));

      manager.add(new Action("Close")
      {
        @Override
        public void run()
        {
          ISystemDescriptor descriptor = ISystemManager.INSTANCE.getDescriptor(system);
          descriptor.close();
        }
      });
    }
    else if (element instanceof Module)
    {
      Module module = (Module)element;
      manager.add(new NewStreamAction(page, treeViewer, adapterFactory, module));
      manager.add(new DeleteModuleAction(page, module));
    }
    else if (element instanceof Baseline)
    {
      Baseline baseline = (Baseline)element;

      if (baseline instanceof Delivery || baseline instanceof Drop)
      {
        FixedBaseline fixedBaseline = (FixedBaseline)baseline;
        manager.add(new CheckoutAction(page, baseline));
        manager.add(new Separator());

        Stream stream = fixedBaseline.getStream();
        manager.add(new NewChangeAction(page, treeViewer, stream, fixedBaseline));
        manager.add(new Separator());
      }
      else if (baseline instanceof Stream)
      {
        Stream stream = (Stream)baseline;
        manager.add(new CheckoutAction(page, baseline));
        manager.add(new Separator());
        manager.add(new NewChangeAction(page, treeViewer, stream, null));
        manager.add(new NewDeliveryAction(page, treeViewer, stream, null));
        manager.add(new Separator());

        EList<DropType> possibleDropTypes = stream.getSystem().getProcess().getDropTypes();
        for (DropType dropType : possibleDropTypes)
        {
          manager.add(new NewDropAction(page, treeViewer, stream, dropType));
        }
      }
      else if (baseline instanceof Change)
      {
        Change change = (Change)baseline;
        manager.add(new CheckoutAction(page, baseline));
        manager.add(new Separator());

        if (change.getDeliveries().isEmpty())
        {
          manager.add(new RenameChangeAction(page, change));
          manager.add(new DeleteChangeAction(page, change));
        }

        for (Stream s : change.getModule().getStreams())
        {
          if (change.getDeliveryPoint(s) == null)
          {
            manager.add(new NewDeliveryAction(page, treeViewer, s, change));
          }
        }
      }
    }

    manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

    IPluginContainer.INSTANCE.forEachElement( //
        MenuFiller.Factory.PRODUCT_GROUP, MenuFiller.class, //
        filler -> filler.fillMenu(page, viewer, manager, element));
  }

  @Override
  protected void doubleClicked(Object object)
  {
    if (OpenHandler.Factory.handleOpen(IPluginContainer.INSTANCE, getSite().getPage(), treeViewer, object))
    {
      return;
    }

    if (object instanceof ISystemDescriptor)
    {
      ISystemDescriptor descriptor = (ISystemDescriptor)object;
      if (descriptor.getState() == State.Closed)
      {
        org.eclipse.emf.cdo.lm.internal.client.SystemManager.INSTANCE.scheduleOpenSystem(descriptor);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class ShowModuleHistoryAction extends Action
  {
    public ShowModuleHistoryAction()
    {
      super("Show Module History", IAction.AS_CHECK_BOX);
    }

    @Override
    public void run()
    {
      boolean newValue = !OM.PREF_SHOW_MODULE_HISTORY.getValue();
      OM.PREF_SHOW_MODULE_HISTORY.setValue(newValue);
      setChecked(newValue);

      StructuredViewer viewer = getCurrentViewer();
      IStructuredSelection oldSelection = viewer.getStructuredSelection();
      boolean selectionChanged = false;

      try
      {
        Object element = oldSelection.getFirstElement();
        if (element instanceof ModuleElement || element instanceof Module)
        {
          ISystemDescriptor systemDescriptor = ISystemManager.INSTANCE.getDescriptor((EObject)element);
          if (systemDescriptor != null)
          {
            viewer.setSelection(new StructuredSelection(systemDescriptor.getSystem()));
            selectionChanged = true;
          }
        }
      }
      finally
      {
        if (selectionChanged)
        {
          viewer.setSelection(oldSelection);
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class RefreshAction extends SafeAction
  {
    public RefreshAction()
    {
      super("Refresh", "Refresh the list of recognized systems", SharedIcons.getDescriptor(SharedIcons.ETOOL_REFRESH));
    }

    @Override
    protected void safeRun() throws Exception
    {
      ISystemManager.INSTANCE.refresh();
    }
  }
}
