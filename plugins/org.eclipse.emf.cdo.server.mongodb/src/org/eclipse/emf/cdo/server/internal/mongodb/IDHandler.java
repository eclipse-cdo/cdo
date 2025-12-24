/*
 * Copyright (c) 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.mongodb;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOID.ObjectType;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.mongodb.IMongoDBStore;
import org.eclipse.emf.cdo.spi.server.LongIDStore;

import org.eclipse.net4j.util.lifecycle.Lifecycle;

import com.mongodb.DBObject;

import java.util.Comparator;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public interface IDHandler extends Comparator<CDOID>
{
  public IMongoDBStore getStore();

  public Set<ObjectType> getObjectIDTypes();

  public CDOID getMinCDOID();

  public CDOID getMaxCDOID();

  public boolean isLocalCDOID(CDOID id);

  public CDOID getNextCDOID(CDORevision revision);

  public CDOID createCDOID(String val);

  public CDOID getNextLocalObjectID();

  public void setNextLocalObjectID(CDOID nextLocalObjectID);

  public CDOID getLastObjectID();

  public void setLastObjectID(CDOID lastObjectID);

  public void write(DBObject doc, String key, CDOID id);

  public CDOID read(DBObject doc, String key);

  public Object toValue(CDOID id);

  public CDOID fromValue(Object value);

  /**
   * @author Eike Stepper
   */
  public class LongValue extends Lifecycle implements IDHandler
  {
    public static final CDOID MIN = CDOID.NULL;

    public static final CDOID MAX = create(Long.MAX_VALUE);

    private MongoDBStore store;

    private CDOID lastObjectID = MIN;

    private CDOID nextLocalObjectID = MAX;

    public LongValue(MongoDBStore store)
    {
      this.store = store;
    }

    @Override
    public MongoDBStore getStore()
    {
      return store;
    }

    @Override
    public Set<ObjectType> getObjectIDTypes()
    {
      return LongIDStore.OBJECT_ID_TYPES;
    }

    @Override
    public CDOID getMinCDOID()
    {
      return MIN;
    }

    @Override
    public CDOID getMaxCDOID()
    {
      return MAX;
    }

    @Override
    public int compare(CDOID id1, CDOID id2)
    {
      return id1.compareTo(id2);
    }

    @Override
    public CDOID createCDOID(String val)
    {
      Long id = Long.valueOf(val);
      return create(id);
    }

    @Override
    public synchronized CDOID getLastObjectID()
    {
      return lastObjectID;
    }

    @Override
    public synchronized void setLastObjectID(CDOID lastObjectID)
    {
      this.lastObjectID = lastObjectID;
    }

    @Override
    public synchronized CDOID getNextLocalObjectID()
    {
      return nextLocalObjectID;
    }

    @Override
    public synchronized void setNextLocalObjectID(CDOID nextLocalObjectID)
    {
      this.nextLocalObjectID = nextLocalObjectID;
    }

    @Override
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

    @Override
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

    @Override
    public void write(DBObject doc, String key, CDOID id)
    {
      long value = toValue(id);
      doc.put(key, value);
    }

    @Override
    public CDOID read(DBObject doc, String key)
    {
      long value = (Long)doc.get(key);
      return fromValue(value);
    }

    @Override
    public Long toValue(CDOID id)
    {
      return CDOIDUtil.getLong(id);
    }

    @Override
    public CDOID fromValue(Object value)
    {
      return CDOIDUtil.createLong((Long)value);
    }
  }
}
