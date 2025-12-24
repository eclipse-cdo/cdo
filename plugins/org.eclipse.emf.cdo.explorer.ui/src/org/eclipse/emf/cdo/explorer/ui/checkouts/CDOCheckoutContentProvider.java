/*
 * Copyright (c) 2015, 2016, 2018-2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.checkouts;

import org.eclipse.emf.cdo.CDOElement;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.explorer.CDOExplorerManager;
import org.eclipse.emf.cdo.explorer.CDOExplorerManager.ElementsChangedEvent;
import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckoutManager;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckoutManager.CheckoutStateEvent;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.explorer.ui.checkouts.actions.OpenWithActionProvider;
import org.eclipse.emf.cdo.internal.ui.CDOContentProvider;
import org.eclipse.emf.cdo.internal.ui.RunnableViewerRefresh;
import org.eclipse.emf.cdo.internal.ui.ViewerUtil;
import org.eclipse.emf.cdo.ui.CDOItemProvider;

import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.om.pref.OMPreferencesChangeEvent;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.ICommonContentProvider;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author Eike Stepper
 */
public class CDOCheckoutContentProvider extends CDOContentProvider<CDOCheckout> implements ICommonContentProvider, IPropertySourceProvider, IOpenListener
{
  public static final String PROJECT_EXPLORER_ID = "org.eclipse.ui.navigator.ProjectExplorer";

  private static final Map<String, CDOCheckoutContentProvider> INSTANCES = new HashMap<>();

  private static final CDOCheckoutManager CHECKOUT_MANAGER = CDOExplorerUtil.getCheckoutManager();

  private final IListener checkoutManagerListener = new IListener()
  {
    @Override
    public void notifyEvent(IEvent event)
    {
      RunnableViewerRefresh viewerRefresh = stateManager.getViewerRefresh();

      if (event instanceof IContainerEvent)
      {
        viewerRefresh.addNotification(null, true, true);
      }
      else if (event instanceof CheckoutStateEvent)
      {
        CheckoutStateEvent e = (CheckoutStateEvent)event;
        CDOCheckout checkout = e.getCheckout();
        CDOCheckout.State state = e.getNewState();

        if (state == CDOCheckout.State.Opening)
        {
          // Trigger hasChildren().
          ViewerUtil.refresh(getViewer(), checkout);

          // Trigger getChildren().
          ViewerUtil.expand(getViewer(), checkout, true);
        }
        else
        {
          if (state == CDOCheckout.State.Closed)
          {
            ViewerUtil.expand(getViewer(), checkout, false);
          }

          viewerRefresh.addNotification(checkout, true, true);

          if (state == CDOCheckout.State.Open)
          {
            ViewerUtil.expand(getViewer(), checkout, true);
          }

          updatePropertySheetPage(checkout);
        }
      }
      else if (event instanceof CDOExplorerManager.ElementsChangedEvent)
      {
        CDOExplorerManager.ElementsChangedEvent e = (CDOExplorerManager.ElementsChangedEvent)event;
        ElementsChangedEvent.StructuralImpact structuralImpact = e.getStructuralImpact();
        Object[] changedElements = e.getChangedElements();

        if (structuralImpact != ElementsChangedEvent.StructuralImpact.NONE && changedElements.length == 1)
        {
          Object changedElement = changedElements[0];
          if (changedElement instanceof CDOElement)
          {
            changedElement = ((CDOElement)changedElement).getParent();
          }

          if (structuralImpact == ElementsChangedEvent.StructuralImpact.PARENT)
          {
            changedElement = CDOExplorerUtil.getParent(changedElement);
          }

          viewerRefresh.addNotification(changedElement, true, true);
        }
        else
        {
          for (Object changedElement : changedElements)
          {
            viewerRefresh.addNotification(changedElement, false, true);
          }
        }

        updatePropertySheetPage(changedElements);
      }
    }

    private void updatePropertySheetPage(final Object element)
    {
      IWorkbenchPage workbenchPage = getWorkbenchPage();

      PropertySheet propertySheet = getPropertySheet(workbenchPage);
      if (propertySheet != null)
      {
        IPage currentPage = propertySheet.getCurrentPage();
        if (currentPage instanceof PropertySheetPage || currentPage instanceof TabbedPropertySheetPage)
        {
          TreeViewer viewer = getViewer();
          Control control = viewer.getControl();
          if (!control.isDisposed())
          {
            control.getDisplay().asyncExec(new Runnable()
            {
              @Override
              public void run()
              {
                if (!control.isDisposed())
                {
                  IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
                  if (selection.size() == 1)
                  {
                    for (Object object : selection.toArray())
                    {
                      if (object == element)
                      {
                        if (currentPage instanceof PropertySheetPage)
                        {
                          ((PropertySheetPage)currentPage).refresh();
                        }
                        else if (currentPage instanceof TabbedPropertySheetPage)
                        {
                          TabbedPropertySheetPage page = (TabbedPropertySheetPage)currentPage;
                          if (page.getCurrentTab() != null)
                          {
                            page.refresh();
                          }
                        }

                        return;
                      }
                    }
                  }
                }
              }
            });
          }
        }
      }
    }

    private PropertySheet getPropertySheet(IWorkbenchPage workbenchPage)
    {
      for (IViewReference viewReference : workbenchPage.getViewReferences())
      {
        if ("org.eclipse.ui.views.PropertySheet".equals(viewReference.getId()))
        {
          IViewPart view = viewReference.getView(false);
          if (view instanceof PropertySheet)
          {
            return (PropertySheet)view;

          }
        }
      }

      return null;
    }
  };

