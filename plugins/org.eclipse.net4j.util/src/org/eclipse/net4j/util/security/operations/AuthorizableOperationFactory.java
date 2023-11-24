/*
 * Copyright (c) 2021-2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.security.operations;

import org.eclipse.net4j.util.collection.CollectionUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.Factory;
import org.eclipse.net4j.util.factory.ProductCreationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 3.16
 */
public final class AuthorizableOperationFactory extends Factory
{
  public static final String PRODUCT_GROUP = "org.eclipse.net4j.util.security.authorizableOperations"; //$NON-NLS-1$

  public AuthorizableOperationFactory()
  {
    super(PRODUCT_GROUP);
  }

  public String getOperationID()
  {
    return getType();
  }

  @Override
  public AuthorizableOperation create(String description) throws ProductCreationException
  {
    return AuthorizableOperation.build(getOperationID());
  }

  public static AuthorizableOperation getAuthorizableOperation(IManagedContainer container, String operationID)
  {
    AuthorizableOperation operation = container.getElementOrNull(PRODUCT_GROUP, operationID, NO_DESCRIPTION);
    if (operation == null)
    {
      operation = AuthorizableOperation.build(operationID);
    }

    return operation;
  }

  public static AuthorizableOperation[] getAuthorizableOperations(IManagedContainer container)
  {
    Set<String> operationIDs = container.getFactoryTypes(PRODUCT_GROUP);
    List<AuthorizableOperation> operations = new ArrayList<>();

    for (String operationID : operationIDs)
    {
      AuthorizableOperation operation = container.getElementOrNull(PRODUCT_GROUP, operationID, NO_DESCRIPTION);
      CollectionUtil.addNotNull(operations, operation);
    }

    return operations.toArray(new AuthorizableOperation[operations.size()]);
  }
}
