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

import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.commands.AClassCreateCommand;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.commands.AInterfaceCreateCommand;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.providers.AcoreElementTypes;

import org.eclipse.emf.transaction.TransactionalEditingDomain;

import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.emf.commands.core.commands.DuplicateEObjectsCommand;
import org.eclipse.gmf.runtime.emf.type.core.requests.CreateElementRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.DuplicateElementsRequest;

/**
 * @generated
 */
public class ACoreRootItemSemanticEditPolicy extends AcoreBaseItemSemanticEditPolicy
{

  /**
   * @generated
   */
  public ACoreRootItemSemanticEditPolicy()
  {
    super(AcoreElementTypes.ACoreRoot_1000);
  }

  /**
   * @generated
   */
  @Override
  protected Command getCreateCommand(CreateElementRequest req)
  {
    if (AcoreElementTypes.AInterface_2001 == req.getElementType())
    {
      return getGEFWrapper(new AInterfaceCreateCommand(req));
    }
    if (AcoreElementTypes.AClass_2002 == req.getElementType())
    {
      return getGEFWrapper(new AClassCreateCommand(req));
    }
    return super.getCreateCommand(req);
  }

  /**
   * @generated
   */
  @Override
  protected Command getDuplicateCommand(DuplicateElementsRequest req)
  {
    TransactionalEditingDomain editingDomain = ((IGraphicalEditPart)getHost()).getEditingDomain();
    return getGEFWrapper(new DuplicateAnythingCommand(editingDomain, req));
  }

  /**
   * @generated
   */
  private static class DuplicateAnythingCommand extends DuplicateEObjectsCommand
  {

    /**
     * @generated
     */
    public DuplicateAnythingCommand(TransactionalEditingDomain editingDomain, DuplicateElementsRequest req)
    {
      super(editingDomain, req.getLabel(), req.getElementsToBeDuplicated(), req.getAllDuplicatedElementsMap());
    }

  }

}
