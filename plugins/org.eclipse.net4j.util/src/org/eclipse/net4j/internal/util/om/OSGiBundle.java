/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.util.om;

import org.eclipse.net4j.internal.util.bundle.AbstractBundle;
import org.eclipse.net4j.internal.util.bundle.AbstractPlatform;
import org.eclipse.net4j.util.WrappedException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

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

  public URL getBaseURL()
  {
    try
    {
      URL entry = getBundleContext().getBundle().getEntry("/"); //$NON-NLS-1$
      URL baseURL = FileLocator.resolve(entry);
      String str = baseURL.toExternalForm();
      if (str.endsWith("/./")) //$NON-NLS-1$
      {
        baseURL = new URL(str.substring(0, str.length() - 2));
      }

      return baseURL;
    }
    catch (IOException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  @Override
  public Iterator<Class<?>> getClasses()
  {
    final Bundle bundle = getBundleContext().getBundle();

    return new AbstractIterator<Class<?>>()
    {
      private Enumeration<String> entryPaths = bundle.getEntryPaths("/");

      @Override
      protected Object computeNextElement()
      {
        while (entryPaths.hasMoreElements())
        {
          String entryPath = entryPaths.nextElement();
          Class<?> c = getClassFromBundle(entryPath);
          if (c != null)
          {
            return c;
          }
        }

        return END_OF_DATA;
      }
    };

  }

  public String getStateLocation()
  {
    Bundle bundle = getBundleContext().getBundle();
    return Platform.getStateLocation(bundle).toString();
  }

  /**
   * @author Eike Stepper
   */
  private static abstract class AbstractIterator<T> implements Iterator<T>
  {
    /**
     * The token to be used in {@link #computeNextElement()} to indicate the end of the iteration.
     */
    protected static final Object END_OF_DATA = new Object();
  
    private boolean computed;
  
    private T next;
  
    public AbstractIterator()
    {
    }
  
    public final boolean hasNext()
    {
      if (computed)
      {
        return true;
      }
  
      Object object = computeNextElement();
      computed = true;
  
      if (object == END_OF_DATA)
      {
        return false;
      }
  
      @SuppressWarnings("unchecked")
      T cast = (T)object;
      next = cast;
      return true;
    }
  
    public final T next()
    {
      if (!hasNext())
      {
        throw new NoSuchElementException();
      }
  
      computed = false;
      return next;
    }
  
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  
    /**
     * Returns the next iteration element, or {@link #END_OF_DATA} if the end of the iteration has been reached.
     */
    protected abstract Object computeNextElement();
  }
}
