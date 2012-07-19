/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.ecoretools.diagram.providers;

import org.eclipse.emf.cdo.dawn.ecoretools.diagram.edit.policies.DawnECoreRootCanonicalEditPolicy;

import org.eclipse.emf.ecoretools.diagram.edit.parts.EPackageEditPart;
import org.eclipse.emf.ecoretools.diagram.part.EcoreDiagramEditorPlugin;

import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.common.core.service.AbstractProvider;
import org.eclipse.gmf.runtime.common.core.service.IOperation;
import org.eclipse.gmf.runtime.diagram.ui.services.editpolicy.CreateEditPoliciesOperation;
import org.eclipse.gmf.runtime.diagram.ui.services.editpolicy.IEditPolicyProvider;

/**
 * @author Martin Fluegge
 */
public class DawnEcoreEditPolicyProvider extends AbstractProvider implements IEditPolicyProvider
{
  public static String ID = "org.eclipse.emf.cdo.dawn.ecoretools.diagram.providers.DawnEcoreEditPolicyProvider";

  public boolean provides(IOperation operation)
  {
    if (operation instanceof CreateEditPoliciesOperation)
    {
      CreateEditPoliciesOperation editPoliciesOperation = (CreateEditPoliciesOperation)operation;
      if (editPoliciesOperation.getEditPart() instanceof EPackageEditPart)
      {
        return true;
      }
    }
    return false;
  }

  public void createEditPolicies(EditPart editPart)
  {
    if (editPart instanceof EPackageEditPart)
    {
      EcoreDiagramEditorPlugin.getInstance().logInfo("Overwriting CANONICAL EDITING POLICY in " + editPart);

      // The EcoreTools implementation overwrites the generated canonical editing policy. But it does not register it a
      // "canonical" but as "pseudocanonical". So we need to do the same to overwrite the behavior.
      editPart.installEditPolicy("PseudoCanonical", new DawnECoreRootCanonicalEditPolicy());
      // editPart.installEditPolicy(EditPolicyRoles.CANONICAL_ROLE, new DawnECoreRootCanonicalEditPolicy());
    }
  }
}
