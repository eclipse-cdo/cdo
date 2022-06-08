/*
 * Copyright (c) 2009-2013, 2016, 2017, 2021, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.spi.common.protocol.CDODataOutputImpl;
import org.eclipse.emf.cdo.spi.common.revision.CDORevisionUnchunker;
import org.eclipse.emf.cdo.spi.server.InternalSession;

import org.eclipse.net4j.signal.Request;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.io.StringIO;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public abstract class CDOServerRequest extends Request
{
  public CDOServerRequest(CDOServerProtocol serverProtocol, short signalID)
  {
    super(serverProtocol, signalID);
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
  protected final void requesting(ExtendedDataOutputStream out) throws Exception
  {
    requesting(new CDODataOutputImpl(out)
    {
      @Override
      public InternalSession getSession()
      {
        return CDOServerRequest.this.getSession();
      }

      @Override
      public CDOPackageRegistry getPackageRegistry()
      {
        return getSession().getRepository().getPackageRegistry();
      }

      @Override
      public CDORevisionUnchunker getRevisionUnchunker()
      {
        return getSession().getRepository();
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
      public CDOIDProvider getIDProvider()
      {
        return getSession();
      }
    });
  }

  protected abstract void requesting(CDODataOutput out) throws IOException;
}
