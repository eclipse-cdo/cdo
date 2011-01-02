/*******************************************************************************
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.cdo.dawn.ecoretools.diagram.providers;

import org.eclipse.emf.cdo.dawn.ecoretools.diagram.edit.parts.DawnEcoreEditPartFactory;

import org.eclipse.emf.ecoretools.diagram.part.EcoreDiagramEditorPlugin;
import org.eclipse.emf.ecoretools.diagram.providers.EcoreEditPartProvider;

/**
 * @author Martin Fluegge
 */
public class DawnEcoreEditPartProvider extends EcoreEditPartProvider
{
  /**
   * This class is currently not needed because the change of the EditPolicy is done by changing the EditPart but by
   * using an own EditPartPolicyProvider. The class is left to have the chance to influence the behavior of the
   * EditParts from the generated fragment.
   */
  public DawnEcoreEditPartProvider()
  {
    super();
    EcoreDiagramEditorPlugin.getInstance().logInfo("Using DawnEcoreEditPartProvider instead of the original one.");
    setFactory(new DawnEcoreEditPartFactory());
  }
}
