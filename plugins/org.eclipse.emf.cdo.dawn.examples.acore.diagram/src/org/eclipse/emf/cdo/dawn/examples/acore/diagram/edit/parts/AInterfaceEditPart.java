/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.policies.AInterfaceItemSemanticEditPolicy;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.policies.AcoreTextSelectionEditPolicy;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.part.AcoreVisualIDRegistry;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.providers.AcoreElementTypes;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.editpolicies.LayoutEditPolicy;
import org.eclipse.gmf.runtime.diagram.core.edithelpers.CreateElementRequestAdapter;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ITextAwareEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ShapeNodeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.ConstrainedToolbarLayoutEditPolicy;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.CreationEditPolicy;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewAndElementRequest;
import org.eclipse.gmf.runtime.draw2d.ui.figures.ConstrainedToolbarLayout;
import org.eclipse.gmf.runtime.draw2d.ui.figures.WrappingLabel;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.gef.ui.figures.DefaultSizeNodeFigure;
import org.eclipse.gmf.runtime.gef.ui.figures.NodeFigure;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.swt.graphics.Color;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @generated
 */
public class AInterfaceEditPart extends ShapeNodeEditPart
{

  /**
   * @generated
   */
  public static final int VISUAL_ID = 2001;

  /**
   * @generated
   */
  protected IFigure contentPane;

  /**
   * @generated
   */
  protected IFigure primaryShape;

  /**
   * @generated
   */
  public AInterfaceEditPart(View view)
  {
    super(view);
  }

  /**
   * @generated
   */
  protected void createDefaultEditPolicies()
  {
    installEditPolicy(EditPolicyRoles.CREATION_ROLE, new CreationEditPolicy());
    super.createDefaultEditPolicies();
    installEditPolicy(EditPolicyRoles.SEMANTIC_ROLE, new AInterfaceItemSemanticEditPolicy());
    installEditPolicy(EditPolicy.LAYOUT_ROLE, createLayoutEditPolicy());
    // XXX need an SCR to runtime to have another abstract superclass that would let children add reasonable
    // editpolicies
    // removeEditPolicy(org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles.CONNECTION_HANDLES_ROLE);
  }