  private final CDOCheckoutStateManager stateManager = new CDOCheckoutStateManager(this);

  private String viewerID;

  public CDOCheckoutContentProvider()
  {
  }

  @Override
  public void init(ICommonContentExtensionSite config)
  {
    viewerID = config.getService().getViewerId();

    synchronized (INSTANCES)
    {
      INSTANCES.put(viewerID, this);
    }

    CHECKOUT_MANAGER.addListener(checkoutManagerListener);
  }

  @Override
  public void saveState(IMemento aMemento)
  {
    // Do nothing.
  }

  @Override
  public void restoreState(IMemento aMemento)
  {
    // Do nothing.
  }

  @Override
  public void dispose()
  {
    CHECKOUT_MANAGER.removeListener(checkoutManagerListener);

    synchronized (INSTANCES)
    {
      INSTANCES.remove(viewerID);
    }
  }

  public void disposeWith(Control control)
  {
    control.addDisposeListener(e -> dispose());
  }

  public final CDOCheckoutStateManager getStateManager()
  {
    return stateManager;
  }

  @Override
  public void inputChanged(Viewer newViewer, Object oldInput, Object newInput)
  {
    super.inputChanged(newViewer, oldInput, newInput);
    stateManager.inputChanged(getViewer(), oldInput, newInput);
  }

  @Override
  public boolean hasChildren(Object object)
  {
    if (object instanceof IResource)
    {
      if (object instanceof IWorkspaceRoot)
      {
        return !CHECKOUT_MANAGER.isEmpty();
      }

      return false;
    }

    return super.hasChildren(object);
  }

  @Override
  public Object[] getChildren(Object object)
  {
    if (object instanceof IResource)
    {
      if (object instanceof IWorkspaceRoot)
      {
        return CHECKOUT_MANAGER.getCheckouts();
      }

      return ViewerUtil.NO_CHILDREN;
    }

    return super.getChildren(object);
  }

  @Override
  public Object getParent(Object object)
  {
    if (object instanceof CDOCheckout)
    {
      return ResourcesPlugin.getWorkspace().getRoot();
    }

    return super.getParent(object);
  }

  @Override
  public IPropertySource getPropertySource(Object object)
  {
    IPropertySourceProvider contentProvider = stateManager.getContentProvider(object);
    if (contentProvider != null)
    {
      return contentProvider.getPropertySource(object);
    }

    return null;
  }

