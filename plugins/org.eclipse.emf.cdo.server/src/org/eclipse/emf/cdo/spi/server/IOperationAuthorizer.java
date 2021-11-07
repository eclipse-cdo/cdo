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

import org.eclipse.emf.cdo.common.CDOCommonSession;
import org.eclipse.emf.cdo.common.CDOCommonSession.AuthorizableOperation;

/**
 * @author Eike Stepper
 * @since 4.15
 */
public interface IOperationAuthorizer
{
  public String authorizeOperation(CDOCommonSession session, AuthorizableOperation operation);
}
