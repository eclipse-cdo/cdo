/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 */
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.common.id.CDOIDObjectFactory;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisionResolver;
import org.eclipse.emf.cdo.internal.common.io.CDODataInputImpl;
import org.eclipse.emf.cdo.internal.common.io.CDODataOutputImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDOListImpl;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.internal.server.Session;
import org.eclipse.emf.cdo.server.IStore;

import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.io.StringIO;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public abstract class CDOServerIndication extends IndicationWithResponse
{
  public CDOServerIndication(CDOServerProtocol protocol, short signalID)
  {
    super(protocol, signalID);
  }

  @Override
  public CDOServerProtocol getProtocol()
  {
    return (CDOServerProtocol)super.getProtocol();
  }

  protected Session getSession()
  {
    return (Session)getProtocol().getSession();
  }

  protected Repository getRepository()
  {
    Repository repository = (Repository)getSession().getSessionManager().getRepository();
    if (!LifecycleUtil.isActive(repository))
    {
      throw new IllegalStateException("Repository has been deactivated");
    }

    return repository;
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
  protected final void indicating(ExtendedDataInputStream in) throws Exception
  {
    indicating(new CDODataInputImpl(in)
    {
      @Override
      protected CDORevisionResolver getRevisionResolver()
      {
        return getRepository().getRevisionManager();
      }

      @Override
      protected CDOPackageRegistry getPackageRegistry()
      {
        return getRepository().getPackageRegistry();
      }

      @Override
      protected StringIO getPackageURICompressor()
      {
        return getProtocol().getPackageURICompressor();
      }

      @Override
      protected CDOIDObjectFactory getIDFactory()
      {
        return getStore().getCDOIDObjectFactory();
      }

      @Override
      protected CDOListFactory getListFactory()
      {
        return CDOListImpl.FACTORY;
      }
    });
  }

  @Override
  protected final void responding(ExtendedDataOutputStream out) throws Exception
  {
    responding(new CDODataOutputImpl(out)
    {
      @Override
      protected StringIO getPackageURICompressor()
      {
        return getProtocol().getPackageURICompressor();
      }

      public CDOIDProvider getIDProvider()
      {
        return getSession();
      }
    });
  }

  protected abstract void indicating(CDODataInput in) throws IOException;

  protected abstract void responding(CDODataOutput out) throws IOException;
}