  public void selectObjects(final Object... objects)
  {
    TreeViewer viewer = getViewer();
    Control control = viewer.getControl();
    if (!control.isDisposed())
    {
      final long end = System.currentTimeMillis() + 5000L;
      final Display display = control.getDisplay();
      display.asyncExec(new Runnable()
      {
        @Override
        public void run()
        {
          if (control.isDisposed())
          {
            return;
          }

          for (Object object : objects)
          {
            LinkedList<Object> path = new LinkedList<>();
            CDOCheckout checkout = CDOExplorerUtil.walkUp(object, path);
            if (checkout != null)
            {
              viewer.setExpandedState(checkout, true);

              if (!path.isEmpty())
              {
                path.removeFirst();
              }

              if (!path.isEmpty())
              {
                path.removeLast();
              }

              for (Object parent : path)
              {
                viewer.setExpandedState(parent, true);
              }
            }
          }

          viewer.setSelection(new StructuredSelection(objects), true);
          IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();

          Set<Object> actual = new HashSet<>(Arrays.asList(selection.toArray()));
          Set<Object> expected = new HashSet<>(Arrays.asList(objects));

          if (!actual.equals(expected))
          {
            if (isObjectLoading(objects) || System.currentTimeMillis() < end)
            {
              display.timerExec(50, this);
            }
          }
        }
      });
    }
  }

  @Override
  public void open(OpenEvent event)
  {
    ISelection selection = event.getSelection();
    if (selection instanceof IStructuredSelection)
    {
      IStructuredSelection ssel = (IStructuredSelection)selection;
      if (ssel.size() == 1)
      {
        Object element = ssel.getFirstElement();
        if (element instanceof CDOCheckout)
        {
          final CDOCheckout checkout = (CDOCheckout)element;
          if (checkout.getState() == CDOCheckout.State.Closed)
          {
            // Simulate CheckoutOpenHandler.
            new Job("Open " + checkout.getLabel())
            {
              @Override
              protected IStatus run(IProgressMonitor monitor)
              {
                try
                {
                  checkout.open();
                }
                catch (Exception ex)
                {
                  OM.LOG.error(ex);
                }

                return Status.OK_STATUS;
              }
            }.schedule();
          }
        }
        else if (element instanceof CDOResourceNode)
        {
          // Do nothing special.
        }
        else if (element instanceof EObject)
        {
          EObject eObject = (EObject)element;

          Shell shell = getWorkbenchPage().getWorkbenchWindow().getShell();
          ComposedAdapterFactory adapterFactory = stateManager.getAdapterFactory(eObject);
          OpenWithActionProvider.editObject(shell, adapterFactory, eObject);
        }
      }
    }
  }

  @Override
  protected void hookViewer(TreeViewer viewer)
  {
    viewer.addOpenListener(this);
  }

  @Override
  protected void unhookViewer(TreeViewer viewer)
  {
    viewer.removeOpenListener(this);
  }

  @Override
  protected Object adapt(Object target, Object type)
  {
    return stateManager.adapt(target, type);
  }

  @Override
  protected Object[] modifyChildren(Object parent, Object[] children)
  {
    return CDOCheckoutContentModifier.Registry.INSTANCE.modifyChildren(parent, children);
  }

  @Override
  protected ITreeContentProvider getContentProvider(Object object)
  {
    return stateManager.getContentProvider(object);
  }

  @Override
  protected RunnableViewerRefresh getViewerRefresh()
  {
    return stateManager.getViewerRefresh();
  }

  @Override
  protected boolean isContext(Object object)
  {
    return object instanceof CDOCheckout;
  }

  @Override
  protected ContextState getContextState(CDOCheckout checkout)
  {
    switch (checkout.getState())
    {
    case Closing:
    case Closed:
      return ContextState.Closed;

    case Opening:
      return ContextState.Opening;

    case Open:
      return ContextState.Open;

    default:
      throw new IllegalStateException("Unexpected checkout state: " + checkout);
    }
  }

  @Override
  protected void openContext(CDOCheckout checkout)
  {
    checkout.open();
  }

