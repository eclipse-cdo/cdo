/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.protocol;


import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.stream.ExtendedDataInputStream;
import org.eclipse.net4j.util.stream.ExtendedDataOutputStream;

import org.eclipse.emf.cdo.core.CDOProtocol;
import org.eclipse.emf.cdo.core.ImplementationError;
import org.eclipse.emf.cdo.core.OIDEncoder;
import org.eclipse.emf.cdo.core.WrappedIOException;
import org.eclipse.emf.cdo.core.protocol.ResourceChangeInfo;
import org.eclipse.emf.cdo.server.AttributeInfo;
import org.eclipse.emf.cdo.server.ClassInfo;
import org.eclipse.emf.cdo.server.ColumnConverter;
import org.eclipse.emf.cdo.server.Mapper;
import org.eclipse.emf.cdo.server.ResourceInfo;
import org.eclipse.emf.cdo.server.ServerCDOProtocol;
import org.eclipse.emf.cdo.server.impl.SQLConstants;
import org.eclipse.emf.cdo.server.internal.CDOServer;

import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import java.io.IOException;

import java.sql.Types;


/**
 * @author Eike Stepper
 */
public class CommitTransactionIndication extends IndicationWithResponse
{
  public static final int CAPACITY_tempIdtoPersistentIdMap = 499;

  private static final ContextTracer TRACER = new ContextTracer(CDOServer.DEBUG_PROTOCOL,
      CommitTransactionIndication.class);

  private Mapper mapper;

  private TransactionTemplate transactionTemplate;

  private Map<Long, Long> tempOIDs = new HashMap<Long, Long>(CAPACITY_tempIdtoPersistentIdMap);

  private List<Long> changedObjectIds = new ArrayList<Long>();

  private Map<Long, Integer> changedObjectOIDOCA = new HashMap<Long, Integer>();

  private List<Long> oidList = new ArrayList<Long>();

  private boolean optimisticControlException = false;

  private List<ResourceChangeInfo> newResources = new ArrayList<ResourceChangeInfo>();

  public CommitTransactionIndication(Mapper mapper, TransactionTemplate transactionTemplate)
  {
    this.mapper = mapper;
    this.transactionTemplate = transactionTemplate;
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocol.COMMIT_TRANSACTION;
  }

  @Override
  protected void indicating(final ExtendedDataInputStream in) throws IOException
  {
    try
    {
      transactionTemplate.execute(new TransactionCallbackWithoutResult()
      {
        public void doInTransactionWithoutResult(TransactionStatus status)
        {
          try
          {
            receiveObjectsToDetach(in);
            receiveObjectsToAttach(in);
            receiveObjectChanges(in);
            receiveNewResources(in);
            if (optimisticControlException)
            {
              status.setRollbackOnly();
            }
          }
          catch (IOException ex)
          {
            throw new WrappedIOException(ex);
          }
        }
      });
    }
    catch (WrappedIOException ex)
    {
      ex.reThrow();
    }
    catch (TransactionException ex)
    {
      CDOServer.LOG.error("Error while committing transaction to database", ex);
    }

    if (!optimisticControlException)
    {
      transmitInvalidations();
      transmitResourceChanges();
    }
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws IOException
  {
    if (optimisticControlException)
    {
      out.writeBoolean(false);
      return;
    }
    else
    {
      out.writeBoolean(true);
    }

    out.writeInt(oidList.size());
    for (Iterator<Long> iter = oidList.iterator(); iter.hasNext();)
    {
      Long id = iter.next();
      out.writeLong(id.longValue());
    }

    out.writeInt(changedObjectIds.size());
    for (Iterator<Long> iter = changedObjectIds.iterator(); iter.hasNext();)
    {
      Long id = iter.next();
      Integer oca = changedObjectOIDOCA.get(id);
      out.writeLong(id.longValue());
      out.writeInt(oca.intValue());
    }
  }

  private void receiveNewResources(ExtendedDataInputStream in) throws IOException
  {
    int rid;
    while ((rid = in.readInt()) != 0)
    {
      String path = in.readString();
      mapper.insertResource(rid, path);
      newResources.add(new ResourceChangeInfo(ResourceChangeInfo.ADDED, rid, path));
    }
  }

  private void receiveObjectsToDetach(ExtendedDataInputStream in) throws IOException
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("receiveObjectsToDetach()");
    }

