/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
public class EMFDerivedContainerValue extends ContainerValue
{
  public EMFDerivedContainerValue(Scope evaluationBlock)
  {
    super(evaluationBlock);
  }

  @Override
  protected void initVariables(Variables variables) throws DebugException
  {
    variables.add("uri", "return org.eclipse.emf.ecore.util.EcoreUtil.getURI(this).toString();");
    variables.add("eResource", "return eResource();");
    variables.add("eContents", "return ((org.eclipse.emf.ecore.util.InternalEList)eContents()).basicList();");
    variables.add("eCrossReferences",
        "return ((org.eclipse.emf.ecore.util.InternalEList)eCrossReferences()).basicList();");
  }
}
