/*
 * Copyright (c) 2010, 2012, 2015, 2016 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 *
 */
package org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.policies;

import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.commands.AClassImplementedInterfacesCreateCommand;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.commands.AClassImplementedInterfacesReorientCommand;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AAttributeEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassImplementedInterfacesEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AInterfaceAAttributeInterfaceCompartmentEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AInterfaceAOperationInterfaceCompartmentEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AOperationEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.part.AcoreVisualIDRegistry;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.providers.AcoreElementTypes;

import org.eclipse.emf.ecore.EAnnotation;

import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.common.core.command.ICompositeCommand;
import org.eclipse.gmf.runtime.diagram.core.commands.DeleteCommand;
import org.eclipse.gmf.runtime.emf.commands.core.command.CompositeTransactionalCommand;
import org.eclipse.gmf.runtime.emf.type.core.commands.DestroyElementCommand;
import org.eclipse.gmf.runtime.emf.type.core.commands.DestroyReferenceCommand;
import org.eclipse.gmf.runtime.emf.type.core.requests.CreateRelationshipRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.DestroyElementRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.DestroyReferenceRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.ReorientReferenceRelationshipRequest;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.View;

import java.util.Iterator;

/**
 * @generated
 */
public class AInterfaceItemSemanticEditPolicy extends AcoreBaseItemSemanticEditPolicy
{

  /**
   * @generated
   */
  public AInterfaceItemSemanticEditPolicy()
  {
    super(AcoreElementTypes.AInterface_2001);
  }

  /**
   * @generated
   */
  @Override
  protected Command getDestroyElementCommand(DestroyElementRequest req)
  {
    View view = (View)getHost().getModel();
    CompositeTransactionalCommand cmd = new CompositeTransactionalCommand(getEditingDomain(), null);
    cmd.setTransactionNestingEnabled(false);
    for (Iterator it = view.getTargetEdges().iterator(); it.hasNext();)
    {
      Edge incomingLink = (Edge)it.next();
      if (AcoreVisualIDRegistry.getVisualID(incomingLink) == AClassImplementedInterfacesEditPart.VISUAL_ID)
      {
        DestroyReferenceRequest r = new DestroyReferenceRequest(incomingLink.getSource().getElement(), null, incomingLink.getTarget().getElement(), false);
        cmd.add(new DestroyReferenceCommand(r));
        cmd.add(new DeleteCommand(getEditingDomain(), incomingLink));
        continue;
      }
    }
    EAnnotation annotation = view.getEAnnotation("Shortcut"); //$NON-NLS-1$
    if (annotation == null)
    {
      // there are indirectly referenced children, need extra commands: false
      addDestroyChildNodesCommand(cmd);
      addDestroyShortcutsCommand(cmd, view);
      // delete host element
      cmd.add(new DestroyElementCommand(req));
    }
    else
    {
      cmd.add(new DeleteCommand(getEditingDomain(), view));
    }
    return getGEFWrapper(cmd.reduce());
  }

  /**
   * @generated
   */
  private void addDestroyChildNodesCommand(ICompositeCommand cmd)
  {
    View view = (View)getHost().getModel();
    for (Iterator nit = view.getChildren().iterator(); nit.hasNext();)
    {
      Node node = (Node)nit.next();
      switch (AcoreVisualIDRegistry.getVisualID(node))
      {
      case AInterfaceAAttributeInterfaceCompartmentEditPart.VISUAL_ID:
        for (Iterator cit = node.getChildren().iterator(); cit.hasNext();)
        {
          Node cnode = (Node)cit.next();
          switch (AcoreVisualIDRegistry.getVisualID(cnode))
          {
          case AAttributeEditPart.VISUAL_ID:
            cmd.add(new DestroyElementCommand(new DestroyElementRequest(getEditingDomain(), cnode.getElement(), false))); // directlyOwned:
                                                                                                                          // true
            // don't need explicit deletion of cnode as parent's view deletion would clean child views as well
            // cmd.add(new org.eclipse.gmf.runtime.diagram.core.commands.DeleteCommand(getEditingDomain(), cnode));
            break;
          }
        }
        break;
      case AInterfaceAOperationInterfaceCompartmentEditPart.VISUAL_ID:
        for (Iterator cit = node.getChildren().iterator(); cit.hasNext();)
        {
          Node cnode = (Node)cit.next();
          switch (AcoreVisualIDRegistry.getVisualID(cnode))
          {
          case AOperationEditPart.VISUAL_ID:
            cmd.add(new DestroyElementCommand(new DestroyElementRequest(getEditingDomain(), cnode.getElement(), false))); // directlyOwned:
                                                                                                                          // true
            // don't need explicit deletion of cnode as parent's view deletion would clean child views as well
            // cmd.add(new org.eclipse.gmf.runtime.diagram.core.commands.DeleteCommand(getEditingDomain(), cnode));
            break;
          }
        }
        break;
      }
    }
  }

  /**
   * @generated
   */
  @Override
  protected Command getCreateRelationshipCommand(CreateRelationshipRequest req)
  {
    Command command = req.getTarget() == null ? getStartCreateRelationshipCommand(req) : getCompleteCreateRelationshipCommand(req);
    return command != null ? command : super.getCreateRelationshipCommand(req);
  }

  /**
   * @generated
   */
  protected Command getStartCreateRelationshipCommand(CreateRelationshipRequest req)
  {
    if (AcoreElementTypes.AClassImplementedInterfaces_4002 == req.getElementType())
    {
      return null;
    }
    return null;
  }

  /**
   * @generated
   */
  protected Command getCompleteCreateRelationshipCommand(CreateRelationshipRequest req)
  {
    if (AcoreElementTypes.AClassImplementedInterfaces_4002 == req.getElementType())
    {
      return getGEFWrapper(new AClassImplementedInterfacesCreateCommand(req, req.getSource(), req.getTarget()));
    }
    return null;
  }

  /**
   * Returns command to reorient EReference based link. New link target or source should be the domain model element
   * associated with this node.
   *
   * @generated
   */
  @Override
  protected Command getReorientReferenceRelationshipCommand(ReorientReferenceRelationshipRequest req)
  {
    switch (getVisualID(req))
    {
    case AClassImplementedInterfacesEditPart.VISUAL_ID:
      return getGEFWrapper(new AClassImplementedInterfacesReorientCommand(req));
    }
    return super.getReorientReferenceRelationshipCommand(req);
  }

}