    for (;;)
    {
      long oid = in.readLong();
      if (oid == CDOProtocol.NO_MORE_OBJECTS)
      {
        break;
      }

      mapper.removeObject(oid);
    }
  }

  private void receiveObjectsToAttach(ExtendedDataInputStream in) throws IOException
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("receiveObjectsToAttach()");
    }

    int count = in.readInt();
    for (int i = 0; i < count; i++)
    {
      long oid = in.readLong();
      if (oid < 0)
      {
        oid = registerTempOID(oid);
      }

      ClassInfo info = receiveClassInfo(in);
      mapper.insertObject(oid, info.getCID());
      boolean isContent = in.readBoolean();
      if (isContent)
      {
        mapper.insertContent(oid);
      }

      receiveObjectsToAttachAttributes(in, info, oid);
    }

    receiveObjectsToAttachReferences(in);
  }

  private void receiveObjectsToAttachReferences(ExtendedDataInputStream in) throws IOException
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("receiveObjectsToAttachReferences()");
    }

    int count = in.readInt();
    for (int i = 0; i < count; i++)
    {
      long oid = in.readLong();
      int feature = in.readInt();
      int ordinal = in.readInt();
      long target = in.readLong();
      boolean containment = in.readBoolean();
      if (oid < 0)
      {
        oid = resolveTempOID(oid);
      }

      if (target < 0)
      {
        target = resolveTempOID(target);
      }

      mapper.insertReference(oid, feature, ordinal, target, containment);
    }
  }

  /**
   * @param in 
   * @return
   * @throws IOException 
   */
  private ClassInfo receiveClassInfo(ExtendedDataInputStream in) throws IOException
  {
    int cid = in.readInt();
    ClassInfo classInfo = mapper.getPackageManager().getClassInfo(cid);
    if (classInfo == null)
    {
      throw new ImplementationError("Unknown cid " + cid);
    }

    return classInfo;
  }

  /**
   * @param tempOID
   * @return
   */
  private long registerTempOID(long tempOID)
  {
    OIDEncoder oidEncoder = mapper.getOidEncoder();
    int rid = oidEncoder.getRID(-tempOID);
    ResourceInfo resourceInfo = mapper.getResourceManager().getResourceInfo(rid, mapper);
    long oidFragment = resourceInfo.getNextOIDFragment();

    Long key = new Long(tempOID);
    long oid = oidEncoder.getOID(rid, oidFragment);
    Long val = new Long(oid);

    tempOIDs.put(key, val);
    oidList.add(val);
    if (TRACER.isEnabled())
    {
      TRACER.trace("Mapping oid " + oidEncoder.toString(key) + " --> " + oidEncoder.toString(val));
    }

    return oid;
  }

  /**
   * @param tempOID
   * @return
   */
  private long resolveTempOID(long tempOID)
  {
    Long sourceVal = tempOIDs.get(new Long(tempOID));
    if (sourceVal == null)
    {
      OIDEncoder oidEncoder = mapper.getOidEncoder();
      throw new ImplementationError("no mapping for temporary oid " + oidEncoder.toString(tempOID));
    }

    return sourceVal.longValue();
  }

  private void receiveObjectChanges(ExtendedDataInputStream in) throws IOException
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("receiveObjectChanges()");
    }

    for (;;)
    {
      long oid = in.readLong();
      if (oid == CDOProtocol.NO_MORE_OBJECT_CHANGES)
      {
        break;
      }

      int oca = in.readInt();
      int newOCA = lock(oid, oca);
      receiveReferenceChanges(in);
      receiveAttributeChanges(in, oid);
      rememberChangedObject(oid, newOCA);
    }
  }

  private void receiveReferenceChanges(ExtendedDataInputStream in) throws IOException
  {
    for (;;)
    {
      byte changeKind = in.readByte();
      if (changeKind == CDOProtocol.NO_MORE_REFERENCE_CHANGES)
      {
        break;
      }

      switch (changeKind)
      {
        case CDOProtocol.FEATURE_SET:
          receiveReferenceSet(in);
          break;
        case CDOProtocol.FEATURE_UNSET:
          receiveReferenceUnset(in);
          break;
        case CDOProtocol.LIST_ADD:
          receiveReferenceAdd(in);
          break;
        case CDOProtocol.LIST_REMOVE:
          receiveReferenceRemove(in);
          break;
        case CDOProtocol.LIST_MOVE:
          receiveReferenceMove(in);
          break;
        default:
          throw new ImplementationError("invalid changeKind: " + changeKind);
      }
    }
  }

  /**
   * @param in 
   * @param oid
   * @param feature
   * @throws IOException 
   */
  private void receiveReferenceSet(ExtendedDataInputStream in) throws IOException
  {
    // oid is not mapped for changes!
    long oid = in.readLong();
    int feature = in.readInt();
    long target = in.readLong();
    boolean containment = in.readBoolean();
    if (target < 0)
    {
      target = resolveTempOID(target);
    }

    if (TRACER.isEnabled())
    {
      OIDEncoder oidEncoder = mapper.getOidEncoder();
      TRACER.trace("received reference set: oid=" + oidEncoder.toString(oid) + ", feature="
          + feature + ", target=" + oidEncoder.toString(target) + ", containment=" + containment);
    }

    mapper.insertReference(oid, feature, 0, target, containment);
  }

  /**
   * @param in 
   * @throws IOException 
   * 
   */
  private void receiveReferenceUnset(ExtendedDataInputStream in) throws IOException
  {
    // oid is not mapped for changes!
    long oid = in.readLong();
    int feature = in.readInt();
    if (TRACER.isEnabled())
    {
      OIDEncoder oidEncoder = mapper.getOidEncoder();
      TRACER.trace("received reference unset: oid=" + oidEncoder.toString(oid) + ", feature="
          + feature);
    }

    mapper.removeReference(oid, feature, 0);
  }

  /**
   * @param in 
   * @throws IOException 
   * 
   */
  private void receiveReferenceAdd(ExtendedDataInputStream in) throws IOException
  {
    // oid is not mapped for changes!
    long oid = in.readLong();
    int feature = in.readInt();
    int ordinal = in.readInt() + 1;
    long target = in.readLong();
    boolean containment = in.readBoolean();
    if (target < 0)
    {
      target = resolveTempOID(target);
    }

    if (TRACER.isEnabled())
    {
      OIDEncoder oidEncoder = mapper.getOidEncoder();
      TRACER.trace("received reference add: oid=" + oidEncoder.toString(oid) + ", feature="
          + feature + ", ordinal=" + ordinal + ", target=" + oidEncoder.toString(target)
          + ", containment=" + containment);
    }

    if (ordinal == 0)
    {
      ordinal = mapper.getCollectionCount(oid, feature);
    }

    mapper.moveReferencesRelative(oid, feature, ordinal, Integer.MAX_VALUE, 1);
    mapper.insertReference(oid, feature, ordinal, target, containment);
  }

  /**
   * @param in 
   * @throws IOException 
   * 
   */
  private void receiveReferenceRemove(ExtendedDataInputStream in) throws IOException
  {
    // oid is not mapped for changes!
    long oid = in.readLong();
    int feature = in.readInt();
    int ordinal = in.readInt() + 1;
    if (TRACER.isEnabled())
    {
      OIDEncoder oidEncoder = mapper.getOidEncoder();
      TRACER.trace("receiveObjectChangesReferences(REMOVE, sourceId=" + oidEncoder.toString(oid)
          + ", featureId=" + feature + ", sourceOrdinal=" + ordinal + ")");
    }

    mapper.removeReference(oid, feature, ordinal);
    mapper.moveReferencesRelative(oid, feature, ordinal, Integer.MAX_VALUE, -1);
  }

  /**
   * @param in 
   * @throws IOException 
   * 
   */
  private void receiveReferenceMove(ExtendedDataInputStream in) throws IOException
  {
    // oid is not mapped for changes!
    long oid = in.readLong();
    int feature = in.readInt();
    int ordinal = in.readInt();
    int moveToIndex = in.readInt();
    if (TRACER.isEnabled())
    {
      OIDEncoder oidEncoder = mapper.getOidEncoder();
      TRACER.trace("received reference move: oid=" + oidEncoder.toString(oid) + ", feature="
          + feature + ", ordinal=" + ordinal + ", moveToIndex=" + moveToIndex);
    }

    ordinal++;
    moveToIndex++;
    mapper.moveReferenceAbsolute(oid, feature, -1, ordinal);
    if (moveToIndex > ordinal)
    {
      mapper.moveReferencesRelative(oid, feature, ordinal + 1, moveToIndex, -1);
    }
    else if (moveToIndex < ordinal)
    {
      mapper.moveReferencesRelative(oid, feature, moveToIndex, ordinal - 1, 1);
    }

    mapper.moveReferenceAbsolute(oid, feature, moveToIndex, -1);
  }

  /**
   * 
   * @param oid
   * @param oca
   */
  private int lock(long oid, int oca)
  {
    boolean ok = mapper.lock(oid, oca);
    if (!ok)
    {
      optimisticControlException = true;
      if (TRACER.isEnabled())
      {
        TRACER.trace("");
        TRACER.trace("============================");
        TRACER.trace("OPTIMISTIC CONTROL EXCEPTION");
        TRACER.trace("============================");
        TRACER.trace("");
      }

      return oca;
    }

    return oca + 1;
  }

  /**
   * @param oid
   */
  private void rememberChangedObject(long oid, int oca)
  {
    Long key = new Long(oid);
    changedObjectIds.add(key);
    changedObjectOIDOCA.put(key, new Integer(oca));
  }

  /**
   * @param in 
   * @param info
   * @param oid
   * @throws IOException 
   * @throws InterruptedException
   */
  private void receiveAttributeChanges(ExtendedDataInputStream in, long oid) throws IOException
  {
    ClassInfo classInfo = null;
    for (;;)
    {
      int cid = in.readInt();
      if (cid == CDOProtocol.NO_MORE_SEGMENTS)
      {
        break;
      }

      classInfo = mapper.getPackageManager().getClassInfo(cid);
      receiveAttributeChangeSegment(in, oid, classInfo);
    }
  }

  /**
   * @param in 
   * @throws IOException 
   * 
   */
  private void receiveAttributeChangeSegment(ExtendedDataInputStream in, long oid,
      ClassInfo classInfo) throws IOException
  {
    int count = in.readInt();
    Object[] args = new Object[count + 1]; // last element is the oid
    args[count] = oid;
    int[] types = new int[count + 1];
    types[count] = Types.BIGINT;

    StringBuffer sql = new StringBuffer("UPDATE ");
    sql.append(classInfo.getTableName());
    sql.append(" SET ");

    for (int i = 0; i < count; i++)
    {
      int feature = in.readInt();
      AttributeInfo attributeInfo = classInfo.getAttributeInfo(feature);
      ColumnConverter converter = mapper.getColumnConverter();

      args[i] = converter.fromChannel(in, attributeInfo.getDataType());
      types[i] = attributeInfo.getColumnType();

      if (i > 0)
      {
        sql.append(", ");
      }

      sql.append(attributeInfo.getColumnName());
      sql.append("=?");
    }

    sql.append(" WHERE ");
    sql.append(SQLConstants.USER_OID_COLUMN);
    sql.append("=?");
    mapper.sql(sql.toString(), args, types);
  }

  private void receiveObjectsToAttachAttributes(ExtendedDataInputStream in, ClassInfo classInfo,
      long oid) throws IOException
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("receiveObjectsToAttachAttributes()");
    }

    while (classInfo != null)
    {
      AttributeInfo[] attributeInfos = classInfo.getAttributeInfos();
      Object[] args = new Object[attributeInfos.length + 1]; // the first element is the oid
      args[0] = oid;

      int[] types = new int[attributeInfos.length + 1];
      types[0] = Types.BIGINT;

      StringBuffer sql = new StringBuffer("INSERT INTO ");
      sql.append(classInfo.getTableName());
      sql.append(" VALUES(?");
      for (int i = 0; i < attributeInfos.length; i++)
      {
        AttributeInfo attributeInfo = attributeInfos[i];
        if (TRACER.isEnabled())
        {
          TRACER.trace("Receiving attribute " + attributeInfo.getName());
        }

        ColumnConverter converter = mapper.getColumnConverter();
        args[i + 1] = converter.fromChannel(in, attributeInfo.getDataType());
        types[i + 1] = attributeInfo.getColumnType();
        sql.append(", ?");
      }

      sql.append(")");
      mapper.sql(sql.toString(), args, types);
      classInfo = classInfo.getParent();
    }
  }

  private void transmitInvalidations()
  {
    if (!changedObjectIds.isEmpty())
    {
      ServerCDOProtocol cdo = (ServerCDOProtocol) getProtocol();
      cdo.fireInvalidationNotification(cdo.getChannel(), changedObjectIds);
    }
  }

  private void transmitResourceChanges()
  {
    CDOServer.LOG.warn("NOT IMPLEMENTED");
    //XXX
    //    if (!newResources.isEmpty())
    //    {
    //      ServerCDOProtocol cdo = (ServerCDOProtocol) getProtocol();
    //      ServerCDOResProtocol cdores = cdo.getServerCDOResProtocol();
    //      cdores.fireResourcesChangedNotification(newResources);
    //    }
  }
}
