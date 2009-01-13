/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo.net4j;

import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.net4j.CDOSessionConfiguration;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.emf.internal.cdo.session.CDOPackageRegistryImpl;
import org.eclipse.emf.internal.cdo.session.CDOSessionFactory;

import org.eclipse.net4j.signal.failover.IFailOverStrategy;
import org.eclipse.net4j.util.container.IManagedContainer;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

/**
 * @author Eike Stepper
 */
public class CDONet4jSessionFactory extends CDOSessionFactory
{

  public static final String TYPE = "cdo";

  public CDONet4jSessionFactory()
  {
    super(TYPE);
  }

  public static CDOSession get(IManagedContainer container, String description)
  {
    return (CDOSession)container.getElement(PRODUCT_GROUP, TYPE, description);
  }

  /**
   * @since 2.0
   */
  @Override
  protected InternalCDOSession createSession(String repositoryName, boolean automaticPackageRegistry,
      IFailOverStrategy failOverStrategy)
  {
    CDOSessionConfiguration configuration = CDONet4jUtil.createSessionConfiguration();
    configuration.setRepositoryName(repositoryName);
    configuration.setFailOverStrategy(failOverStrategy);
    if (automaticPackageRegistry)
    {
      configuration.setPackageRegistry(new CDOPackageRegistryImpl.Eager());
    }

    return (InternalCDOSession)configuration.openSession();
  }
}
