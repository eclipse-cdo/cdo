/*
 * Copyright (c) 2012, 2013, 2015, 2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA LIST) - bug 420644
 */
package org.eclipse.emf.cdo.server.spi.security;

import org.eclipse.emf.cdo.server.internal.security.bundle.OM;
import org.eclipse.emf.cdo.server.security.ISecurityManager;
import org.eclipse.emf.cdo.server.security.SecurityManagerUtil;
import org.eclipse.emf.cdo.server.spi.security.InternalSecurityManager.CommitHandler;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.RepositoryFactory;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IManagedContainer.ContainerAware;
import org.eclipse.net4j.util.factory.Factory;
import org.eclipse.net4j.util.factory.ProductCreationException;

import java.util.List;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 */
public abstract class SecurityManagerFactory extends Factory
{
  public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.security.managers";

  public SecurityManagerFactory(String type)
  {
    super(PRODUCT_GROUP, type);
  }

  @Override
  public abstract ISecurityManager create(String realmPath) throws ProductCreationException;

  /**
   * If the meaning of this type isn't clear, there really should be more of a description here...
   *
   * @author Eike Stepper
   */
  public static class Default extends SecurityManagerFactory implements ContainerAware
  {
    /**
     * @since 4.3
     */
    public static final String TYPE = "default";

    private IManagedContainer container;

    public Default()
    {
      super(TYPE);
    }

    /**
     * @since 4.3
     */
    @Override
    public void setManagedContainer(IManagedContainer container)
    {
      this.container = container;
    }

    @Override
    public ISecurityManager create(String description) throws ProductCreationException
    {
      List<String> tokens = StringUtil.split(description, ":", "()");
      String repositoryName = tokens.get(0);
      String realmPath = tokens.get(1);

      ISecurityManager securityManager = SecurityManagerUtil.createSecurityManager(realmPath);

      for (int i = 2; i < tokens.size(); i++)
      {
        String token = tokens.get(i);
        CommitHandler handler = getHandler(container, token);
        ((InternalSecurityManager)securityManager).addCommitHandler(handler);
      }

      if (securityManager instanceof InternalSecurityManager)
      {
        ((InternalSecurityManager)securityManager).setRepository((InternalRepository)RepositoryFactory.get(container, repositoryName));
      }

      return securityManager;
    }

    /**
     * @since 4.3
     */
    protected CommitHandler getHandler(IManagedContainer container, String token)
    {
      String factoryType;
      String description;

      int pos = token.indexOf('(');
      if (pos == -1)
      {
        factoryType = token.trim();
        description = null;
      }
      else
      {
        factoryType = token.substring(0, pos).trim();
        description = token.substring(pos + 1, token.length() - 1).trim();
      }

      return (CommitHandler)container.getElement(CommitHandler.Factory.PRODUCT_GROUP, factoryType, description);
    }

    /**
     * @since 4.8
     */
    public static ISecurityManager create(IManagedContainer container, String repositoryName, String description)
    {
      String qualifiedDescription = String.format("%s:%s", repositoryName, description); //$NON-NLS-1$
      return (ISecurityManager)container.getElement(PRODUCT_GROUP, TYPE, qualifiedDescription);
    }
  }

  /**
   * If the meaning of this type isn't clear, there really should be more of a description here...
   *
   * @author Eike Stepper
   * @deprecated As of 4.3 use {@link Default} with a description like "realmPath<b>:annotation</b>".
   */
  @Deprecated
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
      OM.LOG.warn(
          "SecurityManagerFactory.Annotation is deprecated. As of 4.3 use SecurityManagerFactory.Default with a description like \"realmPath:annotation\"");

      InternalSecurityManager securityManager = (InternalSecurityManager)SecurityManagerUtil.createSecurityManager(realmPath);

      AnnotationHandler handler = new AnnotationHandler();
      securityManager.addCommitHandler(handler);

      return securityManager;
    }
  }
}
