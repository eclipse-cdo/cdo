/*
 * Copyright (c) 2021, 2024, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui;

import org.eclipse.emf.cdo.internal.ui.handlers.CreateBranchHandler;
import org.eclipse.emf.cdo.internal.ui.handlers.DeleteBranchHandler;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.security.operations.AuthorizableOperation;
import org.eclipse.net4j.util.ui.handlers.AbstractBaseHandler;

/**
 * An abstract base class for {@link AbstractBaseHandler handlers} that automatically update their
 * {@link #isEnabled() enablement} based on an {@link AuthorizableOperation authorizable operation}.
 *
 * @author Eike Stepper
 * @since 4.11
 * @see CreateBranchHandler
 * @see DeleteBranchHandler
 */
public class AbstractAuthorizingHandler<T> extends AbstractBaseHandler<T>
{
  private final AuthorizableOperation authorizableOperation;

  public AbstractAuthorizingHandler(Class<T> type, String operationID)
  {
    super(type, false);
    authorizableOperation = AuthorizableOperation.build(operationID);
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
        String veto = session.authorizeOperations(authorizableOperation)[0];
        return veto == null;
      }
    }

    return false;
  }
}
