/*
 * Copyright (c) 2009-2012, 2014-2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.view;

import org.eclipse.emf.cdo.view.CDOViewProvider.CDOViewProvider2;

import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;

import org.eclipse.emf.common.util.URI;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Base logic to handle CDOViewProvider priority and regular expression.
 *
 * @author Victor Roldan Betancort
 * @since 2.0
 */
public abstract class AbstractCDOViewProvider implements CDOViewProvider2
{
  private String regex;

  private int priority = DEFAULT_PRIORITY;

  @ExcludeFromDump
  private transient Pattern pattern;

  public AbstractCDOViewProvider()
  {
  }

  public AbstractCDOViewProvider(String regex, int priority)
  {
    this.regex = regex;
    this.priority = priority;
  }

  public AbstractCDOViewProvider(String regex)
  {
    this(regex, DEFAULT_PRIORITY);
  }

  @Override
  public int getPriority()
  {
    return priority;
  }

  public void setPriority(int priority)
  {
    this.priority = priority;
  }

  @Override
  public String getRegex()
  {
    return regex;
  }

  public void setRegex(String regex)
  {
    synchronized (regex)
    {
      this.regex = regex;
      pattern = null;
    }
  }

  @Override
  public boolean matchesRegex(URI uri)
  {
    synchronized (regex)
    {
      if (pattern == null)
      {
        pattern = Pattern.compile(regex);
      }
    }

    Matcher matcher = pattern.matcher(uri.toString());
    return matcher.matches();
  }

  /**
   * Must be overwritten for non-canonical URI formats!
   *
   * @since 4.0
   */
  @Override
  public URI getResourceURI(CDOView view, String path)
  {
    return null;
  }

  /**
   * @since 4.4
   */
  @Override
  public final URI getViewURI(CDOView view)
  {
    URI resourceURI = getResourceURI(view, null);
    if (resourceURI != null)
    {
      if (resourceURI.isHierarchical())
      {
        resourceURI = URI.createHierarchicalURI(resourceURI.scheme(), resourceURI.authority(), null, null, null);
      }
      else
      {
        String string = resourceURI.toString();
        if (string.endsWith("/"))
        {
          string = string.substring(0, string.length() - 1);
          resourceURI = URI.createURI(string);
        }
      }
    }

    return resourceURI;
  }

  /**
   * Should be overwritten for non-canonical URI formats!
   *
   * @since 4.4
   */
  @Override
  public URI getViewURI(URI uri)
  {
    return URI.createHierarchicalURI(uri.scheme(), uri.authority(), uri.device(), null, null);
  }

  /**
   * Should be overwritten for non-canonical URI formats!
   *
   * @since 4.4
   */
  @Override
  public String getPath(URI uri)
  {
    return uri.path();
  }

  @Override
  public String toString()
  {
    return "CDOViewProviderDescriptor[" + getPriority() + " --> " + getRegex() + "]";
  }
}
