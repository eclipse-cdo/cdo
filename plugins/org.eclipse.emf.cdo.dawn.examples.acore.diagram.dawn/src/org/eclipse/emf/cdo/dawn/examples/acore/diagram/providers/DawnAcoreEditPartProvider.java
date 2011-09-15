/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.examples.acore.diagram.providers;

import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.DawnAcoreEditPartFactory;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.part.AcoreDiagramEditorPlugin;

/**
 * @author Martin Fluegge
 */
public class DawnAcoreEditPartProvider extends AcoreEditPartProvider
{
  /**
   * This class is currently not needed because the change of the EditPolicy is done by changing the EditPart but by
   * using an own EditPartPolicyProvider. The class is left to have the chance to influence the behavior of the
   * EditParts from the generated fragment.
   */
  public DawnAcoreEditPartProvider()
  {
    super();
    AcoreDiagramEditorPlugin.getInstance().logInfo("Using DawnAcoreEditPartProvider instead of the original one.");
    setFactory(new DawnAcoreEditPartFactory());
  }
}