  @Override
  protected void closeContext(CDOCheckout checkout)
  {
    checkout.close();
  }

  @Override
  protected Object getRootObject(CDOCheckout checkout)
  {
    return checkout.getRootObject();
  }

  private IWorkbenchPage getWorkbenchPage()
  {
    TreeViewer viewer = getViewer();
    if (viewer instanceof CommonViewer)
    {
      CommonViewer commonViewer = (CommonViewer)viewer;
      return commonViewer.getCommonNavigator().getSite().getPage();
    }

    return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
  }

  public static TreeViewer createTreeViewer(Composite container, Predicate<Object> contentPredicate)
  {
    return createTreeViewer(container, SWT.BORDER, contentPredicate);
  }

  public static TreeViewer createTreeViewer(Composite container, int style, Predicate<Object> contentPredicate)
  {
    // TODO This is not lazy, async:
    CDOItemProvider parentItemProvider = new CDOItemProvider(null)
    {
      @Override
      public boolean hasChildren(Object element)
      {
        return getChildren(element).length != 0;
      }

      @Override
      public Object[] getChildren(Object element)
      {
        List<Object> children = new ArrayList<>();
        for (Object child : doGetChildren(element))
        {
          if (contentPredicate.test(child))
          {
            children.add(child);
          }
        }

        return children.toArray();
      }

      private Object[] doGetChildren(Object element)
      {
        if (element instanceof CDOCheckout)
        {
          CDOCheckout checkout = (CDOCheckout)element;
          if (checkout.isOpen())
          {
            return checkout.getRootObject().eContents().toArray();
          }
        }

        return super.getChildren(element);
      }

      @Override
      public void fillContextMenu(IMenuManager manager, ITreeSelection selection)
      {
        // Do nothing.
      }
    };

    CDOCheckoutContentProvider contentProvider = new CDOCheckoutContentProvider();
    contentProvider.disposeWith(container);

    CDOCheckoutLabelProvider labelProvider = new CDOCheckoutLabelProvider(contentProvider);

    TreeViewer parentViewer = new TreeViewer(container, style);
    parentViewer.setContentProvider(parentItemProvider);
    parentViewer.setLabelProvider(labelProvider);
    parentViewer.setInput(CDOExplorerUtil.getCheckoutManager());
    return parentViewer;
  }

  public static TreeViewer createTreeViewer(Composite container)
  {
    return createTreeViewer(container, child -> child instanceof CDOCheckout || child instanceof CDOResourceFolder);
  }

  public static CDOCheckoutContentProvider getInstance(String viewerID)
  {
    synchronized (INSTANCES)
    {
      return INSTANCES.get(viewerID);
    }
  }

  public static void forEachInstance(Consumer<CDOCheckoutContentProvider> consumer)
  {
    CDOCheckoutContentProvider[] contentProviders;
    synchronized (INSTANCES)
    {
      contentProviders = INSTANCES.values().toArray(new CDOCheckoutContentProvider[INSTANCES.size()]);
    }

    for (CDOCheckoutContentProvider contentProvider : contentProviders)
    {
      consumer.accept(contentProvider);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class FromPreferences extends CDOCheckoutContentProvider
  {
    private final IListener preferenceListener = new IListener()
    {
      @Override
      public void notifyEvent(IEvent event)
      {
        @SuppressWarnings("unchecked")
        OMPreferencesChangeEvent<Boolean> preferenceChangeEvent = (OMPreferencesChangeEvent<Boolean>)event;
        if (OM.PREF_SHOW_OBJECTS_IN_EXPLORER.getName().equals(preferenceChangeEvent.getPreference().getName()))
        {
          setHideObjects(!preferenceChangeEvent.getNewValue());
        }
      }
    };

    public FromPreferences()
    {
      setHideObjects(!OM.PREF_SHOW_OBJECTS_IN_EXPLORER.getValue());
      OM.PREFS.addListener(preferenceListener);
    }

    @Override
    public void dispose()
    {
      OM.PREFS.removeListener(preferenceListener);
      super.dispose();
    }
  }
}
