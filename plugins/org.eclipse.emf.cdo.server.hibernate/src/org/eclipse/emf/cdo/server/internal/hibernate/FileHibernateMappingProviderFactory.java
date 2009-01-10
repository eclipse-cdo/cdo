/***************************************************************************
 * Copyright (c) 2004 - 2009 Springsite B.V. and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Taal - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.internal.hibernate;

import org.eclipse.emf.cdo.server.hibernate.IHibernateMappingProvider;

import org.eclipse.net4j.util.StringUtil;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Reads the hibernate mapping file from one or more resource locations and adds them to the configuration.
 * 
 * @author Martin Taal
 */
public class FileHibernateMappingProviderFactory implements IHibernateMappingProvider.Factory
{
  public static final String TYPE = "file";

  public FileHibernateMappingProviderFactory()
  {
  }

  public String getType()
  {
    return TYPE;
  }

  public FileHibernateMappingProvider create(Element config)
  {
    List<String> locations = new ArrayList<String>();
    NodeList mappingFileConfigs = config.getElementsByTagName("mappingFile");
    for (int i = 0; i < mappingFileConfigs.getLength(); i++)
    {
      Element mappingFile = (Element)mappingFileConfigs.item(i);
      String location = mappingFile.getAttribute("location");
      if (!StringUtil.isEmpty(location))
      {
        locations.add(location);
      }
    }

    if (locations.isEmpty())
    {
      throw new IllegalStateException("No mapping file configured for file mapping provider");
    }

    return new FileHibernateMappingProvider(locations.toArray(new String[locations.size()]));
  }
}
