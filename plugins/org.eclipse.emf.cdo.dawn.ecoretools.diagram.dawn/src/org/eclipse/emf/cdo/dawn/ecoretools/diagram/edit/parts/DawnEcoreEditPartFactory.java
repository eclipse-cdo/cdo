/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.ecoretools.diagram.edit.parts;

import org.eclipse.emf.ecoretools.diagram.edit.parts.EcoreEditPartFactory;
import org.eclipse.emf.ecoretools.diagram.part.EcoreDiagramEditorPlugin;
import org.eclipse.emf.ecoretools.diagram.part.EcoreVisualIDRegistry;

import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.notation.View;

public class DawnEcoreEditPartFactory extends EcoreEditPartFactory
{
  public DawnEcoreEditPartFactory()
  {
    super();
    EcoreDiagramEditorPlugin.getInstance().logInfo("Using DawnEcoreEditPartFactory instead of the original one");
  }

  @Override
  public EditPart createEditPart(EditPart context, Object model)
  {
    if (model instanceof View)
    {
      View view = (View)model;
      switch (EcoreVisualIDRegistry.getVisualID(view))
      {
      case DawnECoreRootEditPart.VISUAL_ID:
        return new DawnECoreRootEditPart(view);
      }
    }

    return super.createEditPart(context, model);
  }
}
