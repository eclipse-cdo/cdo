/*
 * Copyright (c) 2012, 2013, 2021 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.server.internal.security.RealmOperationAuthorizer;
import org.eclipse.emf.cdo.server.internal.security.SecurityManager;
import org.eclipse.emf.cdo.server.spi.security.AnnotationHandler;
import org.eclipse.emf.cdo.server.spi.security.HomeFolderHandler;
import org.eclipse.emf.cdo.server.spi.security.SecurityManagerFactory;

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
    return new org.eclipse.emf.cdo.server.internal.security.SecurityManager(realmPath, container);
  }

  @SuppressWarnings("deprecation")
  public static void prepareContainer(IManagedContainer container)
  {
    container.registerFactory(new SecurityManagerFactory.Default());
    container.registerFactory(new SecurityManagerFactory.Annotation());
    container.registerFactory(new AnnotationHandler.Factory());
    container.registerFactory(new HomeFolderHandler.Factory());
    container.registerFactory(new RealmOperationAuthorizer.RequireUser.Factory());
    container.registerFactory(new RealmOperationAuthorizer.RequireGroup.Factory());
    container.registerFactory(new RealmOperationAuthorizer.RequireRole.Factory());
  }
}
