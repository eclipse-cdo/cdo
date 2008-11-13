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
package org.eclipse.emf.cdo.server.internal.db.jdbc;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.IStoreChunkReader.Chunk;
import org.eclipse.emf.cdo.server.db.IClassMapping;
import org.eclipse.emf.cdo.server.db.IDBStoreChunkReader;
import org.eclipse.emf.cdo.server.db.IJDBCDelegate;
import org.eclipse.emf.cdo.server.db.IReferenceMapping;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;

import org.eclipse.net4j.db.IDBConnectionProvider;
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
 * @author Stefan Winkler
 * @since 2.0
 */
public class JDBCPerformanceMeasurementWrapper implements IJDBCDelegate
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, JDBCPerformanceMeasurementWrapper.class);

  private static Map<String, TimeData> timeData = Collections.synchronizedMap(new HashMap<String, TimeData>());

  private IJDBCDelegate delegate;

  public JDBCPerformanceMeasurementWrapper(IJDBCDelegate delegate)
  {
    this.delegate = delegate;
  }

  public void commit(OMMonitor monitor)
  {
    delegate.commit(monitor);
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

  public void initConnection(IDBConnectionProvider connectionProvider, boolean readOnly)
  {
    delegate.initConnection(connectionProvider, readOnly);
  }

  public void insertAttributes(CDORevision revision, IClassMapping classMapping)
  {
    long time = System.currentTimeMillis();
    delegate.insertAttributes(revision, classMapping);
    time = System.currentTimeMillis() - time;

    registerCall("insertAttributes", time);
  }

  public void insertReference(CDORevision sourceRevision, int index, CDOID targetId, IReferenceMapping referenceMapping)
  {
    long time = System.currentTimeMillis();
    delegate.insertReference(sourceRevision, index, targetId, referenceMapping);
    time = System.currentTimeMillis() - time;
    registerCall("insertReferenceDbId", time);
  }

  public void release()
  {
    delegate.release();
  }

  public void rollback()
  {
    delegate.rollback();
  }

  public void selectRevisionAttributes(CDORevision revision, IClassMapping classMapping, String where)
  {
    long time = System.currentTimeMillis();
    delegate.selectRevisionAttributes(revision, null, where);
    time = System.currentTimeMillis() - time;
    registerCall("selectAttributes", time);
  }

  public void selectRevisionReferenceChunks(IDBStoreChunkReader chunkReader, List<Chunk> chunks,
      IReferenceMapping referenceMapping, String where)
  {
    long time = System.currentTimeMillis();
    delegate.selectRevisionReferenceChunks(chunkReader, chunks, referenceMapping, where);
    time = System.currentTimeMillis() - time;
    registerCall("selectReferencesChunks", time);
  }

  public void selectRevisionReferences(CDORevision revision, IReferenceMapping referenceMapping, int referenceChunk)
  {
    long time = System.currentTimeMillis();
    delegate.selectRevisionReferences(revision, referenceMapping, referenceChunk);
    time = System.currentTimeMillis() - time;
    registerCall("selectReferences", time);
  }

  public final void updateRevised(CDORevision revision, IClassMapping classMapping)
  {
    long time = System.currentTimeMillis();
    delegate.updateRevised(revision, classMapping);
    time = System.currentTimeMillis() - time;
    registerCall("updateRevisedRevision", time);
  }

  public final void updateRevised(CDOID cdoid, long revised, IClassMapping classMapping)
  {
    long time = System.currentTimeMillis();
    delegate.updateRevised(cdoid, revised, classMapping);
    time = System.currentTimeMillis() - time;
    registerCall("updateRevisedID", time);
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
}

class TimeData
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
