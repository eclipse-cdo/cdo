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
package org.eclipse.emf.cdo.dawn.gmf.appearance.impl;

import org.eclipse.emf.cdo.dawn.gmf.appearance.DawnAppearancer;

import org.eclipse.gef.EditPart;

/**
 * @author Martin Fluegge
 */
public class DawnBasicNodeEditPartStylizerImpl extends DawnBasicGraphicalEditPartStylizerImpl
{

  @Override
  public void setDefault(EditPart editPart)
  {
    setBorder(editPart, DawnAppearancer.COLOR_NO_CONFLICT, 0);
  }

  @Override
  public void setConflicted(EditPart editPart, int type)
  {
    setBorder(editPart, DawnAppearancer.COLOR_DELETE_CONFLICT, DawnAppearancer.DEFAULT_BORDER_THICKNESS);
  }

  @Override
  public void setLocked(EditPart editPart, int type)
  {
    setBorder(editPart, DawnAppearancer.COLOR_LOCKED_REMOTELY, DawnAppearancer.DEFAULT_BORDER_THICKNESS);
  }
}
