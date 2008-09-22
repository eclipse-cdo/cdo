/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.common.CDODataInput;
import org.eclipse.emf.cdo.common.CDODataOutput;
import org.eclipse.emf.cdo.common.id.CDOIDObjectFactory;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.model.CDOPackageManager;
import org.eclipse.emf.cdo.common.model.CDOPackageURICompressor;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisionResolver;
import org.eclipse.emf.cdo.internal.common.CDODataInputImpl;
import org.eclipse.emf.cdo.internal.common.CDODataOutputImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDOListImpl;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.internal.server.ResourceManager;
import org.eclipse.emf.cdo.internal.server.RevisionManager;
import org.eclipse.emf.cdo.internal.server.Session;
import org.eclipse.emf.cdo.internal.server.SessionManager;
import org.eclipse.emf.cdo.server.IStore;

import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public abstract class CDOServerIndication extends IndicationWithResponse
{
  public CDOServerIndication()
  {
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

  protected CDOPackageManager getPackageManager()
  {
    return getRepository().getPackageManager();
  }

  protected ResourceManager getResourceManager()
  {
    return getRepository().getResourceManager();
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
  protected final void indicating(ExtendedDataInputStream in) throws IOException
  {
    indicating(new CDODataInputImpl(in)
    {
      @Override
      protected CDORevisionResolver getRevisionResolver()
      {
        return CDOServerIndication.this.getRevisionManager();
      }

      @Override
      protected CDOPackageManager getPackageManager()
      {
        return CDOServerIndication.this.getPackageManager();
      }

      @Override
      protected CDOPackageURICompressor getPackageURICompressor()
      {
        return CDOServerIndication.this.getPackageURICompressor();
      }

      @Override
      protected CDOIDObjectFactory getIDFactory()
      {
        return CDOServerIndication.this.getIDFactory();
      }

      @Override
      protected CDOListFactory getListFactory()
      {
        return CDOListImpl.FACTORY;
      }
    });
  }

  @Override
  protected final void responding(ExtendedDataOutputStream out) throws IOException
  {
    responding(new CDODataOutputImpl(out)
    {
      @Override
      protected CDOPackageURICompressor getPackageURICompressor()
      {
        return CDOServerIndication.this.getPackageURICompressor();
      }

      public CDOIDProvider getIDProvider()
      {
        return CDOServerIndication.this.getIDProvider();
      }
    });
  }

  protected abstract void indicating(CDODataInput in) throws IOException;

  protected abstract void responding(CDODataOutput out) throws IOException;
}
