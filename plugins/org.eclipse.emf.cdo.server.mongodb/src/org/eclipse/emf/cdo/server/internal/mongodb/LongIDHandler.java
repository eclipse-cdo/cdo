/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.mongodb;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOID.ObjectType;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.mongodb.IMongoDBStore.IDHandler;
import org.eclipse.emf.cdo.spi.server.LongIDStore;

import org.eclipse.net4j.util.lifecycle.Lifecycle;

import java.util.Set;

/**
 * @author Eike Stepper
 */
public class LongIDHandler extends Lifecycle implements IDHandler
{
  public static final CDOID MIN = CDOID.NULL;

  public static final CDOID MAX = create(Long.MAX_VALUE);

  private MongoDBStore store;

  private CDOID lastObjectID = MIN;

  private CDOID nextLocalObjectID = MAX;

  public LongIDHandler(MongoDBStore store)
  {
    this.store = store;
  }

  public MongoDBStore getStore()
  {
    return store;
  }

  public Set<ObjectType> getObjectIDTypes()
  {
    return LongIDStore.OBJECT_ID_TYPES;
  }

  public CDOID getMinCDOID()
  {
    return MIN;
  }

  public CDOID getMaxCDOID()
  {
    return MAX;
  }

  public int compare(CDOID id1, CDOID id2)
  {
    return id1.compareTo(id2);
  }

  public CDOID createCDOID(String val)
  {
    Long id = Long.valueOf(val);
    return create(id);
  }

  public synchronized CDOID getLastObjectID()
  {
    return lastObjectID;
  }

  public synchronized void setLastObjectID(CDOID lastObjectID)
  {
    this.lastObjectID = lastObjectID;
  }

  public synchronized CDOID getNextLocalObjectID()
  {
    return nextLocalObjectID;
  }

  public synchronized void setNextLocalObjectID(CDOID nextLocalObjectID)
  {
    this.nextLocalObjectID = nextLocalObjectID;
  }

  public synchronized CDOID getNextCDOID(CDORevision revision)
  {
    if (revision.getBranch().isLocal())
    {
      CDOID result = nextLocalObjectID;
      nextLocalObjectID = create(value(result) - 1);
      return result;
    }

    lastObjectID = create(value(lastObjectID) + 1);
    return lastObjectID;
  }

  public boolean isLocalCDOID(CDOID id)
  {
    return compare(id, nextLocalObjectID) > 0;
  }

  private static CDOID create(long id)
  {
    return CDOIDUtil.createLong(id);
  }

  private static long value(CDOID id)
  {
    return CDOIDUtil.getLong(id);
  }
}
