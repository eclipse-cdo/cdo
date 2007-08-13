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

import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.internal.server.PackageManager;
import org.eclipse.emf.cdo.internal.server.ResourceManager;
import org.eclipse.emf.cdo.internal.server.RevisionManager;
import org.eclipse.emf.cdo.internal.server.Session;
import org.eclipse.emf.cdo.internal.server.SessionManager;
import org.eclipse.emf.cdo.server.IStore;

import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.util.transaction.TX;
import org.eclipse.net4j.util.transaction.TransactionException;

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

  protected PackageManager getPackageManager()
  {
    return getRepository().getPackageManager();
  }

  protected SessionManager getSessionManager()
  {
    return getSession().getSessionManager();
  }

  protected RevisionManager getRevisionManager()
  {
    return getRepository().getRevisionManager();
  }

  protected ResourceManager getResourceManager()
  {
    return getRepository().getResourceManager();
  }

  protected IStore getStore()
  {
    return getRepository().getStore();
  }

  protected Repository getRepository()
  {
    return getSessionManager().getRepository();
  }

  protected Session getSession()
  {
    return getProtocol().getSession();
  }

  @Override
  protected CDOServerProtocol getProtocol()
  {
    return (CDOServerProtocol)super.getProtocol();
  }

  protected void transact(Runnable runnable) throws TransactionException
  {
    TX.begin();

    try
    {
      runnable.run();
      TX.commit();
    }
    catch (RuntimeException ex)
    {
      ex.printStackTrace();
      TX.rollback();
    }
  }
}
