/*******************************************************************************
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.cdo.dawn.ecoretools.diagram.edit.parts;

import org.eclipse.emf.cdo.dawn.synchronize.DawnConflictHelper;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecoretools.diagram.edit.parts.EPackageEditPart;
import org.eclipse.emf.ecoretools.diagram.part.EcoreDiagramEditorPlugin;

import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.notation.View;

/**
 * @author Martin Fluegge
 */
public class DawnECoreRootEditPart extends EPackageEditPart
{
  /**
   * @generated
   */
  public DawnECoreRootEditPart(View view)
  {
    super(view);
    EcoreDiagramEditorPlugin.getInstance().logInfo("Using DawnEcoreRootEditPart instead of the original one");
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
