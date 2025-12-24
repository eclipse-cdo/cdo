/*
 * Copyright (c) 2007, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
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
 * An unchecked exception that may be thrown from {@link IFactory factories} to indicate the inability to create a
 * product.
 *
 * @author Eike Stepper
 * @noextend This class is not intended to be subclassed by clients.
 */
public class ProductCreationException extends RuntimeException
{
  private static final long serialVersionUID = 1L;

  public ProductCreationException()
  {
  }

  public ProductCreationException(String message)
  {
    super(message);
  }

  public ProductCreationException(Throwable cause)
  {
    super(cause);
  }

  public ProductCreationException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
