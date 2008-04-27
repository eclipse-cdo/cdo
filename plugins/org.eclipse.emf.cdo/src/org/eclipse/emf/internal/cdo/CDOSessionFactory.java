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
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOSession;

import org.eclipse.net4j.internal.util.factory.Factory;
import org.eclipse.net4j.signal.failover.IFailOverStrategy;
import org.eclipse.net4j.util.container.IManagedContainer;

import org.eclipse.emf.common.util.URI;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * @author Eike Stepper
 */
public class CDOSessionFactory extends Factory
{
  public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.sessions";

  public static final String TYPE = "cdo";

  public CDOSessionFactory()
  {
    super(PRODUCT_GROUP, TYPE);
  }

  public CDOSession create(String description)
  {
    return createSession(getRepositoryName(description), isDisableLegacyObjects(description),
        isAutomaticPackageRegistry(description), null);
  }

  public static String getRepositoryName(String description)
  {
    URI uri = URI.createURI(description);
    IPath path = new Path(uri.path());
    return path.segment(0);
  }

  public boolean isDisableLegacyObjects(String description)
  {
    return description.contains("disableLegacyObjects=true");
  }

  public boolean isAutomaticPackageRegistry(String description)
  {
    return description.contains("automaticPackageRegistry=true");
  }

  public static CDOSession get(IManagedContainer container, String description)
  {
    return (CDOSession)container.getElement(PRODUCT_GROUP, TYPE, description);
  }

  public static CDOSessionImpl createSession(String repositoryName, boolean disableLegacyObjects,
      boolean automaticPackageRegistry, IFailOverStrategy failOverStrategy)
  {
    CDOSessionImpl session = new CDOSessionImpl();
    if (automaticPackageRegistry)
    {
      session.setPackageRegistry(new CDOPackageRegistryImpl.SelfPopulating(session));
    }

    session.setRepositoryName(repositoryName);
    session.setDisableLegacyObjects(disableLegacyObjects);
    session.setFailOverStrategy(failOverStrategy);
    return session;
  }
}
