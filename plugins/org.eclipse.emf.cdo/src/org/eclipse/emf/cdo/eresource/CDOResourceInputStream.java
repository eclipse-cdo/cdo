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
package org.eclipse.emf.cdo.eresource;

import org.eclipse.emf.cdo.CDOView;

import org.eclipse.emf.common.util.URI;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public final class CDOResourceInputStream extends InputStream
{
  private CDOView view;

  private URI uri;

  public CDOResourceInputStream(CDOView view, URI uri)
  {
    this.view = view;
    this.uri = uri;
  }

  public CDOView getView()
  {
    return view;
  }

  public URI getURI()
  {
    return uri;
  }

  @Override
  public int read() throws IOException
  {
    throw new IOException("read not supported");
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("CDOInputStream[{0}]", uri);
  }
}
