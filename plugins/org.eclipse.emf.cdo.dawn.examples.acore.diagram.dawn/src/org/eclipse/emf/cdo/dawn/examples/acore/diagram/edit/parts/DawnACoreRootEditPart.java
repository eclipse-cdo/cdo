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
import org.eclipse.emf.cdo.dawn.gmf.synchronize.DawnConflictHelper;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.notation.View;

/**
 * @author Martin Fluegge
 */
public class DawnACoreRootEditPart extends ACoreRootEditPart
{

  // /**
  // * @generated
  // */
  // public final static String MODEL_ID = "Acore"; //$NON-NLS-1$
  //
  // /**
  // * @generated
  // */
  // public static final int VISUAL_ID = 1000;

  /**
   * @generated
   */
  public DawnACoreRootEditPart(View view)
  {
    super(view);
    AcoreDiagramEditorPlugin.getInstance().logInfo("Using DawnACoreRootEditPart instead of the original one");
  }

  @Override
  protected void removeChild(EditPart child)
  {
    if (DawnConflictHelper.isConflicted((EObject)child.getModel()))
    {
      return;
    }
    super.removeChild(child);
  }
}
