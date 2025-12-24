/*
 * Copyright (c) 2007, 2011, 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.lifecycle;

/**
 * An unchecked wrapper exception for checked exceptions being thrown from {@link Lifecycle#doActivate()}.
 *
 * @author Eike Stepper
 * @noextend This class is not intended to be subclassed by clients.
 */
public class LifecycleException extends IllegalStateException
{
  private static final long serialVersionUID = 1L;

  public LifecycleException()
  {
  }

  public LifecycleException(String message)
  {
    super(message);
  }

  public LifecycleException(Throwable cause)
  {
    super(cause);
  }

  public LifecycleException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
