/*
 * Copyright (c) 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.util;

import org.eclipse.emf.cdo.common.revision.CDOElementProxy;

/**
 * An unchecked exception that indicates that {@link CDOElementProxy list element proxies} have been encountered but
 * cannot be handled.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @since 4.1
 */
public class PartialCollectionLoadingNotSupportedException extends IllegalStateException
{
  private static final long serialVersionUID = 1L;

  public PartialCollectionLoadingNotSupportedException()
  {
  }

  public PartialCollectionLoadingNotSupportedException(String message)
  {
    super(message);
  }

  public PartialCollectionLoadingNotSupportedException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public PartialCollectionLoadingNotSupportedException(Throwable cause)
  {
    super(cause);
  }
}
