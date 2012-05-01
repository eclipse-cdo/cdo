/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.security;

/**
 * Provides the protection level of protectable objects.
 *
 * @author Eike Stepper
 * @since 4.1
 */
public interface CDOPermissionProvider
{
  public static final CDOPermissionProvider NONE = new Constant(CDOPermission.NONE);

  public static final CDOPermissionProvider READ = new Constant(CDOPermission.READ);

  public static final CDOPermissionProvider WRITE = new Constant(CDOPermission.WRITE);

  public CDOPermission getPermission(Object protectableObject);

  /**
   * Provides a constant protection level for all protectable entities.
   *
   * @author Eike Stepper
   */
  public static final class Constant implements CDOPermissionProvider
  {
    private CDOPermission permission;

    private Constant(CDOPermission permission)
    {
      this.permission = permission;
    }

    public CDOPermission getPermission(Object protectableObject)
    {
      return permission;
    }

    @Override
    public String toString()
    {
      return permission.toString();
    }
  }
}
