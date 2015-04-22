/*
 * Copyright (c) 2008, 2010-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.hibernate;

import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreFactory;
import org.eclipse.emf.cdo.server.hibernate.IHibernateMappingProvider;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Map;
import java.util.Properties;

/**
 * @author Eike Stepper
 */
public class HibernateStoreFactory implements IStoreFactory
{
  private static final String PROPERTY_TAG = "property"; //$NON-NLS-1$

  private static final String MAPPINGPROVIDER_TAG = "mappingProvider"; //$NON-NLS-1$

  private static final String NAME_ATTRIBUTE = "name"; //$NON-NLS-1$

  private static final String VALUE_ATTRIBUTE = "value"; //$NON-NLS-1$

  private static final String TYPE_ATTRIBUTE = "type"; //$NON-NLS-1$

  public HibernateStoreFactory()
  {
  }

  public String getStoreType()
  {
    return HibernateStore.TYPE;
  }

  public IStore createStore(String repositoryName, Map<String, String> repositoryProperties, Element storeConfig)
  {
    final IHibernateMappingProvider mappingProvider = getMappingProvider(storeConfig);

    final Properties properties = new Properties();
    final NodeList propertyNodes = storeConfig.getElementsByTagName(PROPERTY_TAG);
    for (int i = 0; i < propertyNodes.getLength(); i++)
    {
      final Element propertyElement = (Element)propertyNodes.item(i);
      properties.setProperty(propertyElement.getAttribute(NAME_ATTRIBUTE),
          propertyElement.getAttribute(VALUE_ATTRIBUTE));
    }

    return HibernateUtil.getInstance().createStore(mappingProvider, properties);
  }

  private IHibernateMappingProvider getMappingProvider(Element storeConfig)
  {
    NodeList mappingProviderConfigs = storeConfig.getElementsByTagName(MAPPINGPROVIDER_TAG);
    if (mappingProviderConfigs.getLength() != 1)
    {
      throw new IllegalStateException("Exactly one mapping provider must be configured for Hibernate store"); //$NON-NLS-1$
    }

    Element mappingProviderConfig = (Element)mappingProviderConfigs.item(0);
    String mappingProviderType = mappingProviderConfig.getAttribute(TYPE_ATTRIBUTE);
    IHibernateMappingProvider.Factory factory = HibernateUtil.getInstance()
        .createMappingProviderFactory(mappingProviderType);
    if (factory == null)
    {
      throw new IllegalArgumentException("Unknown mapping provider type: " + mappingProviderType); //$NON-NLS-1$
    }

    IHibernateMappingProvider mappingProvider = factory.create(mappingProviderConfig);
    if (mappingProvider == null)
    {
      throw new IllegalArgumentException("No mapping provider created: " + mappingProviderType); //$NON-NLS-1$
    }

    return mappingProvider;
  }
}
