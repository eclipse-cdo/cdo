/*
 * Copyright (c) 2012, 2013, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.security;

import org.eclipse.net4j.util.security.IPasswordCredentials;

/**
 * A {@link SecurityItemProvider security item provider} that {@link SecurityItem items}
 * such as {@link User users} or {@link Role roles} can be added to or removed from.
 *
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @author Eike Stepper
 * @since 4.2
 */
public interface SecurityItemContainer extends SecurityItemProvider
{
  public Role addRole(String id);

  public Group addGroup(String id);

  public User addUser(String id);

  public User addUser(String id, String password);

  /**
   * @since 4.3
   */
  public User addUser(IPasswordCredentials credentials);

  /**
   * @since 4.3
   */
  public User setPassword(String id, String password);

  public Role removeRole(String id);

  public Group removeGroup(String id);

  public User removeUser(String id);
}
