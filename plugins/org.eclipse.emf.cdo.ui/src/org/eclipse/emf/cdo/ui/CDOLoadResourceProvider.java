/*
 * Copyright (c) 2022, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui;

import org.eclipse.emf.cdo.internal.ui.editor.CDOLoadResourceAction;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link Factory contributable} provider for {@link Resource resources} that can be loaded into a resource set.
 * Used by {@link CDOLoadResourceAction}.
 *
 * @author Eike Stepper
 * @since 4.13
 */
public interface CDOLoadResourceProvider
{
  public String getButtonText(ResourceSet resourceSet);

  public boolean canHandle(ResourceSet resourceSet);

  public List<URI> browseResources(ResourceSet resourceSet, Shell shell, boolean multi);

  /**
   * Creates {@link CDOLoadResourceProvider load resource providers}.
   *
   * @author Eike Stepper
   */
  public static abstract class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.ui.loadResourceProviders"; //$NON-NLS-1$

    public Factory(String type)
    {
      super(PRODUCT_GROUP, type);
    }

    @Override
    public abstract CDOLoadResourceProvider create(String description) throws ProductCreationException;

    public static List<CDOLoadResourceProvider> getProviders(IManagedContainer container, ResourceSet resourceSet)
    {
      List<CDOLoadResourceProvider> providers = new ArrayList<>();
      container.forEachElement(PRODUCT_GROUP, CDOLoadResourceProvider.class, provider -> {
        if (provider.canHandle(resourceSet))
        {
          providers.add(provider);
        }
      });

      return providers;
    }
  }

  /**
   * An optional {@link #getButtonImage(ResourceSet) button image} provider interface
   * for {@link CDOLoadResourceProvider load resource providers}.
   *
   * @author Eike Stepper
   * @since 4.15
   */
  public interface ImageProvider
  {
    public Image getButtonImage(ResourceSet resourceSet);
  }
}
