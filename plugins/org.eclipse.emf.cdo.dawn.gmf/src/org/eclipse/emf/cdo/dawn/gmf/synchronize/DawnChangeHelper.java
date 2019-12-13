/*
 * Copyright (c) 2011, 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.gmf.synchronize;

import org.eclipse.emf.cdo.dawn.gmf.util.DawnDiagramUpdater;
import org.eclipse.emf.cdo.dawn.gmf.util.DawnResourceHelper;
import org.eclipse.emf.cdo.dawn.util.exceptions.EClassIncompatibleException;
import org.eclipse.emf.cdo.internal.dawn.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.Transaction;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.gmf.runtime.diagram.core.commands.SetPropertyCommand;
import org.eclipse.gmf.runtime.diagram.core.edithelpers.CreateElementRequestAdapter;
import org.eclipse.gmf.runtime.diagram.core.util.ViewUtil;
import org.eclipse.gmf.runtime.diagram.ui.commands.DeferredCreateConnectionViewAndElementCommand;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateConnectionViewAndElementRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateConnectionViewAndElementRequest.ConnectionViewAndElementDescriptor;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewAndElementRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewAndElementRequest.ViewAndElementDescriptor;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewRequestFactory;
import org.eclipse.gmf.runtime.diagram.ui.requests.EditCommandRequestWrapper;
import org.eclipse.gmf.runtime.diagram.ui.requests.RequestConstants;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.parts.DiagramDocumentEditor;
import org.eclipse.gmf.runtime.emf.core.util.EObjectAdapter;
import org.eclipse.gmf.runtime.emf.core.util.PackageUtil;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.emf.type.core.IHintedType;
import org.eclipse.gmf.runtime.emf.type.core.requests.CreateElementRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.DestroyElementRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.DestroyReferenceRequest;
import org.eclipse.gmf.runtime.notation.Anchor;
import org.eclipse.gmf.runtime.notation.Bendpoints;
import org.eclipse.gmf.runtime.notation.Bounds;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * @author Martin Fluegge
 */
