/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.db.jdbc;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.server.IStoreChunkReader.Chunk;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IDBStoreChunkReader;
import org.eclipse.emf.cdo.server.db.IJDBCDelegate;
import org.eclipse.emf.cdo.server.db.mapping.IAttributeMapping;
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.server.db.mapping.IReferenceMapping;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is only to be used in tests (there is a static data aggregation member!).
 * 
 * @author Stefan Winkler
 * @since 2.0
 */
public class JDBCPerformanceReporter extends Lifecycle implements IJDBCDelegate
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, JDBCPerformanceReporter.class);

  private static Map<String, TimeData> timeData = Collections.synchronizedMap(new HashMap<String, TimeData>());

  private IJDBCDelegate delegate;

  public JDBCPerformanceReporter()
  {
  }

  public IJDBCDelegate getDelegate()
  {
    return delegate;
  }

  public void setDelegate(IJDBCDelegate delegate)
  {
    checkInactive();
    this.delegate = delegate;
  }

  public Connection getConnection()
  {
    return delegate.getConnection();
  }

  public PreparedStatement getPreparedStatement(String sql)
  {
    return delegate.getPreparedStatement(sql);
  }

  public Statement getStatement()
  {
    return delegate.getStatement();
  }

  public void insertAttributes(InternalCDORevision revision, IClassMapping classMapping)
  {
    long time = System.currentTimeMillis();
    delegate.insertAttributes(revision, classMapping);
    time = System.currentTimeMillis() - time;

    registerCall("insertAttributes", time);
  }

  public void insertReference(CDOID id, int version, int index, CDOID targetId, IReferenceMapping referenceMapping)
  {
    long time = System.currentTimeMillis();
    delegate.insertReference(id, version, index, targetId, referenceMapping);
    time = System.currentTimeMillis() - time;
    registerCall("insertReferenceDbId", time);
  }

  public void flush(OMMonitor monitor)
  {
    long time = System.currentTimeMillis();
    delegate.flush(monitor);
    time = System.currentTimeMillis() - time;
    registerCall("write", time);
  }

  public void commit(OMMonitor monitor)
  {
    delegate.commit(monitor);
  }

  public void rollback()
  {
    delegate.rollback();
  }

  public boolean selectRevisionAttributes(InternalCDORevision revision, IClassMapping classMapping, String where)
  {
    long time = System.currentTimeMillis();
    boolean result = delegate.selectRevisionAttributes(revision, null, where);
    time = System.currentTimeMillis() - time;
    registerCall("selectAttributes", time);
    return result;
  }

  public void selectRevisionReferenceChunks(IDBStoreChunkReader chunkReader, List<Chunk> chunks,
      IReferenceMapping referenceMapping, String where)
  {
    long time = System.currentTimeMillis();
    delegate.selectRevisionReferenceChunks(chunkReader, chunks, referenceMapping, where);
    time = System.currentTimeMillis() - time;
    registerCall("selectReferencesChunks", time);
  }

  public void selectRevisionReferences(InternalCDORevision revision, IReferenceMapping referenceMapping,
      int referenceChunk)
  {
    long time = System.currentTimeMillis();
    delegate.selectRevisionReferences(revision, referenceMapping, referenceChunk);
    time = System.currentTimeMillis() - time;
    registerCall("selectReferences", time);
  }

  public final void updateRevisedForReplace(InternalCDORevision revision, IClassMapping classMapping)
  {
    long time = System.currentTimeMillis();
    delegate.updateRevisedForReplace(revision, classMapping);
    time = System.currentTimeMillis() - time;
    registerCall("updateRevisedForReplace", time);
  }

  public final void updateRevisedForDetach(CDOID cdoid, long revised, IClassMapping classMapping)
  {
    long time = System.currentTimeMillis();
    delegate.updateRevisedForDetach(cdoid, revised, classMapping);
    time = System.currentTimeMillis() - time;
    registerCall("updateRevisedForDetach", time);
  }

  public void deleteAttributes(CDOID id, IClassMapping classMapping)
  {
    long time = System.currentTimeMillis();
    delegate.deleteAttributes(id, classMapping);
    time = System.currentTimeMillis() - time;
    registerCall("deleteAttributes", time);
  }

  public void deleteReferences(CDOID id, IReferenceMapping referenceMapping)
  {
    long time = System.currentTimeMillis();
    delegate.deleteReferences(id, referenceMapping);
    time = System.currentTimeMillis() - time;
    registerCall("deleteReferences", time);
  }

  public void updateAttributes(InternalCDORevision revision, IClassMapping classMapping)
  {
    long time = System.currentTimeMillis();
    delegate.updateAttributes(revision, classMapping);
    time = System.currentTimeMillis() - time;
    registerCall("updateAllAttributes", time);
  }

  public void updateAttributes(CDOID id, int newVersion, long created,
      List<Pair<IAttributeMapping, Object>> attributeChanges, IClassMapping classMapping)
  {
    long time = System.currentTimeMillis();
    delegate.updateAttributes(id, newVersion, created, attributeChanges, classMapping);
    time = System.currentTimeMillis() - time;
    registerCall("updateAttributes", time);
  }

  public void updateReferenceVersion(CDOID id, int newVersion, IReferenceMapping referenceMapping)
  {
    long time = System.currentTimeMillis();
    delegate.updateReferenceVersion(id, newVersion, referenceMapping);
    time = System.currentTimeMillis() - time;
    registerCall("updateReferenceVersion", time);
  }

  public void insertReferenceRow(CDOID id, int newVersion, int index, CDOID value, IReferenceMapping referenceMapping)
  {
    long time = System.currentTimeMillis();
    delegate.insertReferenceRow(id, newVersion, index, value, referenceMapping);
    time = System.currentTimeMillis() - time;
    registerCall("insertReferenceRow", time);
  }

  public void updateReference(CDOID id, int newVersion, int index, CDOID value, IReferenceMapping referenceMapping)
  {
    long time = System.currentTimeMillis();
    delegate.updateReference(id, newVersion, index, value, referenceMapping);
    time = System.currentTimeMillis() - time;
    registerCall("updateReference", time);
  }

  public void moveReferenceRow(CDOID id, int newVersion, int oldPosition, int newPosition,
      IReferenceMapping referenceMapping)
  {
    long time = System.currentTimeMillis();
    delegate.moveReferenceRow(id, newVersion, oldPosition, newPosition, referenceMapping);
    time = System.currentTimeMillis() - time;
    registerCall("moveReferenceRow", time);
  }

  public void removeReferenceRow(CDOID id, int index, int newVersion, IReferenceMapping referenceMapping)
  {
    long time = System.currentTimeMillis();
    delegate.removeReferenceRow(id, index, newVersion, referenceMapping);
    time = System.currentTimeMillis() - time;
    registerCall("removeReferenceRow", time);
  }

  public void updateAttributes(CDOID id, int newVersion, long created, CDOID newContainerId,
      int newContainingFeatureId, CDOID newResourceId, List<Pair<IAttributeMapping, Object>> attributeChanges,
      IClassMapping classMapping)
  {
    long time = System.currentTimeMillis();
    delegate.updateAttributes(id, newVersion, created, newContainerId, newContainingFeatureId, newResourceId,
        attributeChanges, classMapping);
    time = System.currentTimeMillis() - time;
    registerCall("updateAttributes_with_containment", time);
  }

  public void setStoreAccessor(IDBStoreAccessor storeAccessor)
  {
    delegate.setStoreAccessor(storeAccessor);
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(delegate, "delegate");
  }

  @Override
  protected void doActivate() throws Exception
  {
    LifecycleUtil.activate(delegate);
    super.doActivate();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    report();
    super.doDeactivate();
    LifecycleUtil.deactivate(delegate);
  }

  public static void report()
  {
    for (TimeData td : timeData.values())
    {
      td.report(TRACER);
    }
  }

  private static void registerCall(String method, long time)
  {
    TimeData data = timeData.get(method);
    if (data == null)
    {
      data = new TimeData(method);
      timeData.put(method, data);
    }

    data.registerCall(time);
  }

  /**
   * @author Stefan Winkler
   * @since 2.0
   */
  private static final class TimeData
  {
    private String method;

    private long numberOfCalls = 0;

    private long timeMax = Integer.MIN_VALUE;

    private long timeMin = Integer.MAX_VALUE;

    private long timeTotal = 0;

    public TimeData(String method)
    {
      this.method = method;
    }

    public synchronized void registerCall(long time)
    {
      if (timeMin > time)
      {
        timeMin = time;
      }
      if (timeMax < time)
      {
        timeMax = time;
      }

      numberOfCalls++;
      timeTotal += time;
    }

    public void report(ContextTracer tracer)
    {
      tracer.format("{0}: {1} calls, {2} avg, {3} min, {4} max", method, numberOfCalls, timeTotal / numberOfCalls,
          timeMin, timeMax);
    }
  }
}
