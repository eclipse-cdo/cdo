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
package org.eclipse.emf.cdo.server.security;

import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.internal.security.AnnotationRoleProvider;

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

  public static ISecurityManager createSecurityManager(IRepository repository, String realmPath)
  {
    return createSecurityManager(repository, realmPath, IPluginContainer.INSTANCE);
  }

  public static ISecurityManager createSecurityManager(IRepository repository, String realmPath,
      IManagedContainer container)
  {
    return new org.eclipse.emf.cdo.server.internal.security.SecurityManager(repository, realmPath, container);
  }

  public static void prepareContainer(IManagedContainer container)
  {
    container.registerFactory(new AnnotationRoleProvider.Factory());
  }
}
