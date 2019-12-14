/*
 * Copyright (c) 2011, 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.gmf.appearance.impl;

import org.eclipse.emf.cdo.dawn.gmf.appearance.DawnAppearancer;
import org.eclipse.emf.cdo.dawn.ui.DawnColorConstants;

import org.eclipse.draw2d.Border;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Martin Fluegge
 */
public class DawnBasicTextAwareEditPartStylizerImpl extends DawnBasicGraphicalEditPartStylizerImpl
{

  Map<EditPart, Border> oldValues = new HashMap<>();

  @Override
  public void setDefault(EditPart editPart)
  {
    setBorder(editPart, oldValues.get(editPart));
  }

  @Override
  public void setConflicted(EditPart editPart, int type)
  {
    final IGraphicalEditPart e = (IGraphicalEditPart)editPart;

    // TODO Setting the foreground color just works until the EditPart is refreshed and the default color is used again.
    // Find a better way to handle this.
    // e.getFigure().setForegroundColor(DawnAppearancer.COLOR_DELETE_CONFLICT);
    oldValues.put(e, e.getFigure().getBorder());

    setBorder(editPart, DawnColorConstants.COLOR_DELETE_CONFLICT, DawnAppearancer.DEFAULT_BORDER_THICKNESS);
    // View view = (View)e.getModel();
    // FontStyle style = (FontStyle)view.getStyle(NotationPackage.eINSTANCE.getFontStyle());
    // if (style == null)
    // {
    // style = NotationFactory.eINSTANCE.createFontStyle();
    // style.setFontColor(FigureUtilities.colorToInteger(DiagramColorConstants.red));
    // style.setBold(true);
    //
    // view.getStyles().add(style);
    // }
  }

  @Override
  public void setLocked(EditPart editPart, int type)
  {
    setBorder(editPart, DawnColorConstants.COLOR_LOCKED_REMOTELY, DawnAppearancer.DEFAULT_BORDER_THICKNESS);
  }
}
