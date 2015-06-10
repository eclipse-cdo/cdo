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

import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.policies.AClassAssociationsItemSemanticEditPolicy;

import org.eclipse.draw2d.Connection;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ConnectionNodeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ITreeBranchEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles;
import org.eclipse.gmf.runtime.draw2d.ui.figures.PolylineConnectionEx;
import org.eclipse.gmf.runtime.notation.View;

/**
 * @generated
 */
public class AClassAssociationsEditPart extends ConnectionNodeEditPart implements ITreeBranchEditPart
{

  /**
   * @generated
   */
  public static final int VISUAL_ID = 4003;

  /**
   * @generated
   */
  public AClassAssociationsEditPart(View view)
  {
    super(view);
  }

  /**
   * @generated
   */
  @Override
  protected void createDefaultEditPolicies()
  {
    super.createDefaultEditPolicies();
    installEditPolicy(EditPolicyRoles.SEMANTIC_ROLE, new AClassAssociationsItemSemanticEditPolicy());
  }

  /**
   * Creates figure for this edit part. Body of this method does not depend on settings in generation model so you may
   * safely remove <i>generated</i> tag and modify it.
   *
   * @generated
   */
  @Override
  protected Connection createConnectionFigure()
  {
    return new PolylineConnectionEx();
  }

  /**
   * @generated
   */
  public PolylineConnectionEx getPrimaryShape()
  {
    return (PolylineConnectionEx)getFigure();
  }

}
