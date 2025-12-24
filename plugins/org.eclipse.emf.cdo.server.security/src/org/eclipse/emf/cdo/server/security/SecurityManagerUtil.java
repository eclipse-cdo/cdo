/*
 * Copyright (c) 2012, 2013, 2021, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.security;

import org.eclipse.emf.cdo.security.Access;
import org.eclipse.emf.cdo.security.PatternStyle;
import org.eclipse.emf.cdo.security.Realm;
import org.eclipse.emf.cdo.security.Role;
import org.eclipse.emf.cdo.security.SecurityFactory;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.internal.security.SecurityManager;
import org.eclipse.emf.cdo.server.internal.security.bundle.OM;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;

/**
 * Static factory methods for creating {@link ISecurityManager security managers}.
 *
 * @author Eike Stepper
 */
public final class SecurityManagerUtil
{
  private SecurityManagerUtil()
  {
  }

  /**
   * @since 4.3
   */
  public static ISecurityManager getSecurityManager(IRepository repository)
  {
    return SecurityManager.get(repository);
  }

  public static ISecurityManager createSecurityManager(String realmPath)
  {
    return createSecurityManager(realmPath, IPluginContainer.INSTANCE);
  }

  public static ISecurityManager createSecurityManager(String realmPath, IManagedContainer container)
  {
    return new SecurityManager(realmPath, container);
  }

  /**
   * @since 4.10
   */
  public static Role addResourceRole(Realm realm, String roleName, String resourcePath, boolean writable)
  {
    Role role = realm.addRole(roleName);
    addResourcePermissions(role, resourcePath, writable);
    return role;
  }

  /**
   * @since 4.10
   */
  public static void addResourcePermissions(Role role, String resourcePath, boolean writable)
  {
    role.getPermissions().add(SecurityFactory.eINSTANCE.createFilterPermission(Access.READ, //
        SecurityFactory.eINSTANCE.createResourceFilter(resourcePath, PatternStyle.EXACT, true)));

    if (writable)
    {
      role.getPermissions().add(SecurityFactory.eINSTANCE.createFilterPermission(Access.WRITE, //
          SecurityFactory.eINSTANCE.createResourceFilter(resourcePath, PatternStyle.EXACT, false)));
    }
  }

  public static void prepareContainer(IManagedContainer container)
  {
    OM.BUNDLE.prepareContainer(container);
  }
}
