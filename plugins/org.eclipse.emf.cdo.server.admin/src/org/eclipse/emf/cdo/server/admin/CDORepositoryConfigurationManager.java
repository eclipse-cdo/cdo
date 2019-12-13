/*
 * Copyright (c) 2013, 2014, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.server.admin;

import org.eclipse.emf.cdo.server.IRepository;

import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.lifecycle.ILifecycle;

import org.w3c.dom.Document;

import java.util.Map;

/**
 * Manages repository configurations.
 *
 * @author Christian W. Damus (CEA LIST)
 * @since 4.2
 */
public interface CDORepositoryConfigurationManager extends ILifecycle
{
  public Map<String, IRepository> getRepositories();

  public IRepository addRepository(String name, Document configuration);

  public void removeRepository(IRepository repository);

  public boolean canRemoveRepository(IRepository repository);

  /**
   * Authenticates the user as a server administrator, if applicable.
   *
   * @throws SecurityException if authentication is required and fails
   */
  public void authenticateAdministrator() throws SecurityException;

  /**
   * Specification of the factory API for {@link CDORepositoryConfigurationManager repository configuration managers}.
   *
   * @author Christian W. Damus (CEA LIST)
   */
  public static abstract class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.admin.repositoryConfigurationManagers"; //$NON-NLS-1$

    public Factory(String type)
    {
      super(PRODUCT_GROUP, type);
    }

    @Override
    public abstract CDORepositoryConfigurationManager create(String description) throws ProductCreationException;
  }
}
