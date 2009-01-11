/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.hibernate;

import org.eclipse.emf.cdo.server.IStoreFactory;
import org.eclipse.emf.cdo.server.hibernate.IHibernateMappingProvider;
import org.eclipse.emf.cdo.server.hibernate.IHibernateStore;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Eike Stepper
 */
public class HibernateStoreFactory implements IStoreFactory
{
  public HibernateStoreFactory()
  {
  }

  public String getStoreType()
  {
    return HibernateStore.TYPE;
  }

  public IHibernateStore createStore(Element storeConfig)
  {
    IHibernateMappingProvider mappingProvider = getMappingProvider(storeConfig);
    return HibernateUtil.getInstance().createStore(mappingProvider);
  }

  private IHibernateMappingProvider getMappingProvider(Element storeConfig)
  {
    NodeList mappingProviderConfigs = storeConfig.getElementsByTagName("mappingProvider");
    if (mappingProviderConfigs.getLength() != 1)
    {
      throw new IllegalStateException("Exactly one mapping provider must be configured for Hibernate store");
    }

    Element mappingProviderConfig = (Element)mappingProviderConfigs.item(0);
    String mappingProviderType = mappingProviderConfig.getAttribute("type");
    IHibernateMappingProvider.Factory factory = HibernateUtil.getInstance().createMappingProviderFactory(
        mappingProviderType);
    if (factory == null)
    {
      throw new IllegalArgumentException("Unknown mapping provider type: " + mappingProviderType);
    }

    IHibernateMappingProvider mappingProvider = factory.create(mappingProviderConfig);
    if (mappingProvider == null)
    {
      throw new IllegalArgumentException("No mapping provider created: " + mappingProviderType);
    }

    return mappingProvider;
  }
}
