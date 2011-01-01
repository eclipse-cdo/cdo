/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Reads the hibernate mapping file from one or more resource locations and adds them to the configuration.
 * 
 * @author Martin Taal
 */
public class FileHibernateMappingProvider extends HibernateMappingProvider
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, FileHibernateMappingProvider.class);

  private final String mappingFileLocation;

  public FileHibernateMappingProvider(String mappingFileLocation)
  {
    if (mappingFileLocation == null || mappingFileLocation.length() == 0)
    {
      throw new IllegalArgumentException("mappingFileLocation"); //$NON-NLS-1$
    }

    this.mappingFileLocation = mappingFileLocation;
  }

  public String getMapping()
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Adding hibernate mapping from location(s): " + mappingFileLocation); //$NON-NLS-1$
    }

    InputStream is = null;

    try
    {
      is = getClass().getResourceAsStream(mappingFileLocation);

      StringBuilder sb = new StringBuilder();
      String line;
      BufferedReader reader = new BufferedReader(new InputStreamReader(is, CDOHibernateConstants.UTF8));
      while ((line = reader.readLine()) != null)
      {
        sb.append(line).append(CDOHibernateConstants.NL);
      }
      return sb.toString();
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
