/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2014 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.cdo.common.util.CDOException;

/**
 * An unchecked exception that indicates server-side problems.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 * @deprecated Not used.
 */
@Deprecated
public class ServerException extends CDOException
{
  private static final long serialVersionUID = 1L;

  @Deprecated
  public ServerException()
  {
  }

  @Deprecated
  public ServerException(String message)
  {
    super(message);
  }

  @Deprecated
  public ServerException(String message, Throwable cause)
  {
    super(message, cause);
  }

  @Deprecated
  public ServerException(Throwable cause)
  {
    super(cause);
  }
}
