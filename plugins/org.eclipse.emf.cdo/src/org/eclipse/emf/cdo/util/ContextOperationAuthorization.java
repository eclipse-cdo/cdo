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
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.net4j.util.security.operations.AuthorizableOperation;

import org.eclipse.emf.ecore.EObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 4.28
 */
public final class ContextOperationAuthorization
{
  public static final String CONTEXT_PARAMETER = "context";

  private final EObject context;

  private final Map<String, String> operationIDToVeto = new HashMap<>();

  private ContextOperationAuthorization(EObject context, String[] operationIDs, String[] vetoes)
  {
    this.context = context;

    for (int i = 0; i < vetoes.length; i++)
    {
      operationIDToVeto.put(operationIDs[i], vetoes[i]);
    }
  }

  public ContextOperationAuthorization(String... operationIDs)
  {
    this(null, operationIDs, new String[operationIDs.length]);
  }

  public ContextOperationAuthorization(EObject context, String... operationIDs)
  {
    this(context, operationIDs, authorize(context, operationIDs));
  }

  public EObject getContext()
  {
    return context;
  }

  public Set<String> getOperationsIDs()
  {
    return Collections.unmodifiableSet(operationIDToVeto.keySet());
  }

  public String getVeto(String operationID)
  {
    return operationIDToVeto.get(operationID);
  }

  public boolean isDenied(String operationID)
  {
    return getVeto(operationID) != null;
  }

  public boolean isGranted(String operationID)
  {
    return !isDenied(operationID);
  }

  private static CDOSession getSessionSafe(EObject context)
  {
    try
    {
      return CDOUtil.getSession(context);
    }
    catch (Exception ex)
    {
      return null;
    }
  }

  private static String[] authorize(EObject context, String... operationIDs)
  {
    CDOSession session = getSessionSafe(context);
    if (session != null)
    {
      AuthorizableOperation[] operations = new AuthorizableOperation[operationIDs.length];

      for (int i = 0; i < operationIDs.length; i++)
      {
        operations[i] = AuthorizableOperation.builder(operationIDs[i]) //
            .parameter(CONTEXT_PARAMETER, context) //
            .build();
      }

      return session.authorizeOperations(operations);
    }

    return new String[operationIDs.length];
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
}
