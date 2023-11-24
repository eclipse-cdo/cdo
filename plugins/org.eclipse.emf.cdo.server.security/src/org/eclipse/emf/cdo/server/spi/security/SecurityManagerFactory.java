/*
 * Copyright (c) 2012, 2013, 2015, 2016, 2019, 2021-2023 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.server.IRepository;
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
  public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.security.managers"; //$NON-NLS-1$

  /**
   * @since 4.9
   */
  public static final String DESCRIPTION_SEPARATOR = ":"; //$NON-NLS-1$

  private static final String QUALIFIED_DESCRIPTION_FORMAT = "%s" + DESCRIPTION_SEPARATOR + "%s"; //$NON-NLS-1$ //$NON-NLS-2$

  public SecurityManagerFactory(String type)
  {
    super(PRODUCT_GROUP, type);
  }

  @Override
  public ISecurityManager create(String description) throws ProductCreationException
  {
    List<String> tokens = StringUtil.split(description, DESCRIPTION_SEPARATOR, "()");
    String repositoryName = tokens.remove(0);
    return create(repositoryName, tokens);
  }

  /**
   * @since 4.9
   */
  protected ISecurityManager create(String repositoryName, List<String> description) throws ProductCreationException
  {
    throw new ProductCreationException("Subclasses must implement one of the create() methods");
  }

  /**
   * @since 4.9
   */
  public static String getQualifiedDescription(String repositoryName, String description)
  {
    return String.format(QUALIFIED_DESCRIPTION_FORMAT, repositoryName, description);
  }

  /**
   * @since 4.9
   */
  public static ISecurityManager get(IManagedContainer container, String type, String repositoryName, String description)
  {
    String qualifiedDescription = getQualifiedDescription(repositoryName, description);
    return get(container, type, qualifiedDescription);
  }

  /**
   * @since 4.9
   */
  public static ISecurityManager get(IManagedContainer container, String type, String qualifiedDescription)
  {
    return (ISecurityManager)container.getElement(PRODUCT_GROUP, type, qualifiedDescription);
  }

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
      this(TYPE);
    }

    /**
     * @since 4.11
     */
    protected Default(String type)
    {
      super(type);
    }

    /**
     * @since 4.11
     */
    public IManagedContainer getManagedContainer()
    {
      return container;
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
    protected ISecurityManager create(String repositoryName, List<String> description) throws ProductCreationException
    {
      String realmPath = description.remove(0);
      ISecurityManager securityManager = createSecurityManager(realmPath);

      for (String token : description)
      {
        CommitHandler handler = getHandler(container, token);
        ((InternalSecurityManager)securityManager).addCommitHandler(handler);
      }

      if (securityManager instanceof InternalSecurityManager)
      {
        IRepository repository = RepositoryFactory.get(container, repositoryName);
        ((InternalSecurityManager)securityManager).setRepository((InternalRepository)repository);
      }

      return securityManager;
    }

    /**
     * @since 4.11
     */
    protected ISecurityManager createSecurityManager(String realmPath)
    {
      return SecurityManagerUtil.createSecurityManager(realmPath, container);
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
      return get(container, TYPE, repositoryName, description);
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
