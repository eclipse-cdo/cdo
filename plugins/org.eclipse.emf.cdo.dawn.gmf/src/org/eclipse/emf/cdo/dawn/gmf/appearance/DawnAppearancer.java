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
package org.eclipse.emf.cdo.dawn.gmf.appearance;

import org.eclipse.emf.cdo.dawn.appearance.DawnElementStylizer;
import org.eclipse.emf.cdo.dawn.helper.DawnEditorHelper;
import org.eclipse.emf.cdo.dawn.ui.stylizer.DawnElementStylizerRegistry;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.util.EditPartUtilities;

/**
 * @author Martin Fluegge
 * @since 2.0
 */
public class DawnAppearancer
{
  public static final int DEFAULT_BORDER_THICKNESS = 2;

  public static final int DEFAULT_LINE_THICKNESS = 3;

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
    DawnElementStylizer stylizer = DawnElementStylizerRegistry.instance.getStylizer(editPart);
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
    DawnElementStylizer stylizer = DawnElementStylizerRegistry.instance.getStylizer(editPart);
    if (stylizer != null)
    {
      stylizer.setDefault(editPart);
    }
  }

  /**
   * @since 2.0
   */
  public static void setEditPartLocked(final EditPart editPart, final int type)
  {
    final DawnElementStylizer stylizer = DawnElementStylizerRegistry.instance.getStylizer(editPart);
    if (stylizer != null)
    {
      DawnEditorHelper.getDisplay().syncExec(new Runnable()
      {
        @Override
        public void run()
        {
          stylizer.setLocked(editPart, type);
        }
      });
    }
  }
}
