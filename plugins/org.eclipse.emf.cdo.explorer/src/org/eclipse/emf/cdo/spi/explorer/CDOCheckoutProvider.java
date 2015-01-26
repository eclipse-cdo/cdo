/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.explorer;

import org.eclipse.emf.cdo.explorer.CDOCheckout;

import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides the behaviour of {@link CDOCheckout checkouts}.
 *
 * @author Eike Stepper
 * @since 4.4
 */
public class CDOCheckoutProvider
{

  /**
   * @author Eike Stepper
   */
  public static abstract class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.explorer.checkoutProviders";

    public Factory(String type)
    {
      super(PRODUCT_GROUP, type);
    }

    public abstract CDOCheckoutProvider create(String description) throws ProductCreationException;

    public static CDOCheckoutProvider getRepositoryProvider(String type)
    {
      Object element = IPluginContainer.INSTANCE.getElement(PRODUCT_GROUP, type, null);
      if (element instanceof CDOCheckoutProvider)
      {
        return (CDOCheckoutProvider)element;
      }

      return null;
    }

    public static CDOCheckoutProvider[] getRepositoryProviders()
    {
      List<CDOCheckoutProvider> providers = new ArrayList<CDOCheckoutProvider>();
      for (String type : IPluginContainer.INSTANCE.getFactoryTypes(PRODUCT_GROUP))
      {
        Object element = IPluginContainer.INSTANCE.getElement(PRODUCT_GROUP, type, null);
        if (element instanceof CDOCheckoutProvider)
        {
          CDOCheckoutProvider provider = (CDOCheckoutProvider)element;
          providers.add(provider);
        }
      }

      return providers.toArray(new CDOCheckoutProvider[providers.size()]);
    }
  }
}
