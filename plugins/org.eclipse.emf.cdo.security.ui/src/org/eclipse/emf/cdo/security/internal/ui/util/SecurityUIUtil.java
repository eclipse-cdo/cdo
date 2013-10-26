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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.security.Directory;
import org.eclipse.emf.cdo.security.Realm;
import org.eclipse.emf.cdo.security.SecurityItem;
import org.eclipse.emf.cdo.security.SecurityPackage;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.jface.viewers.IFilter;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Various static utilities for working with the security model.
 *
 * TODO Are there methods in here that should be moved to, e.g., CDOUtil, SecurityManagerUtil, UIUtil, etc?
 *
 * @author Christian W. Damus (CEA LIST)
 */
public final class SecurityUIUtil
{
  private static final IFilter[] RESOURCE_BASED_ROLE_FILTERS = { new ResourceBasedRoleFilter() };

  private static final IFilter[] NO_FILTERS = new IFilter[0];

  private SecurityUIUtil()
  {
  }

  public static Directory getDirectory(Realm realm, EClass itemType)
  {
    Directory result = null;

    String preferredName = itemType == SecurityPackage.Literals.GROUP ? "Groups" //$NON-NLS-1$
        : itemType == SecurityPackage.Literals.USER ? "Users" //$NON-NLS-1$
            : itemType == SecurityPackage.Literals.ROLE ? "Roles" //$NON-NLS-1$
                : ""; //$NON-NLS-1$

    for (SecurityItem next : realm.getItems())
    {
      if (next instanceof Directory)
      {
        Directory directory = (Directory)next;
        if (preferredName.equals(directory.getName()))
        {
          result = (Directory)next;
          break;
        }
      }
    }

    return result;
  }

  public static void applyDefaultFilters(StructuredViewer viewer, EClass itemType)
  {
    for (ViewerFilter next : getDefaultViewerFilters(itemType))
    {
      viewer.addFilter(next);
    }
  }

  private static IFilter[] getDefaultFilters(EClass itemType)
  {
    if (itemType == SecurityPackage.Literals.ROLE)
    {
      return RESOURCE_BASED_ROLE_FILTERS;
    }

    return NO_FILTERS;
  }

  private static IFilter filter(EClass itemType)
  {
    final IFilter[] filters = getDefaultFilters(itemType);

    return filters.length == 0 ? null : new IFilter()
    {
      public boolean select(Object toTest)
      {
        // boolean result = true;
        //
        // for (int i = 0; i < filters.length && result; i++)
        // {
        // result = filters[i].select(toTest);
        // }
        //
        // return result;

        // TODO I generally don't like array iterations with extra conditions. Isn't this easier to recognize?
        for (int i = 0; i < filters.length; i++)
        {
          if (!filters[i].select(toTest))
          {
            return false;
          }
        }

        return true;
      }
    };
  }

  private static List<ViewerFilter> getDefaultViewerFilters(EClass itemType)
  {
    IFilter[] filters = getDefaultFilters(itemType);

    ViewerFilter[] result = new ViewerFilter[filters.length];
    for (int i = 0; i < filters.length; i++)
    {
      result[i] = getViewerFilter(filters[i]);
    }

    return Arrays.asList(result);
  }

  public static ViewerFilter getViewerFilter(final IFilter filter)
  {
    return new ViewerFilter()
    {
      @Override
      public boolean select(Viewer viewer, Object parentElement, Object element)
      {
        return filter.select(element);
      }
    };
  }

  public static void applyDefaultFilters(Collection<?> elements, EClass itemType)
  {
    IFilter filter = filter(itemType);
    if (filter != null)
    {
      for (Iterator<?> iter = elements.iterator(); iter.hasNext();)
      {
        if (!filter.select(iter.next()))
        {
          iter.remove();
        }
      }
    }
  }

  public static IFilter getTypeFilter(final EClass itemType)
  {
    return new IFilter()
    {
      public boolean select(Object toTest)
      {
        return itemType.isInstance(toTest);
      }
    };
  }

  public static void applyTypeFilter(StructuredViewer viewer, final EClass itemType)
  {
    viewer.addFilter(new ViewerFilter()
    {
      @Override
      public boolean select(Viewer viewer, Object parentElement, Object element)
      {
        return itemType.isInstance(element);
      }
    });
  }

  public static boolean isEditable(Object object)
  {
    // TODO What about "unmanaged" EObjects?
    // TODO What about managed legacy objects? --> CDOUtil.getCDOObject()?
    return object instanceof CDOObject && isEditable((CDOObject)object);
  }

  public static boolean isEditable(CDOObject object)
  {
    // TODO What about object.cdoPermission()?
    CDOView view = object.cdoView();
    return view == null || !view.isReadOnly();
  }
}
