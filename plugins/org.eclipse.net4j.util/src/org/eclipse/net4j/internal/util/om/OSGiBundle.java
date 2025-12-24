/*
 * Copyright (c) 2007, 2009-2012, 2019, 2023, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.util.om;

import org.eclipse.net4j.internal.util.bundle.AbstractBundle;
import org.eclipse.net4j.internal.util.bundle.AbstractPlatform;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.AbstractIterator;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Eike Stepper
 */
public class OSGiBundle extends AbstractBundle
{
  public OSGiBundle(AbstractPlatform platform, String bundleID, Class<?> accessor)
  {
    super(platform, bundleID, accessor);
  }

  @Override
  public BundleContext getBundleContext()
  {
    return (BundleContext)super.getBundleContext();
  }

  @Override
  public URL getBaseURL()
  {
    try
    {
      URL entry = getBundleContext().getBundle().getEntry("/"); //$NON-NLS-1$
      URL baseURL = FileLocator.resolve(entry);
      String str = baseURL.toExternalForm();
      if (str.endsWith("/./")) //$NON-NLS-1$
      {
        baseURL = IOUtil.newURL(str.substring(0, str.length() - 2));
      }

      return baseURL;
    }
    catch (IOException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  @Override
  public Class<?> loadClass(String pluginID, String className) throws ClassNotFoundException
  {
    Bundle bundle = Platform.getBundle(pluginID);
    if (bundle == null)
    {
      throw new ClassNotFoundException(className + " cannot be loaded because because bundle " + pluginID + " cannot be resolved");
    }

    return bundle.loadClass(className);
  }

  @Override
  public Iterator<Class<?>> getClasses()
  {
    final Queue<String> folders = new LinkedList<>();
    folders.offer("/");

    return new AbstractIterator<Class<?>>()
    {
      private Enumeration<String> entryPaths;

      @Override
      protected Object computeNextElement()
      {
        for (;;)
        {
          while (entryPaths != null && entryPaths.hasMoreElements())
          {
            String entryPath = entryPaths.nextElement();
            if (entryPath.endsWith("/"))
            {
              folders.offer(entryPath);
            }
            else
            {
              Class<?> c = getClassFromBundle(entryPath);
              if (c != null)
              {
                return c;
              }
            }
          }

          String folder = folders.poll();
          if (folder == null)
          {
            return END_OF_DATA;
          }

          Bundle bundle = getBundleContext().getBundle();
          entryPaths = bundle.getEntryPaths(folder);
        }
      }
    };
  }

  @Override
  public String getStateLocation()
  {
    Bundle bundle = getBundleContext().getBundle();
    return Platform.getStateLocation(bundle).toString();
  }
}
