/*
 * Copyright (c) 2012, 2013, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.security;

/**
 * A {@link SecurityException security exception} indicating the lack of permission required to do something.
 *
 * @author Eike Stepper
 * @since 4.1
 */
public class NoPermissionException extends SecurityException
{
  private static final long serialVersionUID = 1L;

  private static final Boolean UNKNOWN = null;

  private final Object protectableObject;

  private final Boolean write;

  public NoPermissionException(Object protectableObject)
  {
    this(protectableObject, UNKNOWN);
  }

  /**
   * @since 4.21
   */
  public NoPermissionException(Object protectableObject, Boolean write)
  {
    this(protectableObject, "No permission to " + getAccess(write) + " " + protectableObject);
  }

  public NoPermissionException(Object protectableObject, String message)
  {
    this(protectableObject, UNKNOWN, message);
  }

  /**
   * @since 4.21
   */
  public NoPermissionException(Object protectableObject, Boolean write, String message)
  {
    super(message);
    this.protectableObject = protectableObject;
    this.write = write;
  }

  public Object getProtectableObject()
  {
    return protectableObject;
  }

  /**
   * @since 4.21
   */
  public Boolean getWrite()
  {
    return write;
  }

  private static String getAccess(Boolean write)
  {
    if (Boolean.TRUE == write)
    {
      return "write";
    }

    if (Boolean.FALSE == write)
    {
      return "read";
    }

    return "access";
  }
}
