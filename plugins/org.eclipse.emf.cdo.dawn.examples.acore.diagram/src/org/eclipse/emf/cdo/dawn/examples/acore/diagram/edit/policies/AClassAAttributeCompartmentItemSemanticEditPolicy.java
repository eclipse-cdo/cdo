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
package org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.policies;

import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.commands.AAttribute2CreateCommand;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.providers.AcoreElementTypes;

import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.emf.type.core.requests.CreateElementRequest;

/**
 * @generated
 */
public class AClassAAttributeCompartmentItemSemanticEditPolicy extends AcoreBaseItemSemanticEditPolicy
{

  /**
   * @generated
   */
  public AClassAAttributeCompartmentItemSemanticEditPolicy()
  {
    super(AcoreElementTypes.AClass_2002);
  }

  /**
   * @generated
   */
  protected Command getCreateCommand(CreateElementRequest req)
  {
    if (AcoreElementTypes.AAttribute_3003 == req.getElementType())
    {
      return getGEFWrapper(new AAttribute2CreateCommand(req));
    }
    return super.getCreateCommand(req);
  }

}
