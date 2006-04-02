/*******************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Sympedia Methods and Tools.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *******************************************************************************/
package org.eclipse.emf.cdo.server.protocol;


import org.eclipse.net4j.core.Channel;
import org.eclipse.net4j.core.Protocol;
import org.eclipse.net4j.core.impl.AbstractIndicationWithResponse;
import org.eclipse.net4j.util.ImplementationError;

import org.eclipse.emf.cdo.core.CDOProtocol;
import org.eclipse.emf.cdo.core.OIDEncoder;
import org.eclipse.emf.cdo.core.protocol.ResourceChangeInfo;
import org.eclipse.emf.cdo.server.AttributeInfo;
import org.eclipse.emf.cdo.server.ClassInfo;
import org.eclipse.emf.cdo.server.ColumnConverter;
import org.eclipse.emf.cdo.server.Mapper;
import org.eclipse.emf.cdo.server.ResourceInfo;
import org.eclipse.emf.cdo.server.ServerCDOProtocol;
import org.eclipse.emf.cdo.server.ServerCDOResProtocol;
import org.eclipse.emf.cdo.server.impl.SQLConstants;

import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class CommitTransactionIndication extends AbstractIndicationWithResponse
{
  public static final int CAPACITY_tempIdtoPersistentIdMap = 499;

  private Map tempOIDs = new HashMap(CAPACITY_tempIdtoPersistentIdMap);

  private List changedObjectIds = new ArrayList();

  private Map changedObjectOidOca = new HashMap();

  private List oidList = new ArrayList();

  private boolean optimisticControlException = false;

  private Mapper mapper;

  private List<ResourceChangeInfo> newResources = new ArrayList();

  public short getSignalId()
  {
    return CDOProtocol.COMMIT_TRANSACTION;
  }

  public void indicate()
  {
    try
    {
      TransactionTemplate transactionTemplate = ((ServerCDOProtocol) getProtocol())
          .getTransactionTemplate();
      transactionTemplate.execute(new TransactionCallbackWithoutResult()
      {
        public void doInTransactionWithoutResult(TransactionStatus status)
        {
          receiveObjectsToDetach();
          receiveObjectsToAttach();
          receiveObjectChanges();

          receiveNewResources();
        }
      });
    }
    catch (TransactionException ex)
    {
      error("Error while committing transaction to database", ex);
    }

    transmitInvalidations();
    transmitRescourceChanges();
  }

  public void respond()
  {
    if (optimisticControlException)
    {
      transmitBoolean(false);
      return;
    }
    else
    {
      transmitBoolean(true);
    }

    transmitInt(oidList.size());

    for (Iterator iter = oidList.iterator(); iter.hasNext();)
    {
      Long id = (Long) iter.next();
      transmitLong(id.longValue());
    }

    transmitInt(changedObjectIds.size());

    for (Iterator iter = changedObjectIds.iterator(); iter.hasNext();)
    {
      Long id = (Long) iter.next();
      transmitLong(id.longValue());

      Integer oca = (Integer) changedObjectOidOca.get(id);
      transmitInt(oca.intValue());
    }
  }

  private void receiveNewResources()
  {
    int rid;
    while ((rid = receiveInt()) != 0)
    {
      String path = receiveString();
      getMapper().insertResource(rid, path);
      newResources.add(new ResourceChangeInfo(ResourceChangeInfo.ADDED, rid, path));
    }
  }

  private void receiveObjectsToDetach()
  {
    if (isDebugEnabled()) debug("receiveObjectsToDetach()");

    for (;;)
    {
      long oid = receiveLong();

      if (oid == 0)
      {
        break;
      }

      getMapper().removeObject(oid);
    }
  }

  private void receiveObjectsToAttach()
  {
    if (isDebugEnabled()) debug("receiveObjectsToAttach()");
    int count = receiveInt();

    for (int i = 0; i < count; i++)
    {
      long oid = receiveLong();

      if (oid < 0)
      {
        oid = registerTempOID(oid);
      }

      ClassInfo info = receiveClassInfo();
      getMapper().insertObject(oid, info.getCid());

      boolean isContent = receiveBoolean();
      if (isContent)
      {
        getMapper().insertContent(oid);
      }

      receiveObjectsToAttachAttributes(info, oid);
    }

    receiveObjectsToAttachReferences();
  }

  private void receiveObjectsToAttachReferences()
  {
    if (isDebugEnabled()) debug("receiveObjectsToAttachReferences()");
    int count = receiveInt();

    for (int i = 0; i < count; i++)
    {
      long oid = receiveLong();
      int feature = receiveInt();
      int ordinal = receiveInt();
      long target = receiveLong();
      boolean content = receiveBoolean();

      if (oid < 0)
      {
        oid = resolveTempOID(oid);
      }

      if (target < 0)
      {
        target = resolveTempOID(target);
      }

      getMapper().insertReference(oid, feature, ordinal, target, content);
    }
  }

  /**
   * @return
   */
  private ClassInfo receiveClassInfo()
  {
    int cid = receiveInt();
    ClassInfo classInfo = getMapper().getPackageManager().getClassInfo(cid);
    if (classInfo == null) throw new ImplementationError("Unknown cid " + cid);

    return classInfo;
  }

  /**
   * @param tempOID
   * @return
   */
  private long registerTempOID(long tempOID)
  {
    OIDEncoder oIDEncoder = getMapper().getOidEncoder();
    int rid = oIDEncoder.getRID(-tempOID);
    ResourceInfo resourceInfo = getMapper().getResourceManager().getResourceInfo(rid, getMapper());
    long oidFragment = resourceInfo.getNextOIDFragment();

    Long key = new Long(tempOID);
    long oid = oIDEncoder.getOID(rid, oidFragment);
    Long val = new Long(oid);

    tempOIDs.put(key, val);
    oidList.add(val);

    if (isDebugEnabled())
      debug("Mapping oid " + oIDEncoder.toString(key) + " --> " + oIDEncoder.toString(val));
    return oid;
  }

  /**
   * @param tempOID
   * @return
   */
  private long resolveTempOID(long tempOID)
  {
    Long sourceVal = (Long) tempOIDs.get(new Long(tempOID));

    if (sourceVal == null)
    {
      OIDEncoder oIDEncoder = getMapper().getOidEncoder();
      throw new ImplementationError("no mapping for temporary oid " + oIDEncoder.toString(tempOID));
    }

    return sourceVal.longValue();
  }

  private void receiveObjectChanges()
  {
    if (isDebugEnabled()) debug("receiveObjectChanges()");

    for (;;)
    {
      long oid = receiveLong();
      if (oid == CDOProtocol.NO_MORE_OBJECT_CHANGES)
      {
        break;
      }

      int oca = receiveInt();
      int newOCA = lock(oid, oca);

      receiveReferenceChanges();
      receiveAttributeChanges(oid);
      rememberChangedObject(oid, newOCA);
    }
  }

  private void receiveReferenceChanges()
  {
    for (;;)
    {
      byte changeKind = receiveByte();
      if (changeKind == CDOProtocol.NO_MORE_REFERENCE_CHANGES)
      {
        break;
      }

      switch (changeKind)
      {
        case CDOProtocol.FEATURE_SET:
          receiveReferenceSet();
          break;

        case CDOProtocol.FEATURE_UNSET:
          receiveReferenceUnset();
          break;

        case CDOProtocol.LIST_ADD:
          receiveReferenceAdd();
          break;

        case CDOProtocol.LIST_REMOVE:
          receiveReferenceRemove();
          break;

        case CDOProtocol.LIST_MOVE:
          receiveReferenceMove();
          break;

        default:
          throw new ImplementationError("invalid changeKind: " + changeKind);
      }
    }
  }

  /**
   * @param oid
   * @param feature
   */
  private void receiveReferenceSet()
  {
    // oid is not mapped for changes!
    long oid = receiveLong();
    int feature = receiveInt();
    long target = receiveLong();
    boolean content = receiveBoolean();

    if (target < 0)
    {
      target = resolveTempOID(target);
    }

    if (isDebugEnabled())
    {
      OIDEncoder oIDEncoder = getMapper().getOidEncoder();
      debug("received reference set: oid=" + oIDEncoder.toString(oid) + ", feature=" + feature
          + ", target=" + oIDEncoder.toString(target) + ", content=" + content);
    }

    getMapper().insertReference(oid, feature, 0, target, content);
  }

  /**
   * 
   */
  private void receiveReferenceUnset()
  {
    // oid is not mapped for changes!
    long oid = receiveLong();
    int feature = receiveInt();

    if (isDebugEnabled())
    {
      OIDEncoder oIDEncoder = getMapper().getOidEncoder();
      debug("received reference unset: oid=" + oIDEncoder.toString(oid) + ", feature=" + feature);
    }

    getMapper().removeReference(oid, feature, 0);
  }

  /**
   * 
   */
  private void receiveReferenceAdd()
  {
    // oid is not mapped for changes!
    long oid = receiveLong();
    int feature = receiveInt();
    int ordinal = receiveInt() + 1;
    long target = receiveLong();
    boolean content = receiveBoolean();

    if (target < 0)
    {
      target = resolveTempOID(target);
    }

    if (isDebugEnabled())
    {
      OIDEncoder oIDEncoder = getMapper().getOidEncoder();
      debug("received reference add: oid=" + oIDEncoder.toString(oid) + ", feature=" + feature
          + ", ordinal=" + ordinal + ", target=" + oIDEncoder.toString(target) + ", content="
          + content);
    }

    if (ordinal == 0)
    {
      ordinal = getMapper().getCollectionCount(oid, feature);
    }

    getMapper().moveReferencesRelative(oid, feature, ordinal, Integer.MAX_VALUE, 1);
    getMapper().insertReference(oid, feature, ordinal, target, content);
  }

  /**
   * 
   */
  private void receiveReferenceRemove()
  {
    // oid is not mapped for changes!
    long oid = receiveLong();
    int feature = receiveInt();
    int ordinal = receiveInt() + 1;

    if (isDebugEnabled())
    {
      OIDEncoder oIDEncoder = getMapper().getOidEncoder();
      debug("receiveObjectChangesReferences(REMOVE, sourceId=" + oIDEncoder.toString(oid)
          + ", featureId=" + feature + ", sourceOrdinal=" + ordinal + ")");
    }

    getMapper().removeReference(oid, feature, ordinal);
    getMapper().moveReferencesRelative(oid, feature, ordinal, Integer.MAX_VALUE, -1);
  }

  /**
   * 
   */
  private void receiveReferenceMove()
  {
    // oid is not mapped for changes!
    long oid = receiveLong();
    int feature = receiveInt();
    int ordinal = receiveInt();
    int moveToIndex = receiveInt();

    if (isDebugEnabled())
    {
      OIDEncoder oIDEncoder = getMapper().getOidEncoder();
      debug("received reference move: oid=" + oIDEncoder.toString(oid) + ", feature=" + feature
          + ", ordinal=" + ordinal + ", moveToIndex=" + moveToIndex);
    }

    ordinal++;
    moveToIndex++;

    getMapper().moveReferenceAbsolute(oid, feature, -1, ordinal);

    if (moveToIndex > ordinal)
    {
      getMapper().moveReferencesRelative(oid, feature, ordinal + 1, moveToIndex, -1);
    }
    else if (moveToIndex < ordinal)
    {
      getMapper().moveReferencesRelative(oid, feature, moveToIndex, ordinal - 1, 1);
    }

    getMapper().moveReferenceAbsolute(oid, feature, moveToIndex, -1);
  }

  /**
   * 
   * @param oid
   * @param oca
   */
  private int lock(long oid, int oca)
  {
    boolean ok = getMapper().lock(oid, oca);

    if (!ok)
    {
      optimisticControlException = true;

      if (isDebugEnabled())
      {
        debug("");
        debug("============================");
        debug("OPTIMISTIC CONTROL EXCEPTION");
        debug("============================");
        debug("");
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
    changedObjectOidOca.put(key, new Integer(oca));
  }

  /**
   * @param info
   * @param oid
   * @throws InterruptedException
   */
  private void receiveAttributeChanges(long oid)
  {
    ClassInfo classInfo = null;

    for (;;)
    {
      int cid = receiveInt();
      if (cid == CDOProtocol.NO_MORE_SEGMENTS)
      {
        break;
      }

      classInfo = getMapper().getPackageManager().getClassInfo(cid);
      receiveAttributeChangeSegment(oid, classInfo);
    }
  }

  /**
   * 
   */
  private void receiveAttributeChangeSegment(long oid, ClassInfo classInfo)
  {
    int count = receiveInt();
    Object[] args = new Object[count + 1]; // last element is the oid
    args[count] = new Long(oid);

    StringBuffer sql = new StringBuffer("UPDATE ");
    sql.append(classInfo.getTableName());
    sql.append(" SET ");

    for (int i = 0; i < count; i++)
    {
      int feature = receiveInt();
      AttributeInfo attributeInfo = classInfo.getAttributeInfo(feature);
      ColumnConverter converter = getMapper().getColumnConverter();
      args[i] = converter.fromChannel(getChannel(), attributeInfo.getDataType());

      if (i > 0) sql.append(", ");
      sql.append(attributeInfo.getColumnName());
      sql.append("=?");
    }

    sql.append(" WHERE ");
    sql.append(SQLConstants.OBJECT_OID_COLUMN);
    sql.append("=?");

    getMapper().sql(sql.toString(), args);

  }

  private void receiveObjectsToAttachAttributes(ClassInfo classInfo, long oid)
  {
    if (isDebugEnabled()) debug("receiveObjectsToAttachAttributes()");

    while (classInfo != null)
    {
      AttributeInfo[] attributeInfos = classInfo.getAttributeInfos();

      Object[] args = new Object[attributeInfos.length + 1]; // the first element is the oid
      args[0] = new Long(oid);

      StringBuffer sql = new StringBuffer("INSERT INTO ");
      sql.append(classInfo.getTableName());
      sql.append(" VALUES(?");

      for (int i = 0; i < attributeInfos.length; i++)
      {
        AttributeInfo attributeInfo = attributeInfos[i];
        if (isDebugEnabled()) debug("Receiving attribute " + attributeInfo.getName());

        ColumnConverter converter = getMapper().getColumnConverter();
        args[i + 1] = converter.fromChannel(getChannel(), attributeInfo.getDataType());

        sql.append(", ?");
      }

      sql.append(")");
      getMapper().sql(sql.toString(), args);

      classInfo = classInfo.getParent();
    }
  }

  private void transmitInvalidations()
  {
    if (!changedObjectIds.isEmpty())
    {
      Channel me = getChannel();
      int myType = me.getConnector().getType();
      Protocol cdo = me.getProtocol();

      Channel[] channels = cdo.getChannels();
      for (int i = 0; i < channels.length; i++)
      {
        Channel channel = channels[i];
        if (channel != me)
        {
          int type = channel.getConnector().getType();
          if (type == myType) // Important to exclude embedded peers (clients)
          {
            InvalidateObjectRequest signal = new InvalidateObjectRequest(changedObjectIds);

            try
            {
              channel.transmit(signal);
            }
            catch (Exception ex)
            {
              error("Error while requesting signal " + signal, ex);
            }
          }
        }
      }
    }
  }

  private void transmitRescourceChanges()
  {
    if (!newResources.isEmpty())
    {
      Channel me = getChannel();
      int myType = me.getConnector().getType();
      ServerCDOProtocol cdo = (ServerCDOProtocol) me.getProtocol();
      ServerCDOResProtocol cdores = cdo.getCdoResServerProtocol();

      Channel[] channels = cdores.getChannels();
      for (int i = 0; i < channels.length; i++)
      {
        Channel channel = channels[i];
        int type = channel.getConnector().getType();
        if (type == myType) // Important to exclude embedded peers (clients)
        {
          ResourcesChangedRequest signal = new ResourcesChangedRequest(newResources);

          try
          {
            channel.transmit(signal);
          }
          catch (Exception ex)
          {
            error("Error while requesting signal " + signal, ex);
          }
        }
      }
    }
  }

  private Mapper getMapper()
  {
    if (mapper == null)
    {
      mapper = ((ServerCDOProtocol) getProtocol()).getMapper();
    }

    return mapper;
  }
}
