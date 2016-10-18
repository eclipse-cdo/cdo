/*
 * Copyright (c) 2010-2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Ibrahim Sallam - code refactoring for CDO 3.0
 */
package org.eclipse.emf.cdo.server.internal.objectivity;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyObject;
import org.eclipse.emf.cdo.server.objectivity.IObjectivityStoreChunkReader;
import org.eclipse.emf.cdo.spi.server.StoreChunkReader;

import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.List;

public class ObjectivityStoreChunkReader extends StoreChunkReader implements IObjectivityStoreChunkReader
{
  public ObjectivityStoreChunkReader(IStoreAccessor accessor, CDORevision revision, EStructuralFeature feature)
  {
    super(accessor, revision, feature);
    // TODO Auto-generated constructor stub
  }

  @Override
  public ObjectivityStoreAccessor getAccessor()
  {
    return (ObjectivityStoreAccessor)super.getAccessor();
  }

  public List<Chunk> executeRead()
  {
    CDOID id = getRevision().getID();
    getAccessor().ensureActiveSession();
    ObjyObject objyObject = getAccessor().getObject(id);
    ObjyObject objyRevision = objyObject.getRevisionByVersion(getRevision().getVersion(), getRevision().getBranch().getID(),
        getAccessor().getObjySession().getObjectManager());

    List<Chunk> chunks = getChunks();

    for (Chunk chunk : chunks)
    {
      int chunkStartIndex = chunk.getStartIndex();
      int chunkSize = chunk.size();

      // get the data from the feature.
      List<Object> objects = objyRevision.fetchList(getAccessor(), getFeature(), chunkStartIndex, chunkSize);
      // although we asked for a chunkSize we might get less.
      int i = 0;
      for (Object obj : objects)
      {
        chunk.add(i++, obj);
      }
    }
    return chunks;
  }
}
