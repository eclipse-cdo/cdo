/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui;

import org.eclipse.emf.cdo.common.CDOCommonSession.AuthorizableOperation;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * @author Eike Stepper
 * @since 4.11
 */
public final class UIOperations
{
  public static final AuthorizableOperation CREATE_BRANCHES = new AuthorizableOperation("org.eclipse.emf.cdo.ui.CreateBranches");

  public static final AuthorizableOperation RENAME_BRANCHES = new AuthorizableOperation("org.eclipse.emf.cdo.ui.RenameBranches");

  public static final AuthorizableOperation DELETE_BRANCHES = new AuthorizableOperation("org.eclipse.emf.cdo.ui.DeleteBranches");

  public static final AuthorizableOperation[] VALID_OPERATIONS = { CREATE_BRANCHES, RENAME_BRANCHES, DELETE_BRANCHES };

  private static final Map<CDOSession, Set<AuthorizableOperation>> OPERATIONS_PER_SESSION = new WeakHashMap<>();

  private UIOperations()
  {
  }

  public static AuthorizableOperation getOperation(String operationID)
  {
    for (AuthorizableOperation operation : VALID_OPERATIONS)
    {
      if (Objects.equals(operation.getID(), operationID))
      {
        return operation;
      }
    }

    return null;
  }

  public static boolean isAuthorized(CDOSession session, String operationID)
  {
    return isAuthorized(session, getOperation(operationID));
  }

  public static boolean isAuthorized(CDOSession session, AuthorizableOperation operation)
  {
    if (isValid(operation))
    {
      synchronized (OPERATIONS_PER_SESSION)
      {
        Set<AuthorizableOperation> operations = OPERATIONS_PER_SESSION.get(session);
        if (operations == null)
        {
          operations = new HashSet<>();
          OPERATIONS_PER_SESSION.put(session, operations);

          session.addListener(new LifecycleEventAdapter()
          {
            @Override
            protected void onDeactivated(ILifecycle lifecycle)
            {
              synchronized (OPERATIONS_PER_SESSION)
              {
                OPERATIONS_PER_SESSION.remove(session);
              }
            }
          });

          String[] results = session.authorizeOperations(VALID_OPERATIONS);
          for (int i = 0; i < results.length; i++)
          {
            if (results[i] == null)
            {
              operations.add(VALID_OPERATIONS[i]);
            }
          }
        }

        return operations.contains(operation);
      }
    }

    return false;
  }

  private static boolean isValid(AuthorizableOperation operation)
  {
    return operation != null && getOperation(operation.getID()) != null;
  }
}
