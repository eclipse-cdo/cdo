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
package org.eclipse.emf.cdo.server.internal.hibernate;

import org.eclipse.emf.cdo.server.hibernate.IHibernateMappingProvider;

import org.eclipse.net4j.util.StringUtil;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Reads the hibernate mapping file from one or more resource locations and adds them to the configuration.
 *
 * @author Martin Taal
 */
public class FileHibernateMappingProviderFactory implements IHibernateMappingProvider.Factory
{
  public static final String TYPE = "file"; //$NON-NLS-1$

  private static final String FILE_ELEMENT_TAG_NAME = "mappingFile"; //$NON-NLS-1$

  private static final String LOCATION_ATTRIBUTE_NAME = "location"; //$NON-NLS-1$

  public FileHibernateMappingProviderFactory()
  {
  }

  public String getType()
  {
    return TYPE;
  }

  public FileHibernateMappingProvider create(Element config)
  {
    NodeList mappingFileConfigs = config.getElementsByTagName(FILE_ELEMENT_TAG_NAME);
    if (mappingFileConfigs.getLength() != 1)
    {
      throw new IllegalArgumentException("Zero or More than one mapping file location specified, only one location is supported."); //$NON-NLS-1$
    }

    final Element mappingFile = (Element)mappingFileConfigs.item(0);
    final String location = mappingFile.getAttribute(LOCATION_ATTRIBUTE_NAME);
    if (StringUtil.isEmpty(location))
    {
      throw new IllegalArgumentException("Mapping file location is empty"); //$NON-NLS-1$
    }

    return new FileHibernateMappingProvider(location);
  }
}
