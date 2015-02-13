/*
 * Copyright (c) 2009-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.checkouts;

import org.eclipse.emf.cdo.eresource.CDOResourceLeaf;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.ui.ViewerUtil;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.ui.editor.CDOEditor;
import org.eclipse.emf.cdo.transfer.CDOTransferElement;
import org.eclipse.emf.cdo.ui.CDOEditorUtil;

import org.eclipse.net4j.util.ui.views.ContainerItemProvider;

import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * @author Eike Stepper
 */
public class CDOCheckoutLabelProvider extends AdapterFactoryLabelProvider implements IColorProvider,
    IStyledLabelProvider
{
  private static final Image CHECKOUT_IMAGE = OM.getImage("icons/checkout.gif");

  private static final Image CHECKOUT_CLOSED_IMAGE = OM.getImage("icons/checkout_closed.gif");

  private static final Image ERROR_IMAGE = PlatformUI.getWorkbench().getSharedImages()
      .getImage(ISharedImages.IMG_OBJS_ERROR_TSK);

  private static final IEditorRegistry EDITOR_REGISTRY = PlatformUI.getWorkbench().getEditorRegistry();

  private final ComposedAdapterFactory adapterFactory;

  private IPropertyListener editorRegistryListener;

  private ResourceManager resourceManager;

  public CDOCheckoutLabelProvider()
  {
    super(null);

    adapterFactory = CDOEditor.createAdapterFactory(true);
    setAdapterFactory(adapterFactory);
  }

  @Override
  public void dispose()
  {
    if (editorRegistryListener != null)
    {
      EDITOR_REGISTRY.removePropertyListener(editorRegistryListener);
      resourceManager = null;
    }

    if (resourceManager != null)
    {
      resourceManager.dispose();
      resourceManager = null;
    }

    super.dispose();

    // Must come after super.dispose().
    adapterFactory.dispose();
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
    if (element instanceof CDOCheckout)
    {
      CDOCheckout checkout = (CDOCheckout)element;
      return checkout.getLabel();
    }

    if (element instanceof CDOResourceNode)
    {
      CDOResourceNode resourceNode = (CDOResourceNode)element;

      try
      {
        return resourceNode.getName();
      }
      catch (Exception ex)
      {
        return ex.getMessage();
      }
    }

    if (element instanceof ViewerUtil.Pending)
    {
      ViewerUtil.Pending pending = (ViewerUtil.Pending)element;
      return pending.getText();
    }

    return super.getText(element);
  }

  @Override
  public Image getImage(Object element)
  {
    if (element instanceof CDOCheckout)
    {
      CDOCheckout checkout = (CDOCheckout)element;
      if (checkout.isOpen())
      {
        return CHECKOUT_IMAGE;
      }

      return CHECKOUT_CLOSED_IMAGE;
    }

    if (element instanceof ViewerUtil.Pending)
    {
      return ContainerItemProvider.PENDING_IMAGE;
    }

    try
    {
      if (element instanceof CDOResourceLeaf)
      {
        String name = ((CDOResourceLeaf)element).getName();

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
    catch (Exception ex)
    {
      ex.printStackTrace();
      return ERROR_IMAGE;
    }
  }

  protected Image getWorkbenchImage(String name)
  {
    ImageDescriptor imageDescriptor = EDITOR_REGISTRY.getImageDescriptor(name);
    if (imageDescriptor != null)
    {
      if (editorRegistryListener == null)
      {
        editorRegistryListener = new EditorRegistryListener();
        EDITOR_REGISTRY.addPropertyListener(editorRegistryListener);
      }

      ResourceManager resourceManager = getResourceManager();
      return (Image)resourceManager.get(imageDescriptor);
    }

    return null;
  }

  protected ResourceManager getResourceManager()
  {
    if (resourceManager == null)
    {
      resourceManager = new LocalResourceManager(JFaceResources.getResources());
    }

    return resourceManager;
  }

  /**
   * A {@link IPropertyListener listener} on the platform's {@link IEditorRegistry editor registry} that fires {@link LabelProviderChangedEvent label events}
   * from the associated {@link #getItemProvider() item provider} when {@link CDOTransferElement element} labels need to be updated.
   *
   * @author Eike Stepper
   * @since 4.2
   */
  protected class EditorRegistryListener implements IPropertyListener
  {
    public void propertyChanged(Object source, int propId)
    {
      if (propId == IEditorRegistry.PROP_CONTENTS)
      {
        fireLabelProviderChanged();
      }
    }
  }
}
