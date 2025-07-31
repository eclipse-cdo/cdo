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
package org.eclipse.emf.cdo.lm.reviews.ui;

import org.eclipse.emf.cdo.lm.Module;
import org.eclipse.emf.cdo.lm.ui.actions.NewChangeAction;
import org.eclipse.emf.cdo.lm.util.LMLocalOperationAuthorizer;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.net4j.util.collection.Entity;
import org.eclipse.net4j.util.security.operations.AuthorizableOperation;

import java.util.function.Supplier;

/**
 * @author Eike Stepper
 */
public class TestAuthorizer extends LMLocalOperationAuthorizer
{
  public TestAuthorizer()
  {
    int xxx;
    // 1. Remove this class
    // 2. Cleanup this plugin.xml
    // 3. Cleanup LM cdo-server.xml
  }

  @Override
  public Object authorizeOperation(CDOSession session, Supplier<Entity> userInfoSupplier, AuthorizableOperation operation)
  {
    Entity userInfo = userInfoSupplier.get();

    if (operation.getID() == NewChangeAction.OPERATION_ID)
    {
      Module module = getContextModule(operation);
      return module != null && module.getName().equals("Mod1") ? GRANTED : "Change creation not allowed";
    }

    return null;
  }
}
