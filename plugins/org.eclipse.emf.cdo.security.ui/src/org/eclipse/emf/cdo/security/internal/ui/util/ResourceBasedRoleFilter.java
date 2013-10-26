/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.security.internal.ui.util;

import org.eclipse.emf.cdo.security.Permission;
import org.eclipse.emf.cdo.security.Role;

import org.eclipse.jface.viewers.IFilter;

/**
 * A UI filter that selects {@link Role}s that have only
 * {@linkplain ResourceBasedPermissionFilter resource-based} permissions.
 *
 * @author Christian W. Damus (CEA LIST)
 */
public class ResourceBasedRoleFilter implements IFilter
{
  private final ResourceBasedPermissionFilter permFilter = new ResourceBasedPermissionFilter();

  public ResourceBasedRoleFilter()
  {
  }

  public boolean select(Object element)
  {
    boolean result = element instanceof Role;
    if (result)
    {
      Role role = (Role)element;
      for (Permission next : role.getPermissions())
      {
        if (!permFilter.select(next))
        {
          result = false;
          // TODO Should we break here?
        }
      }
    }

    return result;
  }
}
