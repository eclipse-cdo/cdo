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
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.security.operations.AuthorizableOperation;
import org.eclipse.net4j.util.security.operations.OperationAuthorizer;
import org.eclipse.net4j.util.security.operations.OperationAuthorizerFactory;

import java.util.Map;
import java.util.Objects;

/**
 * @author Eike Stepper
 * @since 4.15
 */
public abstract class AbstractOperationAuthorizer<CONTEXT> implements OperationAuthorizer<CONTEXT>
{
  private final String operationID;

  public AbstractOperationAuthorizer(String operationID)
  {
    this.operationID = operationID;
  }

  public final String getOperationID()
  {
    return operationID;
  }

  @Override
  public String authorizeOperation(CONTEXT context, AuthorizableOperation operation)
  {
    if (!Objects.equals(operationID, operation.getID()))
    {
      return null;
    }

    Map<String, Object> parameters = operation.getParameters();
    return authorizeOperation(context, parameters);
  }

  protected abstract String authorizeOperation(CONTEXT context, Map<String, Object> parameters);

  /**
   * @author Eike Stepper
   */
  public static abstract class Factory<CONTEXT> extends OperationAuthorizerFactory<CONTEXT>
  {
    public Factory(String type)
    {
      super(type);
    }

    @Override
    public OperationAuthorizer<CONTEXT> create(String description) throws ProductCreationException
    {
      if (StringUtil.isEmpty(description))
      {
        throw new ProductCreationException("No description: " + this);
      }

      int colon = description.indexOf(':');
      String operationID = colon == -1 ? description : description.substring(0, colon);
      description = colon == -1 ? null : description.substring(colon + 1);
      return create(operationID, description);
    }

    protected abstract OperationAuthorizer<CONTEXT> create(String operationID, String description) throws ProductCreationException;
  }
}
