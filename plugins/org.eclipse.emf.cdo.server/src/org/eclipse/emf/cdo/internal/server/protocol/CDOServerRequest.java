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
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.common.CDODataOutput;
import org.eclipse.emf.cdo.common.id.CDOIDObjectFactory;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.model.CDOPackageURICompressor;
import org.eclipse.emf.cdo.internal.common.CDODataOutputImpl;
import org.eclipse.emf.cdo.internal.server.PackageManager;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.internal.server.RevisionManager;
import org.eclipse.emf.cdo.internal.server.Session;
import org.eclipse.emf.cdo.internal.server.SessionManager;
import org.eclipse.emf.cdo.server.IStore;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.signal.Request;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public abstract class CDOServerRequest extends Request
{
  public CDOServerRequest(IChannel channel)
  {
    super(extractProtocol(channel));
  }

  @Override
  public CDOServerProtocol getProtocol()
  {
    return (CDOServerProtocol)super.getProtocol();
  }

  protected Session getSession()
  {
    return getProtocol().getSession();
  }

  protected CDOPackageURICompressor getPackageURICompressor()
  {
    return getSession();
  }

  protected CDOIDProvider getIDProvider()
  {
    return getSession();
  }

  protected CDOIDObjectFactory getIDFactory()
  {
    return getStore().getCDOIDObjectFactory();
  }

  protected SessionManager getSessionManager()
  {
    return getSession().getSessionManager();
  }

  protected Repository getRepository()
  {
    Repository repository = (Repository)getSessionManager().getRepository();
    if (!repository.isActive())
    {
      throw new IllegalStateException("Repository has been deactivated");
    }

    return repository;
  }

  protected RevisionManager getRevisionManager()
  {
    return getRepository().getRevisionManager();
  }

  protected PackageManager getPackageManager()
  {
    return getRepository().getPackageManager();
  }

  protected IStore getStore()
  {
    IStore store = getRepository().getStore();
    if (!LifecycleUtil.isActive(store))
    {
      throw new IllegalStateException("Store has been deactivated");
    }

    return store;
  }

  @Override
  protected final void requesting(ExtendedDataOutputStream out) throws IOException
  {
    requesting(new CDODataOutputImpl(out)
    {
      @Override
      protected CDOPackageURICompressor getPackageURICompressor()
      {
        return CDOServerRequest.this.getPackageURICompressor();
      }

      public CDOIDProvider getIDProvider()
      {
        return CDOServerRequest.this.getIDProvider();
      }
    });
  }

  protected abstract void requesting(CDODataOutput out) throws IOException;

  private static SignalProtocol extractProtocol(IChannel channel)
  {
    if (LifecycleUtil.isActive(channel))
    {
      return (SignalProtocol)channel.getReceiveHandler();
    }

    throw new IllegalStateException("Channel is inactive: " + channel);
  }
}
