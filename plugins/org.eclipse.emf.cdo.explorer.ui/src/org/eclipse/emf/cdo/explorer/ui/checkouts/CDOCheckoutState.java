/*
 * Copyright (c) 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.checkouts;

import org.eclipse.emf.cdo.CDOElement;
import org.eclipse.emf.cdo.eresource.CDOResourceLeaf;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckoutManager;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.ui.ViewerUtil;
import org.eclipse.emf.cdo.internal.ui.editor.CDOEditor;
import org.eclipse.emf.cdo.ui.CDOEditorUtil;
import org.eclipse.emf.cdo.ui.CDOLabelDecorator;
import org.eclipse.emf.cdo.ui.CDOTreeExpansionAgent;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ViewerNotification;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider.ViewerRefresh;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;

import org.eclipse.core.commands.common.EventManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * @author Eike Stepper
 */
public final class CDOCheckoutState
{
  private static final Image CHECKOUT_IMAGE = OM.getImage("icons/checkout.gif");

  private static final Image CHECKOUT_CLOSED_IMAGE = OM.getImage("icons/checkout_closed.gif");

  private static final Image FOLDER_IMAGE = OM.getImage("icons/CDOResourceFolder.gif");

  private static final Image ERROR_IMAGE = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);

  private static final IEditorRegistry EDITOR_REGISTRY = PlatformUI.getWorkbench().getEditorRegistry();

  private final IListener checkoutManagerListener = new IListener()
  {
    @Override
    public void notifyEvent(IEvent event)
    {
      if (event instanceof CDOCheckoutManager.CheckoutStateEvent)
      {
        CDOCheckoutManager.CheckoutStateEvent e = (CDOCheckoutManager.CheckoutStateEvent)event;
        if (e.getCheckout() == checkout)
        {
          CDOCheckout.State state = e.getNewState();
          if (state == CDOCheckout.State.Open)
          {
            initTreeExpansionAgent();
          }
          else if (state == CDOCheckout.State.Closed)
          {
            disposeTreeExpansionAgent();
          }
        }
      }
    }
  };

  private final EventBroker eventBroker = new EventBroker();

  private final CDOCheckoutStateManager stateManager;

  private final CDOCheckout checkout;

  private final ComposedAdapterFactory adapterFactory;

  private final ContentProvider contentProvider;

  private final LabelProvider labelProvider;

  private CDOTreeExpansionAgent treeExpansionAgent;

  CDOCheckoutState(CDOCheckoutStateManager stateManager, CDOCheckout checkout)
  {
    this.stateManager = stateManager;
    this.checkout = checkout;

    adapterFactory = CDOEditor.createAdapterFactory(true);
    ViewerRefresh viewerRefresh = stateManager.getViewerRefresh();

    CDOCheckoutContentProvider main = stateManager.getMainContentProvider();
    contentProvider = new ContentProvider(this, viewerRefresh);
    contentProvider.inputChanged(main.getViewer(), null, main.getInput());

    ResourceManager resourceManager = stateManager.getResourceManager();
    labelProvider = new LabelProvider(this, resourceManager);
    labelProvider.addListener(eventBroker);

    initTreeExpansionAgent();
    CDOExplorerUtil.getCheckoutManager().addListener(checkoutManagerListener);
  }

  void inputChanged(TreeViewer newTreeViewer, Object oldInput, Object newInput)
  {
    contentProvider.inputChanged(newTreeViewer, oldInput, newInput);

    if (treeExpansionAgent != null)
    {
      if (newTreeViewer != treeExpansionAgent.getViewer())
      {
        disposeTreeExpansionAgent();
      }
    }

    if (newTreeViewer != null)
    {
      initTreeExpansionAgent();
    }
  }

  public CDOCheckout getCheckout()
  {
    return checkout;
  }

  public ComposedAdapterFactory getAdapterFactory()
  {
    return adapterFactory;
  }

  public ContentProvider getContentProvider()
  {
    return contentProvider;
  }

  public LabelProvider getLabelProvider()
  {
    return labelProvider;
  }

  public void addListener(ILabelProviderListener listener)
  {
    eventBroker.addListener(listener);
  }

  public void removeListener(ILabelProviderListener listener)
  {
    eventBroker.removeListener(listener);
  }

  public void dispose()
  {
    CDOExplorerUtil.getCheckoutManager().removeListener(checkoutManagerListener);
    disposeTreeExpansionAgent();

    labelProvider.dispose();
    contentProvider.dispose();
    adapterFactory.dispose();
    eventBroker.dispose();
  }

  private void disposeTreeExpansionAgent()
  {
    if (treeExpansionAgent != null)
    {
      treeExpansionAgent.dispose();
      treeExpansionAgent = null;
    }
  }

  private void initTreeExpansionAgent()
  {
    if (treeExpansionAgent == null)
    {
      CDOView view = checkout.getView();
      if (view != null)
      {
        TreeViewer viewer = stateManager.getMainContentProvider().getViewer();
        if (viewer != null)
        {
          treeExpansionAgent = new CDOTreeExpansionAgent(view, viewer);
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class EventBroker extends EventManager implements ILabelProviderListener
  {
    public void addListener(ILabelProviderListener listener)
    {
      addListenerObject(listener);
    }

    public void removeListener(ILabelProviderListener listener)
    {
      removeListenerObject(listener);
    }

    @Override
    public void labelProviderChanged(final LabelProviderChangedEvent event)
    {
      Object[] listeners = getListeners();
      for (int i = 0; i < listeners.length; ++i)
      {
        final ILabelProviderListener listener = (ILabelProviderListener)listeners[i];
        SafeRunnable.run(new SafeRunnable()
        {
          @Override
          public void run()
          {
            listener.labelProviderChanged(event);
          }
        });
      }
    }

    public void dispose()
    {
      clearListeners();
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class ContentProvider extends AdapterFactoryContentProvider
  {
    private final CDOCheckoutState checkoutState;

    public ContentProvider(CDOCheckoutState checkoutState, ViewerRefresh viewerRefresh)
    {
      super(checkoutState.getAdapterFactory());
      this.checkoutState = checkoutState;
      this.viewerRefresh = viewerRefresh;
    }

    @Override
    public Object[] getElements(Object object)
    {
      synchronized (checkoutState)
      {
        return super.getElements(object);
      }
    }

    @Override
    public Object[] getChildren(Object object)
    {
      synchronized (checkoutState)
      {
        return super.getChildren(object);
      }
    }

    @Override
    public boolean hasChildren(Object object)
    {
      synchronized (checkoutState)
      {
        return super.hasChildren(object);
      }
    }

    @Override
    public Object getParent(Object object)
    {
      synchronized (checkoutState)
      {
        return super.getParent(object);
      }
    }

    @Override
    public void notifyChanged(Notification notification)
    {
      Object notifier = notification.getNotifier();
      if (notifier instanceof EObject)
      {
        EObject eObject = (EObject)notifier;

        Object feature = notification.getFeature();
        if (feature instanceof EReference)
        {
          EReference reference = (EReference)feature;
          if (reference.isContainment())
          {
            Adapter adapter = EcoreUtil.getAdapter(eObject.eAdapters(), CDOCheckout.class);
            if (adapter instanceof CDOCheckout)
            {
              CDOCheckout checkout = (CDOCheckout)adapter;
              if (checkout.isOpen())
              {
                notification = new ViewerNotification(notification, checkout, true, true);
              }
            }
          }
        }
      }

      super.notifyChanged(notification);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class LabelProvider extends AdapterFactoryLabelProvider implements IColorProvider, IStyledLabelProvider
  {
    private final CDOCheckoutState checkoutState;

    private final ResourceManager resourceManager;

    public LabelProvider(CDOCheckoutState checkoutState, ResourceManager resourceManager)
    {
      super(checkoutState.getAdapterFactory());
      this.checkoutState = checkoutState;
      this.resourceManager = resourceManager;
    }

    @Override
    public Color getForeground(Object object)
    {
      synchronized (checkoutState)
      {
        if (object instanceof ViewerUtil.Pending)
        {
          return ContainerItemProvider.PENDING_COLOR;
        }

        return super.getForeground(object);
      }
    }

    @Override
    public Color getBackground(Object object)
    {
      synchronized (checkoutState)
      {
        return super.getBackground(object);
      }
    }

    @Override
    public Font getFont(Object object)
    {
      synchronized (checkoutState)
      {
        return super.getFont(object);
      }
    }

    @Override
    public String getText(Object element)
    {
      synchronized (checkoutState)
      {
        try
        {
          if (element instanceof CDOCheckout)
          {
            CDOCheckout checkout = (CDOCheckout)element;
            String text = checkout.getLabel();
            if (!StringUtil.isEmpty(text))
            {
              return text;
            }
          }

          if (element instanceof CDOElement)
          {
            CDOElement checkoutElement = (CDOElement)element;
            String text = checkoutElement.toString();
            if (!StringUtil.isEmpty(text))
            {
              return text;
            }
          }

          if (element instanceof EObject)
          {
            CDOElement checkoutElement = (CDOElement)EcoreUtil.getExistingAdapter((Notifier)element, CDOElement.class);
            if (checkoutElement != null)
            {
              String text = checkoutElement.toString(element);
              if (!StringUtil.isEmpty(text))
              {
                return text;
              }
            }

            if (element instanceof CDOResourceNode)
            {
              CDOResourceNode resourceNode = (CDOResourceNode)element;

              String name = resourceNode.getName();
              if (name == null)
              {
                // This must be the root resource.
                return "";
              }

              return name;
            }
          }

          if (element instanceof ViewerUtil.Pending)
          {
            ViewerUtil.Pending pending = (ViewerUtil.Pending)element;
            return pending.getText();
          }

          String text = super.getText(element);
          if (!StringUtil.isEmpty(text))
          {
            return text;
          }
        }
        catch (Exception ex)
        {
          //$FALL-THROUGH$
        }

        try
        {
          if (element instanceof EObject)
          {
            EObject eObject = (EObject)element;
            EClass eClass = eObject.eClass();
            String text = getText(eClass);
            if (!StringUtil.isEmpty(text))
            {
              return text;
            }
          }
        }
        catch (Exception ignore)
        {
          //$FALL-THROUGH$
        }

        return element.getClass().getSimpleName();
      }
    }

    @Override
    public Image getImage(Object element)
    {
      synchronized (checkoutState)
      {
        try
        {
          if (element instanceof CDOCheckout)
          {
            CDOCheckout checkout = (CDOCheckout)element;
            if (checkout.isOpen())
            {
              return CDOLabelDecorator.decorate(CHECKOUT_IMAGE, checkout.getRootObject());
            }

            return CHECKOUT_CLOSED_IMAGE;
          }

          if (element instanceof ViewerUtil.Pending)
          {
            return ContainerItemProvider.PENDING_IMAGE;
          }

          if (element instanceof CDOElement)
          {
            element = ((CDOElement)element).getDelegate();
            Image image = doGetImage(element);
            return CDOLabelDecorator.decorate(image, element);
          }

          Image image = doGetImage(element);
          if (image == null)
          {
            return ERROR_IMAGE;
          }

          return image;
        }
        catch (Exception ex)
        {
          return ERROR_IMAGE;
        }
      }
    }

    private Image doGetImage(Object element)
    {
      if (element instanceof CDOResourceLeaf)
      {
        String name = ((CDOResourceLeaf)element).getName();
        if (name == null)
        {
          // This must be the root resource.
          return FOLDER_IMAGE;
        }

        IEditorDescriptor editorDescriptor = EDITOR_REGISTRY.getDefaultEditor(name);
        if (editorDescriptor != null && !CDOEditorUtil.TEXT_EDITOR_ID.equals(editorDescriptor.getId()))
        {
          Image image = getWorkbenchImage(name);
          if (image != null)
          {
            return image;
          }
        }
      }

      return super.getImage(element);
    }

    private Image getWorkbenchImage(String name)
    {
      ImageDescriptor imageDescriptor = EDITOR_REGISTRY.getImageDescriptor(name);
      if (imageDescriptor != null)
      {
        return (Image)resourceManager.get(imageDescriptor);
      }

      return null;
    }
  }
}
