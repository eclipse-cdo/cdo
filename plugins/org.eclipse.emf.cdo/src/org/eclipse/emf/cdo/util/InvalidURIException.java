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
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.cdo.common.util.CDOException;

import org.eclipse.emf.common.util.URI;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class InvalidURIException extends CDOException
{
  private static final long serialVersionUID = 1L;

  private URI uri;

  public InvalidURIException(URI uri, Throwable cause)
  {
    super("Invalid URI: " + uri, cause);
    this.uri = uri;
  }

  public InvalidURIException(URI uri)
  {
    this(uri, null);
  }

  public URI getURI()
  {
    return uri;
  }
}
