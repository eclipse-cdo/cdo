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
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.protocol.id.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOClassRef;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;
import org.eclipse.emf.cdo.protocol.model.CDOPackage;
import org.eclipse.emf.cdo.protocol.model.CDOPackageInfo;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStoreChunkReader;
import org.eclipse.emf.cdo.server.IStoreReader;
import org.eclipse.emf.cdo.server.IStoreWriter;
import org.eclipse.emf.cdo.server.IView;

import org.eclipse.net4j.util.io.CloseableIterator;

import java.util.Collection;
import java.util.Collections;

/**
 * @author Eike Stepper
 */
public class NOOPStoreAccessor extends StoreAccessor implements IStoreReader, IStoreWriter
{
  public NOOPStoreAccessor(NOOPStore store, ISession session)
  {
    super(store, session);
  }

  public NOOPStoreAccessor(NOOPStore store, IView view)
  {
    super(store, view);
  }

  @Override
  public NOOPStore getStore()
  {
    return (NOOPStore)super.getStore();
  }

  public IStoreChunkReader createChunkReader(CDORevision revision, CDOFeature feature)
  {
    return new NOOPStoreChunkReader(this, revision, feature);
  }

  public Collection<CDOPackageInfo> readPackageInfos()
  {
    return Collections.emptySet();
  }

  public void readPackage(CDOPackage cdoPackage)
  {
    throw new UnsupportedOperationException();
  }

  public CloseableIterator<CDOID> readObjectIDs(boolean withTypes)
  {
    throw new UnsupportedOperationException();
  }

  public CDOClassRef readObjectType(CDOID id)
  {
    throw new UnsupportedOperationException();
  }

  public CDORevision readRevision(CDOID id, int referenceChunk)
  {
    throw new UnsupportedOperationException();
  }

  public CDORevision readRevisionByTime(CDOID id, int referenceChunk, long timeStamp)
  {
    throw new UnsupportedOperationException();
  }

  public CDORevision readRevisionByVersion(CDOID id, int referenceChunk, int version)
  {
    throw new UnsupportedOperationException();
  }

  public CDOID readResourceID(String path)
  {
    throw new UnsupportedOperationException();
  }

  public String readResourcePath(CDOID id)
  {
    throw new UnsupportedOperationException();
  }
}
