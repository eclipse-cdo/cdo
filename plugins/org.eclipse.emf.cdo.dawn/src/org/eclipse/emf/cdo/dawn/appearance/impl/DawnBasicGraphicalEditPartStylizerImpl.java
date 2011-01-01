/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.appearance.impl;

import org.eclipse.emf.cdo.dawn.appearance.DawnAppearancer;
import org.eclipse.emf.cdo.dawn.appearance.DawnEditPartStylizer;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.swt.graphics.Color;

/**
 * @author Martin Fluegge
 */
public class DawnBasicGraphicalEditPartStylizerImpl implements DawnEditPartStylizer
{
  public void setDefault(EditPart editPart)
  {
    setBorder(editPart, null);
  }

  public void setConflicted(EditPart editPart, int type)
  {
    setBorder(editPart, DawnAppearancer.COLOR_DELETE_CONFLICT, DawnAppearancer.DEFAULT_BORDER_THICKNESS);
  }

  public void setLocked(EditPart editPart, int type)
  {
    setBorder(editPart, DawnAppearancer.COLOR_LOCKED_REMOTELY, DawnAppearancer.DEFAULT_BORDER_THICKNESS);
  }

  protected void setBorder(EditPart editPart, Color color, int thickness)
  {
    Border thickBorder = new LineBorder(color, thickness);
    setBorder(editPart, thickBorder);
  }

  protected void setBorder(EditPart editPart, Border border)
  {
    GraphicalEditPart e = (GraphicalEditPart)editPart;

    IFigure figure = e.getFigure();
    figure.setBorder(border);
    editPart.refresh();
  }
}
