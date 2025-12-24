/*
 * Copyright (c) 2011, 2012, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.gmf.appearance.impl;

import org.eclipse.emf.cdo.dawn.gmf.appearance.DawnAppearancer;
import org.eclipse.emf.cdo.dawn.spi.DawnState;
import org.eclipse.emf.cdo.dawn.ui.DawnColorConstants;

import org.eclipse.gef.EditPart;

/**
 * @author Martin Fluegge
 */
public class DawnBasicNodeEditPartStylizerImpl extends DawnBasicGraphicalEditPartStylizerImpl
{

  @Override
  public void setDefault(EditPart editPart)
  {
    setBorder(editPart, DawnColorConstants.COLOR_NO_CONFLICT, 0);
  }

  // @Override
  // public void setConflicted(EditPart editPart, int type)
  // {
  // setBorder(editPart, DawnColorConstants.COLOR_DELETE_CONFLICT, DawnAppearancer.DEFAULT_BORDER_THICKNESS);
  // }

  @Override
  public void setLocked(EditPart editPart, int type)
  {
    switch (type)
    {
    case DawnAppearancer.TYPE_LOCKED_GLOBALLY:
    {
      setBorder(editPart, getBackgroundColor(editPart, DawnState.LOCKED_REMOTELY), DawnAppearancer.DEFAULT_BORDER_THICKNESS);
      break;
    }
    case DawnAppearancer.TYPE_LOCKED_LOCALLY:
    {
      setBorder(editPart, getBackgroundColor(editPart, DawnState.LOCKED_LOCALLY), DawnAppearancer.DEFAULT_BORDER_THICKNESS);
      break;
    }
    }
  }
}
