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
package org.eclipse.emf.cdo.lm.util;

import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.lm.Baseline;
import org.eclipse.emf.cdo.util.ContextOperationAuthorization;

import org.eclipse.net4j.util.security.operations.AuthorizableOperation;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.spi.cdo.CDOOperationAuthorizer;

/**
 * @author Eike Stepper
 * @since 1.4
 */
public abstract class LMOperationAuthorizer implements CDOOperationAuthorizer, LMOperations
{
  public LMOperationAuthorizer()
  {
  }

  public static Baseline getContextBaseline(AuthorizableOperation operation)
  {
    EObject object = ContextOperationAuthorization.getContext(operation);
    return EMFUtil.getNearestObject(object, Baseline.class);
  }

  public static org.eclipse.emf.cdo.lm.Stream getContextStream(AuthorizableOperation operation)
  {
    EObject object = ContextOperationAuthorization.getContext(operation);
    return EMFUtil.getNearestObject(object, org.eclipse.emf.cdo.lm.Stream.class);
  }

  public static org.eclipse.emf.cdo.lm.Module getContextModule(AuthorizableOperation operation)
  {
    EObject object = ContextOperationAuthorization.getContext(operation);
    return EMFUtil.getNearestObject(object, org.eclipse.emf.cdo.lm.Module.class);
  }

  public static org.eclipse.emf.cdo.lm.System getContextSystem(AuthorizableOperation operation)
  {
    EObject object = ContextOperationAuthorization.getContext(operation);
    return EMFUtil.getNearestObject(object, org.eclipse.emf.cdo.lm.System.class);
  }
}
