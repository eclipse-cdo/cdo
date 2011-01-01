/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ibrahim Sallam - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.objectivity.db;

import org.eclipse.emf.cdo.server.internal.objectivity.bundle.OM;
import org.eclipse.emf.cdo.server.internal.objectivity.clustering.ObjyPlacementManager;
import org.eclipse.emf.cdo.server.internal.objectivity.clustering.ObjyPlacementManagerImpl;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import com.objy.db.DatabaseNotFoundException;
import com.objy.db.DatabaseOpenException;
import com.objy.db.ObjyRuntimeException;
import com.objy.db.app.Connection;
import com.objy.db.app.Session;
import com.objy.db.app.oo;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class ObjyConnection
{

  public static final ObjyConnection INSTANCE = new ObjyConnection();

  protected Connection connection = null;

  protected boolean isConnected = false;

  protected String fdName = "";

  // protected ObjectivityStore store = null;

  private static final ContextTracer TRACER_DEBUG = new ContextTracer(OM.DEBUG, ObjyConnection.class);

  // private static final ContextTracer TRACER_INFO = new ContextTracer(OM.INFO, ObjyConnection.class);

  // TODO - session pools could be a configuration candidate.
  private static final String SESSION_POOL_NAME_READ = "ReadSP";

  private static final String SESSION_POOL_NAME_WRITE = "WriteSP";

  // private static final String PoolInfo = "PoolInfo";

  protected ConcurrentHashMap<String, ObjySession> readPool;

  protected ConcurrentHashMap<String, ObjySession> writePool;

  private ObjyPlacementManager defaultPlacementManager = null;

  private Object syncObject = new Object();

  private ReentrantLock lock = new ReentrantLock();

  private int sessionMinCacheSize = 600;

  private int sessionMaxCacheSize = 1000;

  public ObjyConnection()
  {
    readPool = new ConcurrentHashMap<String, ObjySession>(20);
    writePool = new ConcurrentHashMap<String, ObjySession>(20);
  }

  /***
   * Connect to a store and an FD. TODO - We might need to allow switching of FD in the future.
   * 
   * @param fdName
   */
  synchronized public void connect(String fdName)
  {
    /****
     * If
     */
    this.fdName = fdName;
    connect();
    // this.store = store;
  }

  private void connect()
  {

    if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG.trace(" SessionMinCacheSize: " + sessionMinCacheSize);
      TRACER_DEBUG.trace(" SessionMaxCacheSize: " + sessionMaxCacheSize);
    }

    if (!isConnected)
    {
      try
      {
        if (Connection.current() == null)
        {
          int options = oo.LogNone; // oo.LogAll;
          Connection.setLoggingOptions(options, true, // boolean logToFiles
              true, // boolean appendLogFiles,
              "c:\\data", // String logDirPath,
              "MainLog.txt"// String mainLogFileName
          );
          if (TRACER_DEBUG.isEnabled())
          {
            TRACER_DEBUG.trace(" creating new Connection");
          }
          connection = Connection.open(fdName, oo.openReadWrite);
          connection.useContextClassLoader(true);

        }
        else
        {
          connection.addToMainLog("ObjyConnection.connect()", "...reopen connection to the FD.");
          connection.setOpenMode(oo.openReadWrite);
          connection.reopen();
          connection.loadSchemaClasses(true);
        }
        isConnected = true;
      }
      catch (DatabaseOpenException e)
      {
        e.printStackTrace();
      }
      catch (DatabaseNotFoundException e)
      {
        e.printStackTrace();
      }
    }
  }

  public String getSessionPoolNameRead()
  {
    return SESSION_POOL_NAME_READ;
  }

  public String getSessionPoolNameWrite()
  {
    return SESSION_POOL_NAME_WRITE;
  }

  public ObjySession getWriteSessionFromPool(String sessionName)
  {
    synchronized (syncObject)
    {
      // return connection.getSessionFromPool(getSessionPoolNameWrite(), sessionName);
      ObjySession session = writePool.get(sessionName);
      if (session == null)
      {
        session = new ObjySession(sessionName, writePool, this);
        writePool.put(sessionName, session);
      }
      session.join();
      return session;
    }
  }

  public ObjySession getReadSessionFromPool(String sessionName)
  {
    synchronized (syncObject)
    {
      // return connection.getSessionFromPool(getSessionPoolNameRead(), sessionName);
      ObjySession session = readPool.get(sessionName);
      if (session == null)
      {
        session = new ObjySession(sessionName, writePool, this);
        readPool.put(sessionName, session);
      }
      session.join();
      return session;
    }
  }

  public void returnSessionToPool(ObjySession session)
  {
    synchronized (syncObject)
    {
      // TODO Auto-generated method stub
      session.leave();
    }
  }

  public void disconnect()
  {
    if (!isConnected)
    {
      return;
    }
    // synchronized(syncObject)
    {
      // it's important to do the lock() call, otherwise during the test-suite
      // run we can exit the test before cleaning up, and session might be
      // partly terminated.
      // We could change the code in cleanupSessionPool() to remove the session
      // from the pool before terminating it, but this could leave some sessions
      // in the connection (another issue here is the connection.reconnect()
      // doesn't work all the time).
      lock.lock();
      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace("ObjyConnection.disconnect() -- Start. " + toString());
      }

      // terminate the session and cleanup the Pool.
      // TRACER_DEBUG.trace("ObjyConnection.disconnect() -- cleanup readPool. ");
      cleanupSessionPool(readPool);
      // TRACER_DEBUG.trace("ObjyConnection.disconnect() -- cleanup writePool. ");
      cleanupSessionPool(writePool);

      // TRACER_DEBUG.trace("ObjyConnection.disconnect() -- cleanup any other sessions. ");
      // for testing we need to find out if there are any open sessions.

      @SuppressWarnings("unchecked")
      Vector<Session> sessions = connection.sessions();
      for (Session aSession : sessions)
      {
        if (TRACER_DEBUG.isEnabled())
        {
          TRACER_DEBUG.trace("Session: " + aSession + " - open state: " + aSession.isOpen());
        }
        // we need to make sure that any open session is aborted, otherwise we
        // can't reopen the fd.
        if (aSession.isOpen())
        {
          try
          {
            aSession.join();
            aSession.abort();
            // IS: sometime we get exception about no transaction, although we checked
            // aSession.isOpen() above.
          }
          catch (ObjyRuntimeException ex)
          {
            ex.printStackTrace();
          }
          finally
          {
            aSession.terminate();
          }
        }
      }

      // 100211:IS - Avoid closing the connection, we're seeing
      // sort of schema issues doing so with 9.4.1...
      /****
       * try { Session session = new Session(); session.begin(); //connection.dropAllUserClasses(true);
       * connection.dropAllUnregisterableClasses(); session.commit(); connection.close(); isConnected = false; } catch
       * (DatabaseClosedException e) { // TODO Auto-generated catch block e.printStackTrace(); }
       ****/
      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace("ObjyConnection.disconnect() -- END. ");
      }
      lock.unlock();
    }

  }

  // public void resetFD()
  // {
  // //fdManager.resetFD();
  // if (Connection.current() != null)
  // {
  // if (!isConnected)
  // connect();
  //
  // // for testing we need to find out if there are any open sessions.
  // Vector<Session> sessions = Connection.current().sessions();
  // System.out.println("Sessions still available: " + sessions.size());
  // for (Session aSession : sessions)
  // {
  // System.out.println("Session: " + aSession + " - open state: " + aSession.isOpen());
  // // we need to make sure that any open session is aborted, otherwise we
  // // can't reopen the fd.
  // if (aSession.isOpen())
  // {
  // try {
  // aSession.join();
  // aSession.abort();
  // // IS: sometime we get exception about no transaction, although we checked
  // // aSession.isOpen() above.
  // } catch (ObjyRuntimeException ex) {
  // ex.printStackTrace();
  // } finally {
  // aSession.terminate();
  // }
  // }
  // }
  //
  // // Session session = new Session();
  // // session.begin();
  // // Iterator itr = session.getFD().containedDBs();
  // // ooDBObj dbObj = null;
  // // List<ooDBObj> dbList = new ArrayList<ooDBObj>();
  // // while (itr.hasNext())
  // // {
  // // dbObj = (ooDBObj) itr.next();
  // // dbList.add(dbObj);
  // // }
  // // itr.close();
  // // session.commit();
  //
  // // session.begin();
  // // for (ooDBObj db : dbList)
  // // {
  // // System.out.println("restFD() - deleting DB(" + db.getOid().getStoreString()+"):" + db.getName());
  // // db.delete();
  // // }
  // // session.commit();
  //
  // // we need to wipe the schema, some tests have similar class and package
  // // names which could cause tests to fail.
  // // for now we'll just wipe the wole FD.
  // //fdManager.resetFD_OLD();
  //
  // //
  // // System.out.println("resetFD() - dumping catalog BEGIN.........");
  // // session.begin();
  // // session.getFD().dumpCatalog();
  // // session.commit();
  // // System.out.println("resetFD() - dumping catalog END...........");
  // // session.terminate();
  //
  // disconnect();
  // }
  // }

  public void registerClass(String name)
  {
    connection.registerClass(name);
  }

  public ObjyPlacementManager getDefaultPlacementManager()
  {
    if (defaultPlacementManager == null)
    {
      defaultPlacementManager = new ObjyPlacementManagerImpl();
    }
    return defaultPlacementManager;
  }

  protected void cleanupSessionPool(ConcurrentHashMap<String, ObjySession> pool)
  {
    for (ObjySession objySession : pool.values())
    {
      try
      {
        if (objySession.isOpen())
        {
          objySession.join();
          objySession.abort();
          // IS: sometime we get exception about no transaction, although we checked
          // aSession.isOpen() above.
        }
      }
      catch (ObjyRuntimeException ex)
      {
        ex.printStackTrace();
      }
      finally
      {
        // TRACER_DEBUG.trace("ObjyConnection.cleanupSessionPool() -- start terminating session. " +
        // objySession.toString());
        try
        {
          objySession.terminate();
          // TRACER_DEBUG.trace("ObjyConnection.cleanupSessionPool() --   end terminating session. " +
          // objySession.toString());
        }
        catch (ObjyRuntimeException ex)
        {
          ex.printStackTrace();
        }
      }
    }
    pool.clear();
  }

  public void setSessionMinCacheSize(int sessionMinCacheSize)
  {
    if (sessionMinCacheSize > this.sessionMinCacheSize)
    {
      this.sessionMinCacheSize = sessionMinCacheSize;
    }
  }

  public void setSessionMaxCacheSize(int sessionMaxCacheSize)
  {
    if (sessionMaxCacheSize > this.sessionMaxCacheSize)
    {
      this.sessionMaxCacheSize = sessionMaxCacheSize;
    }
  }

  public int getMinSessionCacheSize()
  {
    return sessionMinCacheSize;
  }

  public int getMaxSessionCacheSize()
  {
    return sessionMaxCacheSize;
  }
}
