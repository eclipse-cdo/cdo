/*
 * Copyright (c) 2021, 2024 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.util.security.operations.AuthorizableOperation;

/**
 * A CDO {@link CDOException exception} thrown when an {@link AuthorizableOperation authorizable operation}
 * could not be authorized.
 *
 * @author Eike Stepper
 * @since 4.15
 */
public class AuthorizationException extends CDOException
{
  private static final long serialVersionUID = 1L;

  public AuthorizationException(String message)
  {
    super(message);
  }
}
