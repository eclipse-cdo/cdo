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
package org.eclipse.emf.cdo.server.spi.security;

import org.eclipse.emf.cdo.server.security.ISecurityManager;
import org.eclipse.emf.cdo.server.security.SecurityManagerUtil;

import org.eclipse.net4j.util.factory.Factory;
import org.eclipse.net4j.util.factory.ProductCreationException;

/**
 * @author Eike Stepper
 */
public abstract class SecurityManagerFactory extends Factory
{
  public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.security.managers";

  public SecurityManagerFactory(String type)
  {
    super(PRODUCT_GROUP, type);
  }

  public abstract ISecurityManager create(String realmPath) throws ProductCreationException;

  /**
   * @author Eike Stepper
   */
  public static class Default extends SecurityManagerFactory
  {
    private static final String TYPE = "default";

    public Default()
    {
      super(TYPE);
    }

    @Override
    public ISecurityManager create(String realmPath) throws ProductCreationException
    {
      return SecurityManagerUtil.createSecurityManager(realmPath);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Annotation extends SecurityManagerFactory
  {
    private static final String TYPE = "annotation";

    public Annotation()
    {
      super(TYPE);
    }

    @Override
    public ISecurityManager create(String realmPath) throws ProductCreationException
    {
      InternalSecurityManager securityManager = (InternalSecurityManager)SecurityManagerUtil
          .createSecurityManager(realmPath);

      AnnotationHandler handler = new AnnotationHandler();
      securityManager.addCommitHandler(handler);

      return securityManager;
    }
  }
}
