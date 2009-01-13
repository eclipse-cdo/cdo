/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/233490    
 **************************************************************************/
package org.eclipse.emf.internal.cdo.net4j.protocol;

import org.eclipse.emf.cdo.common.id.CDOIDObjectFactory;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.model.CDOPackageManager;
import org.eclipse.emf.cdo.common.model.CDOPackageURICompressor;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisionResolver;
import org.eclipse.emf.cdo.internal.common.io.CDODataInputImpl;
import org.eclipse.emf.cdo.session.CDORevisionManager;
import org.eclipse.emf.cdo.session.CDOSessionPackageManager;

import org.eclipse.emf.internal.cdo.revision.CDOListWithElementProxiesImpl;

import org.eclipse.net4j.signal.Indication;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public abstract class CDOClientIndication extends Indication
{
  public CDOClientIndication(CDOClientProtocol protocol, short signalID)
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

  protected CDOIDObjectFactory getIDFactory()
  {
    return getSession();
  }

  @Override
  protected final void indicating(ExtendedDataInputStream in) throws Exception
  {
    indicating(new CDODataInputImpl(in)
    {
      @Override
      protected CDORevision readCDORevisionData() throws IOException
      {
        CDORevisionFactory factory = getSession().options().getRevisionFactory();
        return factory.createRevision(this);
      }

      @Override
      protected CDORevisionResolver getRevisionResolver()
      {
        return CDOClientIndication.this.getRevisionManager();
      }

      @Override
      protected CDOPackageManager getPackageManager()
      {
        return CDOClientIndication.this.getPackageManager();
      }

      @Override
      protected CDOPackageURICompressor getPackageURICompressor()
      {
        return CDOClientIndication.this.getPackageURICompressor();
      }

      @Override
      protected CDOIDObjectFactory getIDFactory()
      {
        return CDOClientIndication.this.getIDFactory();
      }

      @Override
      protected CDOListFactory getListFactory()
      {
        return CDOListWithElementProxiesImpl.FACTORY;
      }
    });
  }

  protected abstract void indicating(CDODataInput in) throws IOException;
}
