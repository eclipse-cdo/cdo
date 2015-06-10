/*
 * Copyright (c) 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.checkouts;

import org.eclipse.emf.cdo.CDOElement;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * @author Eike Stepper
 */
public class CDOCheckoutViewerSorter extends ViewerSorter
{
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
      EObject child1 = (EObject)e1;
      EObject child2 = (EObject)e2;

      EObject parent = CDOElement.getParentOf(child1);
      if (parent != null)
      {
        EList<EObject> children = parent.eContents();

        int pos1 = children.indexOf(child1);
        int pos2 = children.indexOf(child2);
        return pos1 - pos2;
      }
    }

    return super.compare(viewer, e1, e2);
  }
}
