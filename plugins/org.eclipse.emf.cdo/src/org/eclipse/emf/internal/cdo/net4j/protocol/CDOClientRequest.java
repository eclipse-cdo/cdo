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
package org.eclipse.emf.internal.cdo.net4j.protocol;

import org.eclipse.emf.cdo.common.id.CDOIDObjectFactory;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.model.CDOPackageManager;
import org.eclipse.emf.cdo.common.model.CDOPackageURICompressor;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisionResolver;
import org.eclipse.emf.cdo.internal.common.io.CDODataInputImpl;
import org.eclipse.emf.cdo.internal.common.io.CDODataOutputImpl;
import org.eclipse.emf.cdo.session.CDORevisionManager;
import org.eclipse.emf.cdo.session.CDOSessionPackageManager;

import org.eclipse.emf.internal.cdo.revision.CDOListWithElementProxiesImpl;

import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public abstract class CDOClientRequest<RESULT> extends RequestWithConfirmation<RESULT>
{
  public CDOClientRequest(CDOClientProtocol protocol, short signalID)
  {
    super(protocol, signalID);
  }

  @Override
  public CDOClientProtocol getProtocol()
  {
    return (CDOClientProtocol)super.getProtocol();
  }

  protected InternalCDOSession getSession()
  {
    return (InternalCDOSession)getProtocol().getInfraStructure();
  }

  protected CDORevisionManager getRevisionManager()
  {
    return getSession().getRevisionManager();
  }

  protected CDOSessionPackageManager getPackageManager()
  {
    return getSession().getPackageManager();
  }

  protected CDOPackageURICompressor getPackageURICompressor()
  {
    return getSession();
  }

  protected CDOIDProvider getIDProvider()
  {
    throw new UnsupportedOperationException();
  }

  protected CDOIDObjectFactory getIDFactory()
  {
    return getSession();
  }

  @Override
  protected final void requesting(ExtendedDataOutputStream out) throws Exception
  {
    requesting(new CDODataOutputImpl(out)
    {
      @Override
      protected CDOPackageURICompressor getPackageURICompressor()
      {
        return CDOClientRequest.this.getPackageURICompressor();
      }

      public CDOIDProvider getIDProvider()
      {
        return CDOClientRequest.this.getIDProvider();
      }
    });
  }

  @Override
  protected final RESULT confirming(ExtendedDataInputStream in) throws Exception
  {
    return confirming(new CDODataInputImpl(in)
    {
      @Override
      protected CDORevisionResolver getRevisionResolver()
      {
        return CDOClientRequest.this.getRevisionManager();
      }

      @Override
      protected CDOPackageManager getPackageManager()
      {
        return CDOClientRequest.this.getPackageManager();
      }

      @Override
      protected CDOPackageURICompressor getPackageURICompressor()
      {
        return CDOClientRequest.this.getPackageURICompressor();
      }

      @Override
      protected CDOIDObjectFactory getIDFactory()
      {
        return CDOClientRequest.this.getIDFactory();
      }

      @Override
      protected CDOListFactory getListFactory()
      {
        return CDOListWithElementProxiesImpl.FACTORY;
      }
    });
  }

  protected abstract void requesting(CDODataOutput out) throws IOException;

  protected abstract RESULT confirming(CDODataInput in) throws IOException;
}
