/*
 * Copyright (c) 2012, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.revision.CDORevision;

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

  public CDOPermission getPermission(CDORevision revision, CDOBranchPoint securityContext);

  /**
   * Provides a constant protection level for all {@link CDORevision revisions}.
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

    @Override
    public CDOPermission getPermission(CDORevision revision, CDOBranchPoint securityContext)
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
