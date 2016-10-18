/*
 * Copyright (c) 2008-2012, 2015, 2016 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.hibernate.internal.teneo;

import org.eclipse.emf.cdo.server.hibernate.IHibernateMappingProvider;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Properties;

/**
 * Reads the hibernate mapping file from one or more resource locations and adds them to the configuration.
 *
 * @author Eike Stepper
 * @since 3.0
 */
public class TeneoHibernateMappingProviderFactory implements IHibernateMappingProvider.Factory
{
  public static final String TYPE = "teneo"; //$NON-NLS-1$

  private static final String PROPERTY_TAG = "property"; //$NON-NLS-1$

  private static final String EXTENSION_TAG = "extension"; //$NON-NLS-1$

  private static final String NAME_ATTR = "name"; //$NON-NLS-1$

  private static final String VALUE_ATTR = "value"; //$NON-NLS-1$

  public TeneoHibernateMappingProviderFactory()
  {
  }

  public String getType()
  {
    return TYPE;
  }

  public TeneoHibernateMappingProvider create(Element config)
  {

    final Properties properties = new Properties();
    final NodeList propertyNodes = config.getElementsByTagName(PROPERTY_TAG);
    for (int i = 0; i < propertyNodes.getLength(); i++)
    {
      final Element propertyElement = (Element)propertyNodes.item(i);
      properties.setProperty(propertyElement.getAttribute(NAME_ATTR), propertyElement.getAttribute(VALUE_ATTR));
    }

    final TeneoHibernateMappingProvider mappingProvider = new TeneoHibernateMappingProvider();
    mappingProvider.setMappingProviderProperties(properties);
    collectExtensions(config, mappingProvider);
    return mappingProvider;
  }

  private void collectExtensions(Element config, TeneoHibernateMappingProvider mappingProvider)
  {
    final NodeList extensionNodes = config.getElementsByTagName(EXTENSION_TAG);
    for (int i = 0; i < extensionNodes.getLength(); i++)
    {
      final Element extensionElement = (Element)extensionNodes.item(i);
      final String nameAttrValue = extensionElement.getAttribute(NAME_ATTR);
      final String valueAttrValue = extensionElement.getAttribute(VALUE_ATTR);
      if (nameAttrValue == null || valueAttrValue == null)
      {
        throw new IllegalArgumentException("Extension element has incorrect format, both the name and value attribute should be present"); //$NON-NLS-1$
      }

      mappingProvider.putExtension(nameAttrValue, valueAttrValue);
    }
  }
}
