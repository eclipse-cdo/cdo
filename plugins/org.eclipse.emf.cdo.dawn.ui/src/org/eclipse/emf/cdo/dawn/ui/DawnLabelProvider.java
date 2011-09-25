/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.ui;

import org.eclipse.emf.cdo.ui.CDOLabelProvider;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Color;

/**
 * @author Martin Fluegge
 * @since 1.0
 */
public class DawnLabelProvider extends CDOLabelProvider
{
  public DawnLabelProvider(AdapterFactory adapterFactory, CDOView view, TreeViewer viewer)
  {
    super(adapterFactory, view, viewer);
  }

  @Override
  public Color getForeground(Object object)
  {
    // makes sure that the object is adapted
    Color foreground = super.getForeground(object);
    if (CDOUtil.getCDOObject((EObject)object).cdoWriteLock().isLocked())
    {
      return DawnColorConstants.COLOR_LOCKED_LOCALLY;
    }
    else if (CDOUtil.getCDOObject((EObject)object).cdoWriteLock().isLockedByOthers())
    {
      return DawnColorConstants.COLOR_LOCKED_REMOTELY;
    }
    // Use default
    return foreground;
  }
}
