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
public class AClassAssociationsReorientCommand extends EditElementCommand
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
  public AClassAssociationsReorientCommand(ReorientReferenceRelationshipRequest request)
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
  @Override
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
    return AcoreBaseItemSemanticEditPolicy.LinkConstraints.canExistAClassAssociations_4003(getNewSource(), getOldTarget());
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
    return AcoreBaseItemSemanticEditPolicy.LinkConstraints.canExistAClassAssociations_4003(getOldSource(), getNewTarget());
  }

  /**
   * @generated
   */
  @Override
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
    getOldSource().getAssociations().remove(getOldTarget());
    getNewSource().getAssociations().add(getOldTarget());
    return CommandResult.newOKCommandResult(referenceOwner);
  }

  /**
   * @generated
   */
  protected CommandResult reorientTarget() throws ExecutionException
  {
    getOldSource().getAssociations().remove(getOldTarget());
    getOldSource().getAssociations().add(getNewTarget());
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
