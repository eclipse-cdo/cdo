/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.common.security.CDOPermission;

/**
 * Provides the protection level of protectable objects in the context of a specific user.
 *
 * @author Eike Stepper
 * @since 4.1
 */
public interface IPermissionManager
{
  public CDOPermission getPermission(Object protectableObject, String userID);
}
