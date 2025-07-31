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
package org.eclipse.emf.cdo.lm.util;

import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.lm.Baseline;
import org.eclipse.emf.cdo.session.CDOSession.LocalOperationAuthorizer;

import org.eclipse.net4j.util.security.operations.AuthorizableOperation;

import org.eclipse.emf.ecore.EObject;

/**
 * @author Eike Stepper
 * @since 1.4
 */
public abstract class LMLocalOperationAuthorizer implements LocalOperationAuthorizer
{
  public static final String CONTEXT_PARAMETER = "context";

  public LMLocalOperationAuthorizer()
  {
  }

  public static EObject getContext(AuthorizableOperation operation)
  {
    Object context = operation.getParameter(CONTEXT_PARAMETER);
    if (context instanceof EObject)
    {
      return (EObject)context;
    }

    return null;
  }

  public static Baseline getContextBaseline(AuthorizableOperation operation)
  {
    EObject object = getContext(operation);
    return EMFUtil.getNearestObject(object, Baseline.class);
  }

  public static org.eclipse.emf.cdo.lm.Stream getContextStream(AuthorizableOperation operation)
  {
    EObject object = getContext(operation);
    return EMFUtil.getNearestObject(object, org.eclipse.emf.cdo.lm.Stream.class);
  }

  public static org.eclipse.emf.cdo.lm.Module getContextModule(AuthorizableOperation operation)
  {
    EObject object = getContext(operation);
    return EMFUtil.getNearestObject(object, org.eclipse.emf.cdo.lm.Module.class);
  }

  public static org.eclipse.emf.cdo.lm.System getContextSystem(AuthorizableOperation operation)
  {
    EObject object = getContext(operation);
    return EMFUtil.getNearestObject(object, org.eclipse.emf.cdo.lm.System.class);
  }
}
