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
package org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.policies;

import org.eclipse.emf.cdo.dawn.examples.acore.AClass;
import org.eclipse.emf.cdo.dawn.examples.acore.AInterface;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.helpers.AcoreBaseEditHelper;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.part.AcoreVisualIDRegistry;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.providers.AcoreElementTypes;

import org.eclipse.emf.transaction.TransactionalEditingDomain;

import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.requests.ReconnectRequest;
import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.gmf.runtime.common.core.command.ICompositeCommand;
import org.eclipse.gmf.runtime.diagram.core.commands.DeleteCommand;
import org.eclipse.gmf.runtime.diagram.ui.commands.CommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.SemanticEditPolicy;
import org.eclipse.gmf.runtime.emf.commands.core.command.CompositeTransactionalCommand;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.emf.type.core.requests.ConfigureRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.CreateElementRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.CreateRelationshipRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.DestroyElementRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.DestroyReferenceRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.DestroyRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.DuplicateElementsRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.GetEditContextRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.IEditCommandRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.MoveRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.ReorientReferenceRelationshipRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.ReorientRelationshipRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.SetRequest;
import org.eclipse.gmf.runtime.notation.View;

import java.util.Iterator;

/**
 * @generated
 */
public class AcoreBaseItemSemanticEditPolicy extends SemanticEditPolicy
{

  /**
   * Extended request data key to hold editpart visual id.
   *
   * @generated
   */
  public static final String VISUAL_ID_KEY = "visual_id"; //$NON-NLS-1$

  /**
   * @generated
   */
  private final IElementType myElementType;

  /**
   * @generated
   */
  protected AcoreBaseItemSemanticEditPolicy(IElementType elementType)
  {
    myElementType = elementType;
  }

  /**
   * Extended request data key to hold editpart visual id. Add visual id of edited editpart to extended data of the
   * request so command switch can decide what kind of diagram element is being edited. It is done in those cases when
   * it's not possible to deduce diagram element kind from domain element.
   *
   * @generated
   */
  @Override
  public Command getCommand(Request request)
  {
    if (request instanceof ReconnectRequest)
    {
      Object view = ((ReconnectRequest)request).getConnectionEditPart().getModel();
      if (view instanceof View)
      {
        Integer id = new Integer(AcoreVisualIDRegistry.getVisualID((View)view));
        request.getExtendedData().put(VISUAL_ID_KEY, id);
      }
    }
    return super.getCommand(request);
  }

  /**
   * Returns visual id from request parameters.
   *
   * @generated
   */
  protected int getVisualID(IEditCommandRequest request)
  {
    Object id = request.getParameter(VISUAL_ID_KEY);
    return id instanceof Integer ? ((Integer)id).intValue() : -1;
  }

  /**
   * @generated
   */
  @Override
  protected Command getSemanticCommand(IEditCommandRequest request)
  {
    IEditCommandRequest completedRequest = completeRequest(request);
    Command semanticCommand = getSemanticCommandSwitch(completedRequest);
    semanticCommand = getEditHelperCommand(completedRequest, semanticCommand);
    if (completedRequest instanceof DestroyRequest)
    {
      DestroyRequest destroyRequest = (DestroyRequest)completedRequest;
      return shouldProceed(destroyRequest) ? addDeleteViewCommand(semanticCommand, destroyRequest) : null;
    }
    return semanticCommand;
  }

  /**
   * @generated
   */
  protected Command addDeleteViewCommand(Command mainCommand, DestroyRequest completedRequest)
  {
    Command deleteViewCommand = getGEFWrapper(new DeleteCommand(getEditingDomain(), (View)getHost().getModel()));
    return mainCommand == null ? deleteViewCommand : mainCommand.chain(deleteViewCommand);
  }

