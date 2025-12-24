/*
 * Copyright (c) 2008, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.factory;

/**
 * Thrown from {@link FactoryDescriptor#createFactory()} if a {@link IFactory factory} could not be created.
 *
 * @author Eike Stepper
 * @noextend This class is not intended to be subclassed by clients.
 */
public class FactoryCreationException extends RuntimeException
{
  private static final long serialVersionUID = 1L;

  public FactoryCreationException()
  {
  }

  public FactoryCreationException(String message)
  {
    super(message);
  }

  public FactoryCreationException(Throwable cause)
  {
    super(cause);
  }

  public FactoryCreationException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
