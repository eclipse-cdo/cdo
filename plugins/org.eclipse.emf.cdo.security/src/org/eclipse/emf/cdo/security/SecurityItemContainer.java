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
package org.eclipse.emf.cdo.security;

/**
 * A {@link SecurityItemProvider security item provider} that {@link SecurityItem items} such as {@link User users} or {@link Role roles} can be added to or removed from.
 * @author Eike Stepper
 * @since 4.2
 */
public interface SecurityItemContainer extends SecurityItemProvider
{
  public Role addRole(String id);

  public Group addGroup(String id);

  public User addUser(String id);

  public User addUser(String id, String password);

  public Role removeRole(String id);

  public Group removeGroup(String id);

  public User removeUser(String id);
}
