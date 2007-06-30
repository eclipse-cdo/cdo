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
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.internal.server.RepositoryImpl;
import org.eclipse.emf.cdo.internal.server.ResourceManagerImpl;
import org.eclipse.emf.cdo.internal.server.RevisionManagerImpl;
import org.eclipse.emf.cdo.internal.server.SessionImpl;
import org.eclipse.emf.cdo.internal.server.SessionManagerImpl;

import org.eclipse.net4j.signal.IndicationWithResponse;

/**
 * @author Eike Stepper
 */
public abstract class CDOServerIndication extends IndicationWithResponse
{
  private short signalID;

  public CDOServerIndication(short signalID)
  {
    this.signalID = signalID;
  }

  @Override
  protected short getSignalID()
  {
    return signalID;
  }

  protected SessionManagerImpl getSessionManager()
  {
    return getSession().getSessionManager();
  }

  protected RevisionManagerImpl getRevisionManager()
  {
    return getRepository().getRevisionManager();
  }

  protected ResourceManagerImpl getResourceManager()
  {
    return getRepository().getResourceManager();
  }

  protected RepositoryImpl getRepository()
  {
    return getSessionManager().getRepository();
  }

  protected SessionImpl getSession()
  {
    return getProtocol().getSession();
  }

  protected CDOServerProtocol getProtocol()
  {
    return (CDOServerProtocol)super.getProtocol();
  }
}
