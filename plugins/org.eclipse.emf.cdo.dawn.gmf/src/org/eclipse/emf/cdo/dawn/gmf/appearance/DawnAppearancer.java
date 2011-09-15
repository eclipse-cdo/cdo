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
package org.eclipse.emf.cdo.dawn.gmf.appearance;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.util.EditPartUtilities;
import org.eclipse.swt.graphics.Color;

/**
 * @author Martin Fluegge
 * @since 2.0
 */
public class DawnAppearancer
{
  public static final int DEFAULT_BORDER_THICKNESS = 2;

  public static final int DEFAULT_LINE_THICKNESS = 3;

  public static final Color COLOR_NO_BORDER = new Color(null, 255, 255, 255);

  public static final Color COLOR_LOCKED_REMOTELY = ColorConstants.yellow;

  public static final Color COLOR_LOCKED_LOCALLY = ColorConstants.green;

  public static final Color COLOR_DELETE_CONFLICT = new Color(null, 255, 0, 0);

  public static final Color COLOR_CHANGE_CONFLICT = new Color(null, 0, 0, 255);

  public static final Color COLOR_NO_CONFLICT = new Color(null, 255, 255, 255);

  public static final int TYPE_CONFLICT_NONE = -1;

  public static final int TYPE_CONFLICT_LOCALLY_DELETED = 0;

  public static final int TYPE_CONFLICT_REMOTELY_DELETED = 1;

  public static final int TYPE_CONFLICT_REMOTELY_AND_LOCALLY_CHANGED = 2;

  public static final int TYPE_LOCKED_LOCALLY = 3;

  public static final int TYPE_LOCKED_GLOBALLY = 4;

  /**
   * @since 2.0
   */
  public static void setEditPartConflicted(EditPart editPart, int type)
  {
    DawnEditPartStylizer stylizer = DawnEditPartStylizerRegistry.instance.getStylizer(editPart);
    if (stylizer != null)
    {
      stylizer.setConflicted(editPart, type);
    }
  }

  /**
   * @since 2.0
   */
  public static void setEditPartDefaultAllChildren(EditPart editPart)
  {
    setEditPartDefault(editPart);

    for (Object child : EditPartUtilities.getAllChildren((GraphicalEditPart)editPart))
    {
      setEditPartDefaultAllChildren((EditPart)child);
    }
  }

  /**
   * @since 2.0
   */
  public static void setEditPartDefault(EditPart editPart)
  {
    DawnEditPartStylizer stylizer = DawnEditPartStylizerRegistry.instance.getStylizer(editPart);
    if (stylizer != null)
    {
      stylizer.setDefault(editPart);
    }
  }

  /**
   * @since 2.0
   */
  protected static void setEditPartLocked(EditPart editPart, int type)
  {
    DawnEditPartStylizer stylizer = DawnEditPartStylizerRegistry.instance.getStylizer(editPart);
    if (stylizer != null)
    {
      stylizer.setDefault(editPart);
    }
  }
}
