/*
 * Copyright (c) 2013, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.security.internal.ui.util;

import org.eclipse.emf.cdo.security.FilterPermission;
import org.eclipse.emf.cdo.security.Permission;
import org.eclipse.emf.cdo.security.PermissionFilter;
import org.eclipse.emf.cdo.security.ResourceFilter;

import org.eclipse.jface.viewers.IFilter;

/**
 * A UI filter that selects {@link Permission}s that are
 * resource-based permissions.
 *
 * @author Christian W. Damus (CEA LIST)
 */
public class ResourceBasedPermissionFilter implements IFilter
{
  public ResourceBasedPermissionFilter()
  {
  }

  @Override
  public boolean select(Object element)
  {
    if (!(element instanceof FilterPermission))
    {
      return false;
    }

    {
      FilterPermission perm = (FilterPermission)element;
      for (PermissionFilter filter : perm.getFilters())
      {
        // We cannot edit CombinedFilters even if they comprise
        // only ResourceFilters
        if (!(filter instanceof ResourceFilter))
        {
          return false;
        }
      }
    }

    return true;
  }
}
