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
import org.eclipse.emf.cdo.dawn.gmf.appearance.DawnEditPartStylizer;
import org.eclipse.emf.cdo.dawn.spi.DawnState;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.swt.graphics.Color;

/**
 * @author Martin Fluegge
 * @since 2.0
 */

public class DawnBasicGraphicalEditPartStylizerImpl extends DawnEditPartStylizer
{
  /**
   * @since 2.0
   */
  @Override
  public void setDefault(EditPart editPart)
  {
    setBorder(editPart, null);
  }

  /**
   * @since 2.0
   */
  @Override
  public void setConflicted(EditPart editPart, int type)
  {
    setBorder(editPart, getForegroundColor(editPart, DawnState.CONFLICT), DawnAppearancer.DEFAULT_BORDER_THICKNESS);
  }

  /**
   * @since 2.0
   */
  @Override
  public void setLocked(EditPart editPart, int type)
  {
    setBorder(editPart, getBackgroundColor(editPart, DawnState.LOCKED_REMOTELY), DawnAppearancer.DEFAULT_BORDER_THICKNESS);
  }

  /**
   * @since 2.0
   */
  protected void setBorder(EditPart editPart, Color color, int thickness)
  {
    Border thickBorder = new LineBorder(color, thickness);
    setBorder(editPart, thickBorder);
  }

  /**
   * @since 2.0
   */
  protected void setBorder(EditPart editPart, Border border)
  {
    GraphicalEditPart e = (GraphicalEditPart)editPart;

    IFigure figure = e.getFigure();
    figure.setBorder(border);
    editPart.refresh();
  }
}
