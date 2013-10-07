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

import org.eclipse.emf.cdo.security.CombinedFilter;
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

  public boolean select(Object element)
  {
    boolean result = element instanceof FilterPermission;

    if (result)
    {
      FilterPermission perm = (FilterPermission)element;
      for (PermissionFilter filter : perm.getFilters())
      {
        if (!isResourceFilter(filter))
        {
          result = false;
        }
      }
    }

    return result;
  }

  protected boolean isResourceFilter(PermissionFilter filter)
  {
    boolean result = filter instanceof ResourceFilter;

    if (!result && filter instanceof CombinedFilter)
    {
      result = true; // Assume all permission filters are OK

      CombinedFilter combined = (CombinedFilter)filter;
      for (PermissionFilter next : combined.getOperands())
      {
        result = isResourceFilter(next);
        if (!result)
        {
          break;
        }
      }
    }

    return result;
  }
}
