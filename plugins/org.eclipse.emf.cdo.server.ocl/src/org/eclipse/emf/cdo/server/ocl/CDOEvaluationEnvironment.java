/*
 * Copyright (c) 2013, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.server.ocl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.ocl.EvaluationEnvironment;
import org.eclipse.ocl.ecore.EcoreEnvironmentFactory;
import org.eclipse.ocl.ecore.EcoreEvaluationEnvironment;

/**
 * A CDO-specific specialization of the OCL evaluation environment.
 *
 * @author Christian W. Damus
 * @since 4.2
 */
class CDOEvaluationEnvironment extends EcoreEvaluationEnvironment
{
  public CDOEvaluationEnvironment(EcoreEnvironmentFactory factory)
  {
    super(factory);
  }

  public CDOEvaluationEnvironment(EvaluationEnvironment<EClassifier, EOperation, EStructuralFeature, EClass, EObject> parent)
  {
    super(parent);
  }

  @Override
  public Object callOperation(EOperation operation, int opcode, Object source, Object[] args) throws IllegalArgumentException
  {
    CDOAdditionalOperation cdoOperation = CDOAdditionalOperation.getInstance(operation);
    if (cdoOperation != null)
    {
      return cdoOperation.evaluate(this, source, args);
    }

    return super.callOperation(operation, opcode, source, args);
  }
}