  /**
   * @generated
   */
  private Command getEditHelperCommand(IEditCommandRequest request, Command editPolicyCommand)
  {
    if (editPolicyCommand != null)
    {
      ICommand command = editPolicyCommand instanceof ICommandProxy ? ((ICommandProxy)editPolicyCommand).getICommand()
          : new CommandProxy(editPolicyCommand);
      request.setParameter(AcoreBaseEditHelper.EDIT_POLICY_COMMAND, command);
    }
    IElementType requestContextElementType = getContextElementType(request);
    request.setParameter(AcoreBaseEditHelper.CONTEXT_ELEMENT_TYPE, requestContextElementType);
    ICommand command = requestContextElementType.getEditCommand(request);
    request.setParameter(AcoreBaseEditHelper.EDIT_POLICY_COMMAND, null);
    request.setParameter(AcoreBaseEditHelper.CONTEXT_ELEMENT_TYPE, null);
    if (command != null)
    {
      if (!(command instanceof CompositeTransactionalCommand))
      {
        command = new CompositeTransactionalCommand(getEditingDomain(), command.getLabel()).compose(command);
      }
      return new ICommandProxy(command);
    }
    return editPolicyCommand;
  }

  /**
   * @generated
   */
  private IElementType getContextElementType(IEditCommandRequest request)
  {
    IElementType requestContextElementType = AcoreElementTypes.getElementType(getVisualID(request));
    return requestContextElementType != null ? requestContextElementType : myElementType;
  }

  /**
   * @generated
   */
  protected Command getSemanticCommandSwitch(IEditCommandRequest req)
  {
    if (req instanceof CreateRelationshipRequest)
    {
      return getCreateRelationshipCommand((CreateRelationshipRequest)req);
    }
    else if (req instanceof CreateElementRequest)
    {
      return getCreateCommand((CreateElementRequest)req);
    }
    else if (req instanceof ConfigureRequest)
    {
      return getConfigureCommand((ConfigureRequest)req);
    }
    else if (req instanceof DestroyElementRequest)
    {
      return getDestroyElementCommand((DestroyElementRequest)req);
    }
    else if (req instanceof DestroyReferenceRequest)
    {
      return getDestroyReferenceCommand((DestroyReferenceRequest)req);
    }
    else if (req instanceof DuplicateElementsRequest)
    {
      return getDuplicateCommand((DuplicateElementsRequest)req);
    }
    else if (req instanceof GetEditContextRequest)
    {
      return getEditContextCommand((GetEditContextRequest)req);
    }
    else if (req instanceof MoveRequest)
    {
      return getMoveCommand((MoveRequest)req);
    }
    else if (req instanceof ReorientReferenceRelationshipRequest)
    {
      return getReorientReferenceRelationshipCommand((ReorientReferenceRelationshipRequest)req);
    }
    else if (req instanceof ReorientRelationshipRequest)
    {
      return getReorientRelationshipCommand((ReorientRelationshipRequest)req);
    }
    else if (req instanceof SetRequest)
    {
      return getSetCommand((SetRequest)req);
    }
    return null;
  }

  /**
   * @generated
   */
  protected Command getConfigureCommand(ConfigureRequest req)
  {
    return null;
  }

  /**
   * @generated
   */
  protected Command getCreateRelationshipCommand(CreateRelationshipRequest req)
  {
    return null;
  }

  /**
   * @generated
   */
  protected Command getCreateCommand(CreateElementRequest req)
  {
    return null;
  }

  /**
   * @generated
   */
  protected Command getSetCommand(SetRequest req)
  {
    return null;
  }

  /**
   * @generated
   */
  protected Command getEditContextCommand(GetEditContextRequest req)
  {
    return null;
  }

  /**
   * @generated
   */
  protected Command getDestroyElementCommand(DestroyElementRequest req)
  {
    return null;
  }

  /**
   * @generated
   */
  protected Command getDestroyReferenceCommand(DestroyReferenceRequest req)
  {
    return null;
  }

  /**
   * @generated
   */
  protected Command getDuplicateCommand(DuplicateElementsRequest req)
  {
    return null;
  }

