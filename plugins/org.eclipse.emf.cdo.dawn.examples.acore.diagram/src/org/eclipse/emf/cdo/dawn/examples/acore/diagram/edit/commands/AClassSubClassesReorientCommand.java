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
package org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.commands;

import org.eclipse.emf.cdo.dawn.examples.acore.AClass;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.policies.AcoreBaseItemSemanticEditPolicy;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.emf.type.core.commands.EditElementCommand;
import org.eclipse.gmf.runtime.emf.type.core.requests.ReorientReferenceRelationshipRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.ReorientRelationshipRequest;

/**
 * @generated
 */
public class AClassSubClassesReorientCommand extends EditElementCommand
{

  /**
   * @generated
   */
  private final int reorientDirection;

  /**
   * @generated
   */
  private final EObject referenceOwner;

  /**
   * @generated
   */
  private final EObject oldEnd;

  /**
   * @generated
   */
  private final EObject newEnd;

  /**
   * @generated
   */
  public AClassSubClassesReorientCommand(ReorientReferenceRelationshipRequest request)
  {
    super(request.getLabel(), null, request);
    reorientDirection = request.getDirection();
    referenceOwner = request.getReferenceOwner();
    oldEnd = request.getOldRelationshipEnd();
    newEnd = request.getNewRelationshipEnd();
  }

  /**
   * @generated
   */
  public boolean canExecute()
  {
    if (false == referenceOwner instanceof AClass)
    {
      return false;
    }
    if (reorientDirection == ReorientRelationshipRequest.REORIENT_SOURCE)
    {
      return canReorientSource();
    }
    if (reorientDirection == ReorientRelationshipRequest.REORIENT_TARGET)
    {
      return canReorientTarget();
    }
    return false;
  }

  /**
   * @generated
   */
  protected boolean canReorientSource()
  {
    if (!(oldEnd instanceof AClass && newEnd instanceof AClass))
    {
      return false;
    }
    return AcoreBaseItemSemanticEditPolicy.LinkConstraints
        .canExistAClassSubClasses_4001(getNewSource(), getOldTarget());
  }

  /**
   * @generated
   */
  protected boolean canReorientTarget()
  {
    if (!(oldEnd instanceof AClass && newEnd instanceof AClass))
    {
      return false;
    }
    return AcoreBaseItemSemanticEditPolicy.LinkConstraints
        .canExistAClassSubClasses_4001(getOldSource(), getNewTarget());
  }

  /**
   * @generated
   */
  protected CommandResult doExecuteWithResult(IProgressMonitor monitor, IAdaptable info) throws ExecutionException
  {
    if (!canExecute())
    {
      throw new ExecutionException("Invalid arguments in reorient link command"); //$NON-NLS-1$
    }
    if (reorientDirection == ReorientRelationshipRequest.REORIENT_SOURCE)
    {
      return reorientSource();
    }
    if (reorientDirection == ReorientRelationshipRequest.REORIENT_TARGET)
    {
      return reorientTarget();
    }
    throw new IllegalStateException();
  }

  /**
   * @generated
   */
  protected CommandResult reorientSource() throws ExecutionException
  {
    getOldSource().getSubClasses().remove(getOldTarget());
    getNewSource().getSubClasses().add(getOldTarget());
    return CommandResult.newOKCommandResult(referenceOwner);
  }

  /**
   * @generated
   */
  protected CommandResult reorientTarget() throws ExecutionException
  {
    getOldSource().getSubClasses().remove(getOldTarget());
    getOldSource().getSubClasses().add(getNewTarget());
    return CommandResult.newOKCommandResult(referenceOwner);
  }

  /**
   * @generated
   */
  protected AClass getOldSource()
  {
    return (AClass)referenceOwner;
  }

  /**
   * @generated
   */
  protected AClass getNewSource()
  {
    return (AClass)newEnd;
  }

  /**
   * @generated
   */
  protected AClass getOldTarget()
  {
    return (AClass)oldEnd;
  }

  /**
   * @generated
   */
  protected AClass getNewTarget()
  {
    return (AClass)newEnd;
  }
}
