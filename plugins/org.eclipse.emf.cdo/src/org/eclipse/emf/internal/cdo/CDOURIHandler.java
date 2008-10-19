/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.eresource.CDOResourceInputStream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.URIHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class CDOURIHandler implements URIHandler
{
  private CDOViewImpl view;

  public CDOURIHandler(CDOViewImpl view)
  {
    this.view = view;
  }

  public CDOViewImpl getView()
  {
    return view;
  }

  public boolean canHandle(URI uri)
  {
    return false;
  }

  public boolean exists(URI uri, Map<?, ?> options)
  {
    return false;
  }

  public void delete(URI uri, Map<?, ?> options) throws IOException
  {
  }

  public InputStream createInputStream(URI uri, Map<?, ?> options) throws IOException
  {
    return new CDOResourceInputStream(view, uri);
  }

  public OutputStream createOutputStream(URI uri, Map<?, ?> options) throws IOException
  {
    return null;
  }

  public Map<String, ?> contentDescription(URI uri, Map<?, ?> options) throws IOException
  {
    return null;
  }

  public Map<String, ?> getAttributes(URI uri, Map<?, ?> options)
  {
    return null;
  }

  public void setAttributes(URI uri, Map<String, ?> attributes, Map<?, ?> options) throws IOException
  {
  }
}
