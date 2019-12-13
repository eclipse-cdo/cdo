/*
 * Copyright (c) 2009-2013, 2016, 2017, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.lob.CDOLobStore;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.common.security.CDOPermissionProvider;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.spi.common.protocol.CDODataInputImpl;
import org.eclipse.emf.cdo.spi.common.protocol.CDODataOutputImpl;
import org.eclipse.emf.cdo.spi.common.revision.CDORevisionUnchunker;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalView;

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

  protected InternalSession getSession()
  {
    return getProtocol().getSession();
  }

  protected InternalRepository getRepository()
  {
    InternalRepository repository = getSession().getManager().getRepository();
    if (!LifecycleUtil.isActive(repository))
    {
      throw new IllegalStateException("CDORepositoryInfo has been deactivated"); //$NON-NLS-1$
    }

    return repository;
  }

  protected IStore getStore()
  {
    IStore store = getRepository().getStore();
    if (!LifecycleUtil.isActive(store))
    {
      throw new IllegalStateException("Store has been deactivated"); //$NON-NLS-1$
    }

    return store;
  }

  protected InternalView getView(int viewID)
  {
    InternalSession session = getSession();
    return session.getView(viewID);
  }

  @Override
  protected String getAdditionalInfo()
  {
    String additionalInfo = super.getAdditionalInfo();

    String userID = getSession().getUserID();
    if (userID != null)
    {
      additionalInfo += ", user=" + userID;
    }

    return additionalInfo;
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws Exception
  {
    indicating(new CDODataInputImpl(in)
    {
      @Override
      public CDOPackageRegistry getPackageRegistry()
      {
        return getRepository().getPackageRegistry();
      }

      @Override
      protected boolean isXCompression()
      {
        return CDOProtocolConstants.X_COMPRESSION;
      }

      @Override
      protected StringIO getPackageURICompressor()
      {
        return getProtocol().getPackageURICompressor();
      }

      @Override
      protected CDOBranchManager getBranchManager()
      {
        return getRepository().getBranchManager();
      }

      @Override
      protected CDOCommitInfoManager getCommitInfoManager()
      {
        return getRepository().getCommitInfoManager();
      }

      @Override
      protected CDORevisionFactory getRevisionFactory()
      {
        return getRepository().getRevisionManager().getFactory();
      }

      @Override
      protected CDOLobStore getLobStore()
      {
        return null; // Not used on server
      }

      @Override
      protected CDOListFactory getListFactory()
      {
        return CDOListFactory.DEFAULT;
      }
    });
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws Exception
  {
    responding(new CDODataOutputImpl(out)
    {
      @Override
      public CDOPackageRegistry getPackageRegistry()
      {
        return getRepository().getPackageRegistry();
      }

      @Override
      public CDORevisionUnchunker getRevisionUnchunker()
      {
        return getRepository();
      }

      @Override
      public CDOIDProvider getIDProvider()
      {
        return getSession();
      }

      @Override
      public CDOPermissionProvider getPermissionProvider()
      {
        return getSession();
      }

      @Override
      protected boolean isXCompression()
      {
        return CDOProtocolConstants.X_COMPRESSION;
      }

      @Override
      protected StringIO getPackageURICompressor()
      {
        return getProtocol().getPackageURICompressor();
      }
    });
  }

  protected abstract void indicating(CDODataInput in) throws IOException;

  protected abstract void responding(CDODataOutput out) throws IOException;
}
