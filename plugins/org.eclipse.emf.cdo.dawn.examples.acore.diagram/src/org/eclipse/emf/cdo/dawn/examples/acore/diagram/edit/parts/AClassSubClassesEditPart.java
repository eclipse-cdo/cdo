/*
 * Copyright (c) 2010, 2012 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.policies.AClassSubClassesItemSemanticEditPolicy;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.RotatableDecoration;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ConnectionNodeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ITreeBranchEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles;
import org.eclipse.gmf.runtime.draw2d.ui.figures.PolylineConnectionEx;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.swt.graphics.Color;

/**
 * @generated
 */
public class AClassSubClassesEditPart extends ConnectionNodeEditPart implements ITreeBranchEditPart
{

  /**
   * @generated
   */
  public static final int VISUAL_ID = 4001;

  /**
   * @generated
   */
  public AClassSubClassesEditPart(View view)
  {
    super(view);
  }

  /**
   * @generated
   */
  protected void createDefaultEditPolicies()
  {
    super.createDefaultEditPolicies();
    installEditPolicy(EditPolicyRoles.SEMANTIC_ROLE, new AClassSubClassesItemSemanticEditPolicy());
  }

  /**
   * Creates figure for this edit part. Body of this method does not depend on settings in generation model so you may
   * safely remove <i>generated</i> tag and modify it.
   * 
   * @generated
   */

  protected Connection createConnectionFigure()
  {
    return new AClassSubClassesFigure();
  }

  /**
   * @generated
   */
  public AClassSubClassesFigure getPrimaryShape()
  {
    return (AClassSubClassesFigure)getFigure();
  }

  /**
   * @generated
   */
  public class AClassSubClassesFigure extends PolylineConnectionEx
  {

    /**
     * @generated
     */
    public AClassSubClassesFigure()
    {
      this.setLineWidth(1);

      setTargetDecoration(createTargetDecoration());
    }

    /**
     * @generated
     */
    private RotatableDecoration createTargetDecoration()
    {
      PolygonDecoration df = new PolygonDecoration();
      df.setFill(true);
      df.setLineWidth(1);
      df.setBackgroundColor(DF_BACK);
      PointList pl = new PointList();
      pl.addPoint(getMapMode().DPtoLP(-2), getMapMode().DPtoLP(2));
      pl.addPoint(getMapMode().DPtoLP(0), getMapMode().DPtoLP(0));
      pl.addPoint(getMapMode().DPtoLP(-2), getMapMode().DPtoLP(-2));
      pl.addPoint(getMapMode().DPtoLP(-2), getMapMode().DPtoLP(2));
      df.setTemplate(pl);
      df.setScale(getMapMode().DPtoLP(7), getMapMode().DPtoLP(3));
      return df;
    }

  }

  /**
   * @generated
   */
  static final Color DF_BACK = new Color(null, 255, 255, 255);

}
