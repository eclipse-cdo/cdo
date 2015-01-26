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

import org.eclipse.emf.cdo.explorer.CDORepository;

import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides the behaviour of {@link CDORepository repositories}.
 *
 * @author Eike Stepper
 * @since 4.4
 */
public class CDORepositoryProvider
{

  /**
   * @author Eike Stepper
   */
  public static abstract class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.explorer.repositoryProviders";

    public Factory(String type)
    {
      super(PRODUCT_GROUP, type);
    }

    public abstract CDORepositoryProvider create(String description) throws ProductCreationException;

    public static CDORepositoryProvider getRepositoryProvider(String type)
    {
      Object element = IPluginContainer.INSTANCE.getElement(PRODUCT_GROUP, type, null);
      if (element instanceof CDORepositoryProvider)
      {
        return (CDORepositoryProvider)element;
      }

      return null;
    }

    public static CDORepositoryProvider[] getRepositoryProviders()
    {
      List<CDORepositoryProvider> providers = new ArrayList<CDORepositoryProvider>();
      for (String type : IPluginContainer.INSTANCE.getFactoryTypes(PRODUCT_GROUP))
      {
        Object element = IPluginContainer.INSTANCE.getElement(PRODUCT_GROUP, type, null);
        if (element instanceof CDORepositoryProvider)
        {
          CDORepositoryProvider provider = (CDORepositoryProvider)element;
          providers.add(provider);
        }
      }

      return providers.toArray(new CDORepositoryProvider[providers.size()]);
    }
  }
}
