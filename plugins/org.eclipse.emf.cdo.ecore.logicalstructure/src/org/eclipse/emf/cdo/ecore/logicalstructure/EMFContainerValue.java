/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ecore.logicalstructure;

import org.eclipse.emf.cdo.ecore.logicalstructure.util.ContainerValue;
import org.eclipse.emf.cdo.ecore.logicalstructure.util.Scope;

import org.eclipse.debug.core.DebugException;

/**
 * @author Eike Stepper
 */
public class EMFContainerValue extends ContainerValue
{
  public EMFContainerValue(Scope evaluationBlock)
  {
    super(evaluationBlock);
  }

  @Override
  protected void initVariables(Variables variables) throws DebugException
  {
    ContainerValue derivedContainer = new EMFDerivedContainerValue(getScope());
    variables.add("[derived]", derivedContainer);

    variables.add("eProxyURI", "return eProxyURI();");
    variables.add("eAdapters", "return eAdapters();");
    variables.add("eDeliver", "return eDeliver();");
    variables.add("eDirectResource", "return eDirectResource();");
    variables.add("eInternalContainer", "return eInternalContainer();");
    variables.add("eContainingFeature", "return eContainingFeature();");
    variables.add("eContainmentFeature", "return eContainmentFeature();");
  }
}
