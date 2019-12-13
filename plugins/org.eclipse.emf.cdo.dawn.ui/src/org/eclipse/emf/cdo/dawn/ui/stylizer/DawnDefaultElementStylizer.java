/*
 * Copyright (c) 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.ui.stylizer;

import org.eclipse.emf.cdo.dawn.appearance.DawnElementStylizer;
import org.eclipse.emf.cdo.dawn.spi.DawnState;
import org.eclipse.emf.cdo.dawn.ui.DawnColorConstants;
import org.eclipse.emf.cdo.dawn.ui.icons.DawnIconRegistry;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * @author Martin Fluegge
 * @since 2.0
 */
public class DawnDefaultElementStylizer implements DawnElementStylizer
{
  @Override
  public void setDefault(Object element)
  {
  }

  @Override
  public void setConflicted(Object element, int type)
  {
  }

  @Override
  public void setLocked(Object element, int type)
  {
  }

  @Override
  public Image getImage(Object element, DawnState state)
  {
    switch (state)
    {
    case LOCKED_LOCALLY:
    {
      return DawnIconRegistry.getImage(DawnIconRegistry.LOCKED);
    }
    case LOCKED_REMOTELY:
    {
      return DawnIconRegistry.getImage(DawnIconRegistry.LOCKED);
    }
    }
    return null;
  }

  @Override
  public Color getForegroundColor(Object element, DawnState state)
  {
    switch (state)
    {
    case CONFLICT:
    {
      return DawnColorConstants.COLOR_DELETE_CONFLICT;
    }
    }

    return null;
  }

  @Override
  public Color getBackgroundColor(Object element, DawnState state)
  {
    switch (state)
    {
    case LOCKED_LOCALLY:
    {
      return DawnColorConstants.COLOR_LOCKED_LOCALLY;
    }
    case LOCKED_REMOTELY:
    {
      return DawnColorConstants.COLOR_LOCKED_REMOTELY;
    }
    }

    return null;
  }
}
