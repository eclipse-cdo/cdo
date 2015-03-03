/*
 * Copyright (c) 2004-2014 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.ui.ViewerUtil;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.ui.editor.CDOEditor;
import org.eclipse.emf.cdo.ui.CDOEditorUtil;
import org.eclipse.emf.cdo.ui.CDOLabelDecorator;

import org.eclipse.net4j.util.ui.views.ContainerItemProvider;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
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
import org.eclipse.swt.graphics.Color;
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

  private static final Image ERROR_IMAGE = PlatformUI.getWorkbench().getSharedImages()
      .getImage(ISharedImages.IMG_OBJS_ERROR_TSK);

  private static final IEditorRegistry EDITOR_REGISTRY = PlatformUI.getWorkbench().getEditorRegistry();

  private final EventBroker eventBroker = new EventBroker();

  private final CDOCheckout checkout;

  private final ComposedAdapterFactory adapterFactory;

  private final ContentProvider contentProvider;

  private final LabelProvider labelProvider;

  CDOCheckoutState(CDOCheckoutStateManager stateManager, CDOCheckout checkout)
  {
    this.checkout = checkout;

    adapterFactory = CDOEditor.createAdapterFactory(true);
    ViewerRefresh viewerRefresh = stateManager.getViewerRefresh();

    CDOCheckoutContentProvider main = stateManager.getMainContentProvider();
    contentProvider = new ContentProvider(adapterFactory, viewerRefresh);
    contentProvider.inputChanged(main.getViewer(), null, main.getInput());

    ResourceManager resourceManager = stateManager.getResourceManager();
    labelProvider = new LabelProvider(adapterFactory, resourceManager);
    labelProvider.addListener(eventBroker);
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
    labelProvider.dispose();
    contentProvider.dispose();
    adapterFactory.dispose();
    eventBroker.dispose();
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

    public void labelProviderChanged(final LabelProviderChangedEvent event)
    {
      Object[] listeners = getListeners();
      for (int i = 0; i < listeners.length; ++i)
      {
        final ILabelProviderListener listener = (ILabelProviderListener)listeners[i];
        SafeRunnable.run(new SafeRunnable()
        {
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
    public ContentProvider(AdapterFactory adapterFactory, ViewerRefresh viewerRefresh)
    {
      super(adapterFactory);
      this.viewerRefresh = viewerRefresh;
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
  public static final class LabelProvider extends AdapterFactoryLabelProvider implements IColorProvider,
      IStyledLabelProvider
  {
    private final ResourceManager resourceManager;

    public LabelProvider(AdapterFactory adapterFactory, ResourceManager resourceManager)
    {
      super(adapterFactory);
      this.resourceManager = resourceManager;
    }

    @Override
    public Color getForeground(Object object)
    {
      if (object instanceof ViewerUtil.Pending)
      {
        return ContainerItemProvider.PENDING_COLOR;
      }

      return super.getForeground(object);
    }

    @Override
    public String getText(Object element)
    {
      try
      {
        if (element instanceof CDOCheckout)
        {
          CDOCheckout checkout = (CDOCheckout)element;
          return checkout.getLabel();
        }

        if (element instanceof CDOElement)
        {
          CDOElement checkoutElement = (CDOElement)element;
          return checkoutElement.toString();
        }

        if (element instanceof EObject)
        {
          CDOElement checkoutElement = (CDOElement)EcoreUtil.getExistingAdapter((Notifier)element, CDOElement.class);
          if (checkoutElement != null)
          {
            return checkoutElement.toString(element);
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

        return super.getText(element);
      }
      catch (Exception ex)
      {
        return ex.getMessage();
      }
    }

    @Override
    public Image getImage(Object element)
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

        return doGetImage(element);
      }
      catch (Exception ex)
      {
        return ERROR_IMAGE;
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
