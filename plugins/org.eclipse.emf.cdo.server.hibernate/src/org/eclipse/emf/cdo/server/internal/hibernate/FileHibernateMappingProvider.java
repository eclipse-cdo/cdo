/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.cdo.server.internal.hibernate.bundle.OM;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.hibernate.cfg.Configuration;

import java.io.InputStream;

/**
 * Reads the hibernate mapping file from one or more resource locations and adds them to the configuration.
 * 
 * @author Martin Taal
 */
public class FileHibernateMappingProvider extends HibernateMappingProvider
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, FileHibernateMappingProvider.class);

  private final String[] mappingFileLocations;

  public FileHibernateMappingProvider(String... mappingFileLocations)
  {
    if (mappingFileLocations == null || mappingFileLocations.length == 0)
    {
      throw new IllegalArgumentException("mappingFileLocations");
    }

    this.mappingFileLocations = mappingFileLocations;
  }

  public void addMapping(Configuration configuration)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Adding hibernate mapping from location(s):");
    }

    for (String location : mappingFileLocations)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace(location);
      }

      InputStream is = null;

      try
      {
        // MT.Question: the mapping file is in a dependent plugin but when using the OM.BUNDLE
        // it tries to find it in this plugin and I get:
        // filenotfound:
        // /home/mtaal/mydata/dev/workspaces/nextspace/org.eclipse.emf.cdo.server.hibernate/mappings/product.hbm.xml
        // I have set Eclipse-BuddyPolicy to dependent
        // is = OM.BUNDLE.getInputStream(location);
        is = getClass().getResourceAsStream(location);
        configuration.addInputStream(is);
      }
      catch (Exception e)
      {
        throw WrappedException.wrap(e);
      }
      finally
      {
        IOUtil.close(is);
      }
    }
  }
}
