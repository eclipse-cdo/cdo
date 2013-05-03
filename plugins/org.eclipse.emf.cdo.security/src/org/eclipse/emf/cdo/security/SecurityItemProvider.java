/*
 * Copyright (c) 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.security;

/**
 * Provides {@link SecurityItem security items} such as {@link User users} or {@link Role roles} by their given IDs.
 *
 * @author Eike Stepper
 * @since 4.2
 */
public interface SecurityItemProvider
{
  public Role getRole(String id);

  public Group getGroup(String id);

  public User getUser(String id);
}
