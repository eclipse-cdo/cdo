/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.server.embedded;

import org.eclipse.emf.cdo.server.embedded.CDOSession;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.emf.internal.cdo.session.CDOSessionImpl;

/**
 * @author Eike Stepper
 */
public class EmbeddedClientSession extends CDOSessionImpl implements CDOSession
{
  public EmbeddedClientSession(EmbeddedClientSessionConfiguration configuration)
  {
    super(configuration);
  }

  @Override
  public EmbeddedClientSessionConfiguration getConfiguration()
  {
    return (EmbeddedClientSessionConfiguration)super.getConfiguration();
  }

  public InternalRepository getRepository()
  {
    return getConfiguration().getRepository();
  }

  public InternalCDOPackageRegistry getPackageRegistry()
  {
    return getRepository().getPackageRegistry();
  }

  public InternalCDOBranchManager getBranchManager()
  {
    return getRepository().getBranchManager();
  }

  public InternalCDORevisionManager getRevisionManager()
  {
    return getConfiguration().getRevisionManager();
  }
}
