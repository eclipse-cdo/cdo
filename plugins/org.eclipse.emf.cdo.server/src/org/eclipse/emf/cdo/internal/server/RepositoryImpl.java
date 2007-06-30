/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.internal.protocol.CDOIDImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.server.Repository;

import java.util.UUID;

/**
 * @author Eike Stepper
 */
public class RepositoryImpl implements Repository
{
  private static final long INITIAL_OID_VALUE = 1;

  private String name;

  private String uuid;

  private SessionManagerImpl sessionManager;

  private ResourceManagerImpl resourceManager;

  private RevisionManagerImpl revisionManager;

  private long nextOIDValue = INITIAL_OID_VALUE;

  public RepositoryImpl(String name)
  {
    this.name = name;
    this.uuid = UUID.randomUUID().toString();
    sessionManager = new SessionManagerImpl(this);
    resourceManager = new ResourceManagerImpl(this);
    revisionManager = new RevisionManagerImpl(this);
  }

  public String getName()
  {
    return name;
  }

  public String getUUID()
  {
    return uuid;
  }

  public SessionManagerImpl getSessionManager()
  {
    return sessionManager;
  }

  public ResourceManagerImpl getResourceManager()
  {
    return resourceManager;
  }

  public RevisionManagerImpl getRevisionManager()
  {
    return revisionManager;
  }

  public CDOID getNextCDOID()
  {
    return CDOIDImpl.create(nextOIDValue++);
  }
}
