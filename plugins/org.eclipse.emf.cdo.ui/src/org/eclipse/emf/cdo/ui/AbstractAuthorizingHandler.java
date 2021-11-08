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
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.ui.handlers.AbstractBaseHandler;

/**
 * @author Eike Stepper
 * @since 4.11
 */
public class AbstractAuthorizingHandler<T> extends AbstractBaseHandler<T>
{
  private final AuthorizableOperation authorizableOperation;

  public AbstractAuthorizingHandler(Class<T> type, String operationID)
  {
    super(type, false);
    authorizableOperation = new AuthorizableOperation(operationID);
  }

  @Override
  public boolean isEnabled()
  {
    if (elements != null && !elements.isEmpty())
    {
      T element = elements.get(0);

      CDOSession session = CDOUtil.getSession(element);
      if (session != null)
      {
        String result = session.authorizeOperations(authorizableOperation)[0];
        if (result == null)
        {
          return true;
        }
      }
    }

    return false;
  }
}
