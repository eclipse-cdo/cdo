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

import org.eclipse.emf.cdo.security.Directory;
import org.eclipse.emf.cdo.security.Realm;
import org.eclipse.emf.cdo.security.SecurityItem;
import org.eclipse.emf.cdo.security.SecurityPackage;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.jface.viewers.IFilter;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import java.util.Collection;
import java.util.Iterator;

/**
 * Various static utilities for working with the security model.
 *
 * @author Christian W. Damus (CEA LIST)
 */
public final class SecurityUIUtil
{
  private static final IFilter[] RESOURCE_BASED_PERMISSION_FILTERS = { new ResourceBasedPermissionFilter() };

  private static final IFilter[] NO_FILTERS = new IFilter[0];

  private SecurityUIUtil()
  {
  }

  public static Directory getDirectory(Realm realm, EClass itemType)
  {
    Directory explicitDefault = getDefaultDirectory(realm, itemType);
    if (explicitDefault != null)
    {
      return explicitDefault;
    }

    // Name-based hack in case the default directory structure has been broken

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
          return directory;
        }
      }
    }

    return null;
  }

  private static Directory getDefaultDirectory(Realm realm, EClass itemType)
  {
    if (itemType.getEPackage() == SecurityPackage.eINSTANCE)
    {
      switch (itemType.getClassifierID())
      {
      case SecurityPackage.ROLE:
        return realm.getDefaultRoleDirectory();
      case SecurityPackage.GROUP:
        return realm.getDefaultGroupDirectory();
      case SecurityPackage.USER:
        return realm.getDefaultUserDirectory();
      }
    }

    return null;
  }

  public static void applySupportedElementFilter(StructuredViewer viewer, EClass itemType)
  {
    ViewerFilter filter = getSupportedElementViewerFilter(itemType);
    if (filter != null)
    {
      viewer.addFilter(filter);
    }
  }

  private static IFilter[] getSupportedElementFilters(EClass itemType)
  {
    if (itemType == SecurityPackage.Literals.FILTER_PERMISSION)
    {
      return RESOURCE_BASED_PERMISSION_FILTERS;
    }

    return NO_FILTERS;
  }

  public static IFilter getSupportedElementFilter(EClass itemType)
  {
    final IFilter[] filters = getSupportedElementFilters(itemType);

    return filters.length == 0 ? null : new IFilter()
    {
      @Override
      public boolean select(Object toTest)
      {
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

  public static ViewerFilter getSupportedElementViewerFilter(EClass itemType)
  {
    IFilter filter = getSupportedElementFilter(itemType);
    return filter == null ? null : getViewerFilter(filter);
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

  public static void applySupportedElementFilter(Collection<?> elements, EClass itemType)
  {
    IFilter filter = getSupportedElementFilter(itemType);
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
      @Override
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
    return !(object instanceof EObject) || CDOUtil.isWritableObject((EObject)object);
  }
}
