/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts;

import org.eclipse.emf.cdo.dawn.examples.acore.diagram.part.AcoreDiagramEditorPlugin;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.part.AcoreVisualIDRegistry;

import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.notation.View;

public class DawnAcoreEditPartFactory extends AcoreEditPartFactory
{
  public DawnAcoreEditPartFactory()
  {
    super();
    AcoreDiagramEditorPlugin.getInstance().logInfo("Using DawnAcoreEditPartFactory instead of the original one");
  }

  @Override
  public EditPart createEditPart(EditPart context, Object model)
  {
    if (model instanceof View)
    {
      View view = (View)model;
      switch (AcoreVisualIDRegistry.getVisualID(view))
      {
      case DawnACoreRootEditPart.VISUAL_ID:
        return new DawnACoreRootEditPart(view);
      }
    }

    return super.createEditPart(context, model);
  }
}