  /**
   * @generated
   */
  protected Command getMoveCommand(MoveRequest req)
  {
    return null;
  }

  /**
   * @generated
   */
  protected Command getReorientReferenceRelationshipCommand(ReorientReferenceRelationshipRequest req)
  {
    return UnexecutableCommand.INSTANCE;
  }

  /**
   * @generated
   */
  protected Command getReorientRelationshipCommand(ReorientRelationshipRequest req)
  {
    return UnexecutableCommand.INSTANCE;
  }

  /**
   * @generated
   */
  protected final Command getGEFWrapper(ICommand cmd)
  {
    return new ICommandProxy(cmd);
  }

  /**
   * Returns editing domain from the host edit part.
   *
   * @generated
   */
  protected TransactionalEditingDomain getEditingDomain()
  {
    return ((IGraphicalEditPart)getHost()).getEditingDomain();
  }

  /**
   * Clean all shortcuts to the host element from the same diagram
   *
   * @generated
   */
  protected void addDestroyShortcutsCommand(ICompositeCommand cmd, View view)
  {
    assert view.getEAnnotation("Shortcut") == null; //$NON-NLS-1$
    for (Iterator it = view.getDiagram().getChildren().iterator(); it.hasNext();)
    {
      View nextView = (View)it.next();
      if (nextView.getEAnnotation("Shortcut") == null || !nextView.isSetElement() //$NON-NLS-1$
          || nextView.getElement() != view.getElement())
      {
        continue;
      }
      cmd.add(new DeleteCommand(getEditingDomain(), nextView));
    }
  }

  /**
   * @generated
   */
  public static class LinkConstraints
  {

    /**
     * @generated
     */
    public static boolean canCreateAClassSubClasses_4001(AClass source, AClass target)
    {
      if (source != null)
      {
        if (source.getSubClasses().contains(target))
        {
          return false;
        }
      }

      return canExistAClassSubClasses_4001(source, target);
    }

    /**
     * @generated
     */
    public static boolean canCreateAClassImplementedInterfaces_4002(AClass source, AInterface target)
    {
      if (source != null)
      {
        if (source.getImplementedInterfaces().contains(target))
        {
          return false;
        }
      }

      return canExistAClassImplementedInterfaces_4002(source, target);
    }

    /**
     * @generated
     */
    public static boolean canCreateAClassAssociations_4003(AClass source, AClass target)
    {
      if (source != null)
      {
        if (source.getAssociations().contains(target))
        {
          return false;
        }
      }

      return canExistAClassAssociations_4003(source, target);
    }

    /**
     * @generated
     */
    public static boolean canCreateAClassAggregations_4004(AClass source, AClass target)
    {
      if (source != null)
      {
        if (source.getAggregations().contains(target))
        {
          return false;
        }
      }

      return canExistAClassAggregations_4004(source, target);
    }

    /**
     * @generated
     */
    public static boolean canCreateAClassCompositions_4005(AClass source, AClass target)
    {
      if (source != null)
      {
        if (source.getCompositions().contains(target))
        {
          return false;
        }
      }

      return canExistAClassCompositions_4005(source, target);
    }

    /**
     * @generated
     */
    public static boolean canExistAClassSubClasses_4001(AClass source, AClass target)
    {
      return true;
    }

    /**
     * @generated
     */
    public static boolean canExistAClassImplementedInterfaces_4002(AClass source, AInterface target)
    {
      return true;
    }

    /**
     * @generated
     */
    public static boolean canExistAClassAssociations_4003(AClass source, AClass target)
    {
      return true;
    }

    /**
     * @generated
     */
    public static boolean canExistAClassAggregations_4004(AClass source, AClass target)
    {
      return true;
    }

    /**
     * @generated
     */
    public static boolean canExistAClassCompositions_4005(AClass source, AClass target)
    {
      return true;
    }
  }

}
