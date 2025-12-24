/*
 * Copyright (c) 2010-2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.ui;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.dawn.appearance.DawnElementStylizer;
import org.eclipse.emf.cdo.dawn.spi.DawnState;
import org.eclipse.emf.cdo.dawn.ui.stylizer.DawnElementStylizerRegistry;
import org.eclipse.emf.cdo.ui.CDOLabelProvider;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

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

    DawnElementStylizer stylizer = DawnElementStylizerRegistry.instance.getStylizer(object);

    CDOObject cdoObject = CDOUtil.getCDOObject((EObject)object);
    if (cdoObject.cdoWriteLock().isLocked())
    {
      return stylizer.getForegroundColor(cdoObject, DawnState.LOCKED_LOCALLY);
    }
    else if (CDOUtil.getCDOObject((EObject)object).cdoWriteLock().isLockedByOthers())
    {
      return stylizer.getForegroundColor(cdoObject, DawnState.LOCKED_REMOTELY);
    }
    // Use default
    return foreground;
  }

  @Override
  public Color getBackground(Object object)
  {
    // makes sure that the object is adapted
    Color background = super.getBackground(object);

    DawnElementStylizer stylizer = DawnElementStylizerRegistry.instance.getStylizer(object);

    CDOObject cdoObject = CDOUtil.getCDOObject((EObject)object);
    if (cdoObject.cdoWriteLock().isLocked())
    {
      return stylizer.getBackgroundColor(cdoObject, DawnState.LOCKED_LOCALLY);
    }
    else if (CDOUtil.getCDOObject((EObject)object).cdoWriteLock().isLockedByOthers())
    {
      return stylizer.getBackgroundColor(cdoObject, DawnState.LOCKED_REMOTELY);
    }
    // Use default
    return background;
  }

  @Override
  public Image getImage(Object object)
  {
    DawnElementStylizer stylizer = DawnElementStylizerRegistry.instance.getStylizer(object);

    CDOObject cdoObject = CDOUtil.getCDOObject((EObject)object);
    if (cdoObject.cdoWriteLock().isLocked())
    {
      return stylizer.getImage(cdoObject, DawnState.LOCKED_LOCALLY);
    }
    else if (CDOUtil.getCDOObject((EObject)object).cdoWriteLock().isLockedByOthers())
    {
      return stylizer.getImage(cdoObject, DawnState.LOCKED_REMOTELY);
    }

    return super.getImage(object);
  }
}
