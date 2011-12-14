/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui.ide;

import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.ui.internal.ide.bundle.OM;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * {@link org.eclipse.jface.viewers.ILabelProvider ILabelProvider} implementation for the CDO Team integration. Capable
 * of providing text and image information for {@link org.eclipse.emf.cdo.ui.ide.Node nodes}
 * 
 * @author Eike Stepper
 */
public class RepositoryLabelProvider extends LabelProvider
{
  private ComposedAdapterFactory adapterFactory;

  public RepositoryLabelProvider()
  {
    adapterFactory = RepositoryContentProvider.createAdapterFactory();
  }

  @Override
  public void dispose()
  {
    adapterFactory.dispose();
    super.dispose();
  }

  @Override
  public String getText(Object element)
  {
    if (element instanceof Node)
    {
      Node node = (Node)element;
      return node.getText();
    }

    if (element instanceof CDOResourceNode)
    {
      CDOResourceNode resourceNode = (CDOResourceNode)element;
      return resourceNode.getName();
    }

    if (element instanceof Notifier)
    {
      Notifier notifier = (Notifier)element;
      IItemLabelProvider adapter = (IItemLabelProvider)adapterFactory.adapt(notifier, IItemLabelProvider.class);
      if (adapter != null)
      {
        return adapter.getText(notifier);
      }
    }

    if (element instanceof IAdaptable)
    {
      Object adapter = ((IAdaptable)element).getAdapter(ILabelProvider.class);
      if (adapter != null)
      {
        return ((ILabelProvider)adapter).getText(element);
      }
    }

    return super.getText(element);
  }

  @Override
  public Image getImage(Object element)
  {
    if (element instanceof Node)
    {
      Node node = (Node)element;
      String imageKey = node.getImageKey();
      ImageDescriptor imageDescriptor = OM.Activator.imageDescriptorFromPlugin(OM.BUNDLE_ID, imageKey);
      return imageDescriptor.createImage();
    }

    if (element instanceof Notifier)
    {
      Notifier notifier = (Notifier)element;
      IItemLabelProvider adapter = (IItemLabelProvider)adapterFactory.adapt(notifier, IItemLabelProvider.class);
      if (adapter != null)
      {
        return ExtendedImageRegistry.getInstance().getImage(adapter.getImage(notifier));
      }
    }

    if (element instanceof IAdaptable)
    {
      Object adapter = ((IAdaptable)element).getAdapter(ILabelProvider.class);
      if (adapter != null)
      {
        return ((ILabelProvider)adapter).getImage(element);
      }
    }

    return super.getImage(element);
  }
}
