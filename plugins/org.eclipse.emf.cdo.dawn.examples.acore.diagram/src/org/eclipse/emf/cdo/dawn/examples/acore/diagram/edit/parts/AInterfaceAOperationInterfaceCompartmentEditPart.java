/*
 * Copyright (c) 2010, 2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 *
 */
package org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts;

import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.policies.AInterfaceAOperationInterfaceCompartmentCanonicalEditPolicy;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.policies.AInterfaceAOperationInterfaceCompartmentItemSemanticEditPolicy;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.part.Messages;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ListCompartmentEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.CreationEditPolicy;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.DragDropEditPolicy;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles;
import org.eclipse.gmf.runtime.diagram.ui.figures.ResizableCompartmentFigure;
import org.eclipse.gmf.runtime.notation.View;

/**
 * @generated
 */
public class AInterfaceAOperationInterfaceCompartmentEditPart extends ListCompartmentEditPart
{

  /**
   * @generated
   */
  public static final int VISUAL_ID = 7002;

  /**
   * @generated
   */
  public AInterfaceAOperationInterfaceCompartmentEditPart(View view)
  {
    super(view);
  }

  /**
   * @generated
   */
  @Override
  protected boolean hasModelChildrenChanged(Notification evt)
  {
    return false;
  }

  /**
   * @generated
   */
  @Override
  public String getCompartmentName()
  {
    return Messages.AInterfaceAOperationInterfaceCompartmentEditPart_title;
  }

  /**
   * @generated
   */
  @Override
  public IFigure createFigure()
  {
    ResizableCompartmentFigure result = (ResizableCompartmentFigure)super.createFigure();
    result.setTitleVisibility(false);
    return result;
  }

  /**
   * @generated
   */
  @Override
  protected void createDefaultEditPolicies()
  {
    super.createDefaultEditPolicies();
    installEditPolicy(EditPolicyRoles.SEMANTIC_ROLE,
        new AInterfaceAOperationInterfaceCompartmentItemSemanticEditPolicy());
    installEditPolicy(EditPolicyRoles.CREATION_ROLE, new CreationEditPolicy());
    installEditPolicy(EditPolicyRoles.DRAG_DROP_ROLE, new DragDropEditPolicy());
    installEditPolicy(EditPolicyRoles.CANONICAL_ROLE,
        new AInterfaceAOperationInterfaceCompartmentCanonicalEditPolicy());
  }

  /**
   * @generated
   */
  @Override
  protected void setRatio(Double ratio)
  {
    // nothing to do -- parent layout does not accept Double constraints as ratio
    // super.setRatio(ratio);
  }

}