public class DawnChangeHelper
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, DawnChangeHelper.class);

  protected static final java.util.Map<String, Boolean> options = new HashMap<String, Boolean>();

  protected static final java.util.Map<Object, Object> setElementOptions = new HashMap<Object, Object>();

  static
  {
    options.put(Transaction.OPTION_UNPROTECTED, Boolean.FALSE);
    options.put(Transaction.OPTION_NO_NOTIFICATIONS, Boolean.TRUE);
    options.put(Transaction.OPTION_NO_TRIGGERS, Boolean.TRUE);
  }

  /**
   * generic Method to create an EditPart by the given view
   *
   * @param node
   * @param elementType
   * @param diagramEP
   * @return the EditPart for the newly created Node
   */
  public static EditPart createNode(final Node node, IElementType elementType, DiagramEditPart diagramEP)
  {
    CreateViewRequest createViewRequest = CreateViewRequestFactory.getCreateShapeRequest(elementType, diagramEP.getDiagramPreferencesHint());

    Bounds bounds = (Bounds)node.getLayoutConstraint();

    Point p = new Point(bounds.getX(), bounds.getY());
    Dimension dimension = new Dimension(bounds.getWidth(), bounds.getHeight());
    createViewRequest.setLocation(p);
    createViewRequest.setSize(dimension);

    diagramEP.performRequest(createViewRequest);

    final IAdaptable viewAdapter = (IAdaptable)((List<?>)createViewRequest.getNewObject()).get(0);
    final EditPartViewer viewer = diagramEP.getViewer();
    final EditPart kep = (EditPart)viewer.getEditPartRegistry().get(viewAdapter.getAdapter(View.class));
    return kep;
  }

  public static EditPart createAttribute(final Node node, IElementType elementType, IGraphicalEditPart parentEditpart)
  {

    if (TRACER.isEnabled())
    {
      TRACER.format("ElementType: {0}  semanticHint {1}", elementType, ((IHintedType)elementType).getSemanticHint()); //$NON-NLS-1$
    }

    CreateElementRequest request = new CreateElementRequest(elementType);
    CreateElementRequestAdapter createElementRequestAdapter = new CreateElementRequestAdapter(request);

    ViewAndElementDescriptor viewAndElementDescriptor = new ViewAndElementDescriptor(createElementRequestAdapter, Node.class,
        ((IHintedType)elementType).getSemanticHint(),

        parentEditpart.getDiagramPreferencesHint());

    CreateViewAndElementRequest createViewRequest = new CreateViewAndElementRequest(viewAndElementDescriptor);

    // Command command = parentEditpart.getCommand(createViewRequest);

    parentEditpart.performRequest(createViewRequest);

    final IAdaptable viewAdapter = (IAdaptable)((List<?>)createViewRequest.getNewObject()).get(0);
    final EditPartViewer viewer = parentEditpart.getViewer();
    final EditPart kep = (EditPart)viewer.getEditPartRegistry().get(viewAdapter.getAdapter(View.class));
    return kep;
  }

  /***************************************
   * This method creates an EditPart given by the specified ElemetType and PLaces it to the give coordinates
   * Element-Types could be: DawnElementTypes.Klasse_1001
   *
   * @param p
   *          the point where the view shoudl be created
   * @param elementType
   * @param diagramEditor
   * @return the created EditPart
   ********************************************************************************************************************/
  public static EditPart createEditPart(Point p, IElementType elementType, DiagramDocumentEditor diagramEditor)
  {
    DiagramEditPart diagramEP = diagramEditor.getDiagramEditPart();

    CreateViewRequest createViewRequest = CreateViewRequestFactory.getCreateShapeRequest(elementType, diagramEP.getDiagramPreferencesHint());

    createViewRequest.setLocation(p);
    diagramEP.performRequest(createViewRequest);

    final IAdaptable viewAdapter = (IAdaptable)((List<?>)createViewRequest.getNewObject()).get(0);
    final EditPartViewer viewer = diagramEP.getViewer();
    return (EditPart)viewer.getEditPartRegistry().get(viewAdapter.getAdapter(View.class));
  }

  /***************************************
   * sets a property for an editpart
   *
   * @param editPart
   * @param name
   * @param object
   * @param dawnDiagramEditor
   ********************************************************************************************************************/
  public static void setProperty(EditPart editPart, String name, String packageUtilPropertyID, Object object, DiagramDocumentEditor dawnDiagramEditor)
  {
    SetPropertyCommand setPropertyCommand = new SetPropertyCommand(dawnDiagramEditor.getEditingDomain(), editPart, packageUtilPropertyID, name, object);

    DiagramEditPart diagramEP = dawnDiagramEditor.getDiagramEditPart();

    diagramEP.getDiagramEditDomain().getDiagramCommandStack().execute(new ICommandProxy(setPropertyCommand));

  }

  /***************************************
   * resizes an Editpart to the given dimension
   *
   * @param editpart
   * @param d
   *          dimension
   ********************************************************************************************************************/
  public static void resizeEditPart(EditPart editpart, Dimension d)
  {
    boolean editModeEnabled = ((org.eclipse.gmf.runtime.diagram.ui.editparts.GraphicalEditPart)editpart).isEditModeEnabled();
    if (!editModeEnabled)
    {
      ((org.eclipse.gmf.runtime.diagram.ui.editparts.GraphicalEditPart)editpart).enableEditMode();
    }

    ChangeBoundsRequest move_req = new ChangeBoundsRequest(RequestConstants.REQ_RESIZE);
    move_req.setResizeDirection(PositionConstants.SOUTH_EAST);

    Bounds b = (Bounds)((Node)editpart.getModel()).getLayoutConstraint();
    Dimension dimensionDelta = new Dimension(d.width - b.getWidth(), d.height - b.getHeight());

    move_req.setSizeDelta(dimensionDelta);

    Command cmd = editpart.getCommand(move_req);
    if (cmd == null || !cmd.canExecute())
    {
      throw new IllegalArgumentException("Command is not executable.");
    }
    ((DiagramEditPart)editpart.getParent()).getDiagramEditDomain().getDiagramCommandStack().execute(cmd);

    if (editModeEnabled)
    {
      ((org.eclipse.gmf.runtime.diagram.ui.editparts.GraphicalEditPart)editpart).enableEditMode();
    }
    else
    {
      ((org.eclipse.gmf.runtime.diagram.ui.editparts.GraphicalEditPart)editpart).disableEditMode();
    }
  }

  /***************************************
   * moves an EdidPart to the given postione
   *
   * @param editpart
   * @param p
   ********************************************************************************************************************/
  public static void moveEditPart(EditPart editpart, Point p)
  {
    ChangeBoundsRequest move_req = new ChangeBoundsRequest(RequestConstants.REQ_MOVE);

    Bounds b = (Bounds)((Node)editpart.getModel()).getLayoutConstraint();
    Point newPoint = new Point(p.x - b.getX(), p.y - b.getY());
    move_req.setMoveDelta(newPoint);

    Command cmd = editpart.getCommand(move_req);
    if (cmd == null || !cmd.canExecute())
    {
      throw new IllegalArgumentException("Command is not executable.");
    }
    ((DiagramEditPart)editpart.getParent()).getDiagramEditDomain().getDiagramCommandStack().execute(cmd);
  }

  /**
   * Moves the Editpart from the current position to the new Vector
   */
  public static void moveEditPartTo(EditPart editpart, Point p)
  {
    ChangeBoundsRequest move_req = new ChangeBoundsRequest(RequestConstants.REQ_MOVE);
    move_req.setMoveDelta(p);
    Command cmd = editpart.getCommand(move_req);
    if (cmd == null || !cmd.canExecute())
    {
      throw new IllegalArgumentException("Command is not executable.");
    }
    ((DiagramEditPart)editpart.getParent()).getDiagramEditDomain().getDiagramCommandStack().execute(cmd);
  }

  /**
   * deletes the EditPart an the concerning model element
   *
   * @param editPart
   * @param editor
   */
  public static void deleteEditPart(final EditPart editPart, DiagramDocumentEditor editor)
  {
    final View view = (View)editPart.getModel();

    EObject eObject = view.getElement();
    if (eObject != null)
    {
      DestroyElementRequest destroy = new DestroyElementRequest(editor.getEditingDomain(), eObject, false);

      Request req = new EditCommandRequestWrapper(destroy);

      Command command = editPart.getCommand(req);
      editor.getDiagramEditDomain().getDiagramCommandStack().execute(command);
    }
    TransactionalEditingDomain domain = editor.getEditingDomain();
    if (view instanceof Edge) // also update the lists of the other client
    {
      domain.getCommandStack().execute(new RecordingCommand(domain)
      {
        @Override
        public void doExecute()
        {
          Edge e = (Edge)view;
          View source = e.getSource();
          View target = e.getTarget();
          if (source != null && target != null)
          {
            EObject targetElement = target.getElement();
            EObject sourceElement = source.getElement();

            EStructuralFeature containingFeature = sourceElement.eContainingFeature();
            Collection<?> sourceCrossreferenceCollection = (Collection<?>)sourceElement.eGet(containingFeature);
            sourceCrossreferenceCollection.remove(targetElement);
          }
        }
      });
    }
    ViewUtil.destroy(view);
  }

  /***************************************
   * Deletes a view and it's contained element
   *
   * @param view
   * @param editor
   ********************************************************************************************************************/
  public static void deleteView(final View view, DiagramDocumentEditor editor)
  {

    if (TRACER.isEnabled())
    {
      TRACER.format("Deleting view {0} ", view); //$NON-NLS-1$
    }

    EObject eObject = view.getElement();
    if (eObject != null)
    {
      DestroyElementRequest destroy = new DestroyElementRequest(editor.getEditingDomain(), eObject, false);

      Request req = new EditCommandRequestWrapper(destroy);

      EditPart findEditPart = DawnDiagramUpdater.findEditPart(view, editor.getDiagramEditPart().getViewer());
      Command command = findEditPart.getCommand(req);
      editor.getDiagramEditDomain().getDiagramCommandStack().execute(command);
    }
    if (view instanceof Edge) // also update the lists of the other client
    {
      destroyEdge((Edge)view, editor);
    }
    ViewUtil.destroy(view);
  }

  public static void destroyEdge(Edge edge, DiagramDocumentEditor editor)
  {
    EObject referenceObject = ViewUtil.resolveSemanticElement(edge.getTarget());
    EObject container = ViewUtil.resolveSemanticElement(edge.getSource());
    EditPart parentEditpart = DawnDiagramUpdater.findEditPart(edge, editor.getDiagramEditPart().getViewer());
    DestroyReferenceRequest destroyReferenceRequest = new DestroyReferenceRequest(editor.getEditingDomain(), container, null, referenceObject, false);
    Request req = new EditCommandRequestWrapper(destroyReferenceRequest);

    Command command = parentEditpart.getCommand(req);
    editor.getDiagramEditDomain().getDiagramCommandStack().execute(command);
  }

  /***************************************
   * creates an edge for a given elementType
   *
   * @param oldEdge
   * @param sourceEditPart
   * @param targetEditPart
   * @param elementType
   * @param root
   * @return the newly created EditPart
   ********************************************************************************************************************/
  public static EditPart createEdge(Edge oldEdge, EditPart sourceEditPart, EditPart targetEditPart, IElementType elementType, final DiagramEditPart root)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Creaeting Edge from old edge {0} ", oldEdge); //$NON-NLS-1$
    }

    CreateConnectionViewAndElementRequest req = new CreateConnectionViewAndElementRequest(elementType, ((IHintedType)elementType).getSemanticHint(),
        root.getDiagramPreferencesHint());

    ICommand createConnectionCmd = new DeferredCreateConnectionViewAndElementCommand(req, new EObjectAdapter((EObject)sourceEditPart.getModel()),
        new EObjectAdapter((EObject)targetEditPart.getModel()), root.getViewer());

    root.getDiagramEditDomain().getDiagramCommandStack().execute(new ICommandProxy(createConnectionCmd));

    final EditPartViewer viewer = root.getViewer();

    final EditPart ret = (EditPart)viewer.getEditPartRegistry().get(((ConnectionViewAndElementDescriptor)req.getNewObject()).getAdapter(View.class));

    if (ret != null)
    {
      setAnchorsAndBendPoints(ret, oldEdge, root);
    }
    return ret;
  }

  /***************************************
   * This Method sets the Anchors and Bendpoint from an old Edge to the edge of the given EditPart
   *
   * @param edgeEditpart
   * @param oldEdge
   * @param root
   ********************************************************************************************************************/
  public static void setAnchorsAndBendPoints(final EditPart edgeEditpart, final Edge oldEdge, final DiagramEditPart root)
  {
    TransactionalEditingDomain domain = root.getEditingDomain();// getEditingDomain();
    domain.getCommandStack().execute(new RecordingCommand(domain)
    {
      @Override
      public void doExecute()
      {
        Edge edge = (Edge)edgeEditpart.getModel();

        edge.setBendpoints((Bendpoints)DawnResourceHelper.createCopy(oldEdge.getBendpoints()));
        if (oldEdge.getSourceAnchor() != null)
        {
          edge.setSourceAnchor((Anchor)DawnResourceHelper.createCopy(oldEdge.getSourceAnchor()));
        }
        if (oldEdge.getTargetAnchor() != null)
        {
          edge.setTargetAnchor((Anchor)DawnResourceHelper.createCopy(oldEdge.getTargetAnchor()));
        }

        edgeEditpart.refresh();
        root.refresh();
      }
    });
  }

  /**
   * returns the border color from a given EditPart
   *
   * @param editPart
   * @return the border color of the EditPart
   */
  public static Color getBorderColor(EditPart editPart)
  {
    GraphicalEditPart e = (GraphicalEditPart)editPart;

    Border border = e.getFigure().getBorder();

    if (border instanceof org.eclipse.draw2d.MarginBorder)
    {
      // MarginBorder b = ((MarginBorder) e.getFigure().getBorder());
      // if (border != null)
      // {
      // return b.getColor();
      // }
      return null;
    }
    else if (border instanceof LineBorder)
    {
      LineBorder b = (LineBorder)e.getFigure().getBorder();

      return b.getColor();
    }

    return null;
  }

  /**
   * activates a given EditPart
   *
   * @param editPart
   */
  public static void activateEditPart(final EditPart editPart)
  {
    Display.getDefault().asyncExec(new Runnable()
    {
      @Override
      public void run()
      {
        editPart.activate();
        AbstractGraphicalEditPart graphicalEditPart = (AbstractGraphicalEditPart)editPart;
        graphicalEditPart.getFigure().setEnabled(true);
      }
    });
  }

  /**
   * activates the diagram EditPart
   *
   * @param diagramEditPart
   */
  public static void activateDiagramEditPart(DiagramEditPart diagramEditPart)
  {
    DawnChangeHelper.activateEditPart(diagramEditPart);

    for (Object e : diagramEditPart.getChildren())
    {
      DawnChangeHelper.activateEditPart((EditPart)e);
    }
  }

  /**
   * deactivates the given EditPart
   *
   * @param editPart
   */
  public static void deactivateEditPart(final EditPart editPart)
  {
    Display.getDefault().asyncExec(new Runnable()
    {
      @Override
      public void run()
      {
        editPart.deactivate();
        editPart.getViewer().deselect(editPart);

        AbstractGraphicalEditPart graphicalEditPart = (AbstractGraphicalEditPart)editPart;
        graphicalEditPart.getFigure().setEnabled(false);
      }
    });
  }

  /**
   * deactivates the DiagramEditPart
   *
   * @param diagramEditPart
   */
  public static void deactivateDiagramEditPart(DiagramEditPart diagramEditPart)
  {
    DawnChangeHelper.deactivateEditPart(diagramEditPart);

    for (Object e : diagramEditPart.getChildren())
    {
      DawnChangeHelper.deactivateEditPart((EditPart)e);
    }

  }

  /**
   * Deselects the given EditPart EditPart
   */
  public static void deselect(final EditPart e)
  {
    Display.getDefault().asyncExec(new Runnable()
    {

      @Override
      public void run()
      {
        e.getViewer().deselect(e);
      }
    });
  }

  /**
   * selects the given EditPart
   *
   * @param e
   */
  public static void select(final EditPart e)
  {
    Display.getDefault().asyncExec(new Runnable()
    {

      @Override
      public void run()
      {
        e.getViewer().select(e);
      }
    });
  }

  /***************************************
   * sets a property for an editpart
   *
   * @param editPart
   * @param name
   * @param object
   * @param dawnDiagramEditor
   ********************************************************************************************************************/
  public static void setProperty(EditPart editPart, String name, EAttribute attribute, Object object, DiagramDocumentEditor dawnDiagramEditor)
  {
    SetPropertyCommand setPropertyCommand = new SetPropertyCommand(dawnDiagramEditor.getEditingDomain(), editPart, PackageUtil.getID(attribute), name, object);
    DiagramEditPart diagramEP = dawnDiagramEditor.getDiagramEditPart();

    diagramEP.getDiagramEditDomain().getDiagramCommandStack().execute(new ICommandProxy(setPropertyCommand));
  }

  /***************************************
   * updates the model
   *
   * @param editPart
   * @param model
   * @param editor
   ********************************************************************************************************************/
  public static void updateModel(final EditPart editPart, final EObject model, DiagramDocumentEditor editor)
  {
    editPart.getViewer().getEditDomain();
    View view = (View)editPart.getModel();
    final EObject element = view.getElement();

    if (element.eClass().getName().equals("AnOperation")) // ugliest of all workarounds. Just for the prototype
    {
      editor.getEditingDomain().getCommandStack().execute(new RecordingCommand(editor.getEditingDomain())
      {
        @Override
        public void doExecute()
        {
          try
          {
            DawnResourceHelper.updateEObject(element, model);
          }
          catch (EClassIncompatibleException e)
          {
            e.printStackTrace();
          }
        }
      });
    }
    else
    {
      for (EAttribute attribute : element.eClass().getEAllAttributes())
      {
        setProperty(editPart, attribute.getName(), attribute, model.eGet(attribute), editor);
      }
    }
  }

  public static void deleteAttribute(View view, DiagramDocumentEditor editor)
  {
    deleteView(view, editor);
  }
}