  /**
   * @generated
   */
  protected LayoutEditPolicy createLayoutEditPolicy()
  {

    ConstrainedToolbarLayoutEditPolicy lep = new ConstrainedToolbarLayoutEditPolicy()
    {

      protected EditPolicy createChildEditPolicy(EditPart child)
      {
        if (child.getEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE) == null)
        {
          if (child instanceof ITextAwareEditPart)
          {
            return new AcoreTextSelectionEditPolicy();
          }
        }
        return super.createChildEditPolicy(child);
      }
    };
    return lep;
  }

  /**
   * @generated
   */
  protected IFigure createNodeShape()
  {
    AInterfaceFigure figure = new AInterfaceFigure();
    return primaryShape = figure;
  }

  /**
   * @generated
   */
  public AInterfaceFigure getPrimaryShape()
  {
    return (AInterfaceFigure)primaryShape;
  }

  /**
   * @generated
   */
  protected boolean addFixedChild(EditPart childEditPart)
  {
    if (childEditPart instanceof AInterfaceNameEditPart)
    {
      ((AInterfaceNameEditPart)childEditPart).setLabel(getPrimaryShape().getFigureAInterfaceNameFigure());
      return true;
    }
    if (childEditPart instanceof AInterfaceAAttributeInterfaceCompartmentEditPart)
    {
      IFigure pane = getPrimaryShape().getFigureInterfaceAttributes();
      setupContentPane(pane); // FIXME each comparment should handle his content pane in his own way
      pane.add(((AInterfaceAAttributeInterfaceCompartmentEditPart)childEditPart).getFigure());
      return true;
    }
    if (childEditPart instanceof AInterfaceAOperationInterfaceCompartmentEditPart)
    {
      IFigure pane = getPrimaryShape().getFigureInterfaceOperations();
      setupContentPane(pane); // FIXME each comparment should handle his content pane in his own way
      pane.add(((AInterfaceAOperationInterfaceCompartmentEditPart)childEditPart).getFigure());
      return true;
    }
    return false;
  }

  /**
   * @generated
   */
  protected boolean removeFixedChild(EditPart childEditPart)
  {
    if (childEditPart instanceof AInterfaceNameEditPart)
    {
      return true;
    }
    if (childEditPart instanceof AInterfaceAAttributeInterfaceCompartmentEditPart)
    {
      IFigure pane = getPrimaryShape().getFigureInterfaceAttributes();
      setupContentPane(pane); // FIXME each comparment should handle his content pane in his own way
      pane.remove(((AInterfaceAAttributeInterfaceCompartmentEditPart)childEditPart).getFigure());
      return true;
    }
    if (childEditPart instanceof AInterfaceAOperationInterfaceCompartmentEditPart)
    {
      IFigure pane = getPrimaryShape().getFigureInterfaceOperations();
      setupContentPane(pane); // FIXME each comparment should handle his content pane in his own way
      pane.remove(((AInterfaceAOperationInterfaceCompartmentEditPart)childEditPart).getFigure());
      return true;
    }
    return false;
  }

  /**
   * @generated
   */
  protected void addChildVisual(EditPart childEditPart, int index)
  {
    if (addFixedChild(childEditPart))
    {
      return;
    }
    super.addChildVisual(childEditPart, -1);
  }

  /**
   * @generated
   */
  protected void removeChildVisual(EditPart childEditPart)
  {
    if (removeFixedChild(childEditPart))
    {
      return;
    }
    super.removeChildVisual(childEditPart);
  }

  /**
   * @generated
   */
  protected IFigure getContentPaneFor(IGraphicalEditPart editPart)
  {
    if (editPart instanceof AInterfaceAAttributeInterfaceCompartmentEditPart)
    {
      return getPrimaryShape().getFigureInterfaceAttributes();
    }
    if (editPart instanceof AInterfaceAOperationInterfaceCompartmentEditPart)
    {
      return getPrimaryShape().getFigureInterfaceOperations();
    }
    return getContentPane();
  }

  /**
   * @generated
   */
  protected NodeFigure createNodePlate()
  {
    DefaultSizeNodeFigure result = new DefaultSizeNodeFigure(40, 40);
    return result;
  }

  /**
   * Creates figure for this edit part. Body of this method does not depend on settings in generation model so you may
   * safely remove <i>generated</i> tag and modify it.
   * 
   * @generated
   */
  protected NodeFigure createNodeFigure()
  {
    NodeFigure figure = createNodePlate();
    figure.setLayoutManager(new StackLayout());
    IFigure shape = createNodeShape();
    figure.add(shape);
    contentPane = setupContentPane(shape);
    return figure;
  }

  /**
   * Default implementation treats passed figure as content pane. Respects layout one may have set for generated figure.
   * 
   * @param nodeShape
   *          instance of generated figure class
   * @generated
   */
  protected IFigure setupContentPane(IFigure nodeShape)
  {
    if (nodeShape.getLayoutManager() == null)
    {
      ConstrainedToolbarLayout layout = new ConstrainedToolbarLayout();
      layout.setSpacing(5);
      nodeShape.setLayoutManager(layout);
    }
    return nodeShape; // use nodeShape itself as contentPane
  }

  /**
   * @generated
   */
  public IFigure getContentPane()
  {
    if (contentPane != null)
    {
      return contentPane;
    }
    return super.getContentPane();
  }

  /**
   * @generated
   */
  protected void setForegroundColor(Color color)
  {
    if (primaryShape != null)
    {
      primaryShape.setForegroundColor(color);
    }
  }

  /**
   * @generated
   */
  protected void setBackgroundColor(Color color)
  {
    if (primaryShape != null)
    {
      primaryShape.setBackgroundColor(color);
    }
  }

  /**
   * @generated
   */
  protected void setLineWidth(int width)
  {
    if (primaryShape instanceof Shape)
    {
      ((Shape)primaryShape).setLineWidth(width);
    }
  }

  /**
   * @generated
   */
  protected void setLineType(int style)
  {
    if (primaryShape instanceof Shape)
    {
      ((Shape)primaryShape).setLineStyle(style);
    }
  }

  /**
   * @generated
   */
  public EditPart getPrimaryChildEditPart()
  {
    return getChildBySemanticHint(AcoreVisualIDRegistry.getType(AInterfaceNameEditPart.VISUAL_ID));
  }

  /**
   * @generated
   */
  public List<IElementType> getMARelTypesOnTarget()
  {
    ArrayList<IElementType> types = new ArrayList<IElementType>(1);
    types.add(AcoreElementTypes.AClassImplementedInterfaces_4002);
    return types;
  }

  /**
   * @generated
   */
  public List<IElementType> getMATypesForSource(IElementType relationshipType)
  {
    LinkedList<IElementType> types = new LinkedList<IElementType>();
    if (relationshipType == AcoreElementTypes.AClassImplementedInterfaces_4002)
    {
      types.add(AcoreElementTypes.AClass_2002);
    }
    return types;
  }

  /**
   * @generated
   */
  public EditPart getTargetEditPart(Request request)
  {
    if (request instanceof CreateViewAndElementRequest)
    {
      CreateElementRequestAdapter adapter = ((CreateViewAndElementRequest)request).getViewAndElementDescriptor()
          .getCreateElementRequestAdapter();
      IElementType type = (IElementType)adapter.getAdapter(IElementType.class);
      if (type == AcoreElementTypes.AAttribute_3001)
      {
        return getChildBySemanticHint(AcoreVisualIDRegistry
            .getType(AInterfaceAAttributeInterfaceCompartmentEditPart.VISUAL_ID));
      }
      if (type == AcoreElementTypes.AOperation_3002)
      {
        return getChildBySemanticHint(AcoreVisualIDRegistry
            .getType(AInterfaceAOperationInterfaceCompartmentEditPart.VISUAL_ID));
      }
    }
    return super.getTargetEditPart(request);
  }

  /**
   * @generated
   */
  public class AInterfaceFigure extends RoundedRectangle
  {

    /**
     * @generated
     */
    private WrappingLabel fFigureAInterfaceNameFigure;

    /**
     * @generated
     */
    private RectangleFigure fFigureInterfaceAttributes;

    /**
     * @generated
     */
    private RectangleFigure fFigureInterfaceOperations;

    /**
     * @generated
     */
    public AInterfaceFigure()
    {

      ToolbarLayout layoutThis = new ToolbarLayout();
      layoutThis.setStretchMinorAxis(true);
      layoutThis.setMinorAlignment(ToolbarLayout.ALIGN_CENTER);

      layoutThis.setSpacing(0);
      layoutThis.setVertical(true);

      this.setLayoutManager(layoutThis);

      this.setCornerDimensions(new Dimension(getMapMode().DPtoLP(12), getMapMode().DPtoLP(12)));
      this.setLineWidth(1);
      this.setBackgroundColor(THIS_BACK);
      createContents();
    }

    /**
     * @generated
     */
    private void createContents()
    {

      WrappingLabel aInterfaceTypeLabelFigure0 = new WrappingLabel();
      aInterfaceTypeLabelFigure0.setText("«Interface»");

      this.add(aInterfaceTypeLabelFigure0);

      fFigureAInterfaceNameFigure = new WrappingLabel();
      fFigureAInterfaceNameFigure.setText("<...>");

      this.add(fFigureAInterfaceNameFigure);

      fFigureInterfaceAttributes = new RectangleFigure();
      fFigureInterfaceAttributes.setLineWidth(1);

      this.add(fFigureInterfaceAttributes);
      fFigureInterfaceAttributes.setLayoutManager(new StackLayout());

      fFigureInterfaceOperations = new RectangleFigure();
      fFigureInterfaceOperations.setLineWidth(1);

      this.add(fFigureInterfaceOperations);
      fFigureInterfaceOperations.setLayoutManager(new StackLayout());

    }

    /**
     * @generated
     */
    private boolean myUseLocalCoordinates = false;

    /**
     * @generated
     */
    protected boolean useLocalCoordinates()
    {
      return myUseLocalCoordinates;
    }

    /**
     * @generated
     */
    protected void setUseLocalCoordinates(boolean useLocalCoordinates)
    {
      myUseLocalCoordinates = useLocalCoordinates;
    }

    /**
     * @generated
     */
    public WrappingLabel getFigureAInterfaceNameFigure()
    {
      return fFigureAInterfaceNameFigure;
    }

    /**
     * @generated
     */
    public RectangleFigure getFigureInterfaceAttributes()
    {
      return fFigureInterfaceAttributes;
    }

    /**
     * @generated
     */
    public RectangleFigure getFigureInterfaceOperations()
    {
      return fFigureInterfaceOperations;
    }

  }

  /**
   * @generated
   */
  static final Color THIS_BACK = new Color(null, 250, 250, 190);

}
