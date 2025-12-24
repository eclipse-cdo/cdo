/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui;

import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import java.util.Comparator;

/**
 * @author Eike Stepper
 */
public class CDOViewerComparator extends ViewerComparator
{
  public CDOViewerComparator()
  {
  }

  public CDOViewerComparator(Comparator<? super String> comparator)
  {
    super(comparator);
  }

  @Override
  public int category(Object element)
  {
    if (element instanceof CDOResourceFolder)
    {
      // Sort folders before other elements.
      return -1;
    }

    return super.category(element);
  }

  @Override
  public int compare(Viewer viewer, Object e1, Object e2)
  {
    // Don't sort normal EObjects.
    if (e1 instanceof EObject && !(e1 instanceof CDOResourceNode))
    {
      return 0;
    }

    return super.compare(viewer, e1, e2);
  }
}
