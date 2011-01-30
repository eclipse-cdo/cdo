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

import org.eclipse.net4j.util.io.TMPUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import com.objy.db.app.Session;
import com.objy.db.app.ooContObj;
import com.objy.db.app.ooDBObj;

import org.w3c.dom.Element;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class FdManager
{

  private static final ContextTracer TRACER_DEBUG = new ContextTracer(OM.DEBUG, FdManager.class);

  private static final ContextTracer TRACER_INFO = new ContextTracer(OM.INFO, FdManager.class);

  private static final String DEFAULT_VALUE = "_DEFAULT_";

  // private static FdManager fdManagerSingleton = null;

  private String fdName = "test";

  private String fdFileHost = DEFAULT_VALUE;

  private String fdDirPath = null;

  private String lockserverHost = DEFAULT_VALUE;

  private String fdNumber = "12345";

  private String pageSize = DEFAULT_VALUE;

  private String fdFilePath = null;

  private String bootFilePath = null;

  private boolean initialized = false;

  public void initialize(boolean reset)
  {
    if (fdDirPath == null)
    {
      File dataFolder = TMPUtil.createTempFolder("Objy", "data");
      fdDirPath = dataFolder.getAbsolutePath();
    }
    if (fdFilePath == null)
    {
      fdFilePath = fdDirPath + File.separator + fdName + ".fdb";
    }
    if (bootFilePath == null)
    {
      bootFilePath = fdDirPath + File.separator + fdName + ".boot";
    }

    if (!initialized)
    {
      if (reset)
      {
        initialized = resetFD();
      }
      else if (!fdExists())
      {
        initialized = createFD();
      }
      else
      {
        // FD is ready, just use it.
        initialized = true;
      }
    }
  }

  public String getFd()
  {
    return bootFilePath;
  }

  public boolean resetFD()
  {
    return deleteDBs();
  }

  public boolean resetFD_OLD()
  {
    boolean bRet = true;
    // TBD: we need to add code to delete all DBs.
    // also we need to delete the schema.
    // It might be easier to just delete the FD, then create another one.
    if (fdExists())
    {
      bRet = deleteFD();
    }

    if (bRet)
    {
      bRet = createFD();
    }

    return bRet;
  }

  private boolean createFD()
  {
    boolean bRet = false;
    Process proc = null;

    String command = "oonewfd"
        // + " -fdfilehost " + getFdFileHost()
        + " -fdfilepath " + fdFilePath + " -lockserver " + getLockServerHost() + " -fdnumber " + fdNumber
        + " -pagesize " + getPageSize()
        // + " -jnldirpath " + jrnlDirPath
        // + " -licensefile " + licenseFilePath
        // + ((standAlone)?" -standalone ":" ")
        + " " + bootFilePath;

    TRACER_INFO.trace("Createing FD: '" + bootFilePath + "'.");

    try
    {
      proc = Runtime.getRuntime().exec(command);
      if (proc.waitFor() != 0)
      {
        dumpStream(proc.getErrorStream());
        throw new RuntimeException("Error creating FD...");
      }

      dumpStream(proc.getInputStream());
      bRet = true;
      // loadSchema();

    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    catch (InterruptedException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return bRet;
  }

  // TODO - we made it public to allow wiping out the whole FD, there is close
  // package names and classes in the test suite.
  public boolean deleteFD()
  {
    boolean bRet = false;
    Process proc = null;
    File file = new File(bootFilePath);
    if (!file.exists())
    {
      return true;
    }

    String command = "oodeletefd" + " -force " + bootFilePath;
    TRACER_INFO.trace("Deleting FD: '" + bootFilePath + "'.");

    try
    {
      proc = Runtime.getRuntime().exec(command);
      if (proc.waitFor() != 0)
      {
        dumpStream(proc.getErrorStream());
        throw new RuntimeException("Error deleting FD...");
      }

      dumpStream(proc.getInputStream());
      bRet = true;

    }
    catch (InterruptedException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return bRet;

  }

  private boolean deleteDBs()
  {
    boolean bRet = false;
    Process proc = null;

    String command = "oodeletedb" + " -all " + " -force " + bootFilePath;
    // command += " ; del *.DB";
    TRACER_INFO.trace("Deleting all DBs from : '" + bootFilePath + "'.");

    try
    {
      proc = Runtime.getRuntime().exec(command);
      if (proc.waitFor() != 0)
      {
        dumpStream(proc.getErrorStream());
        throw new RuntimeException("Error deleting DBs...");
      }

      dumpStream(proc.getInputStream());
      bRet = true;

    }
    catch (InterruptedException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return bRet;

  }

  @SuppressWarnings("unused")
  private boolean deleteDBs_cl()
  {
    boolean bRet = false;
    Process proc = null;

    String command = "oodeletedb" + " -all " + " -force " + bootFilePath;
    // command += " ; del *.DB";
    TRACER_INFO.trace("Deleting all DBs from : '" + bootFilePath + "'.");

    try
    {
      proc = Runtime.getRuntime().exec(command);
      if (proc.waitFor() != 0)
      {
        dumpStream(proc.getErrorStream());
        throw new RuntimeException("Error deleting DBs...");
      }

      dumpStream(proc.getInputStream());
      bRet = true;

    }
    catch (InterruptedException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return bRet;
  }

  private boolean fdExists()
  {
    boolean bRet = false;

    File file = new File(bootFilePath);
    bRet = file.exists();
    // Process proc = null;
    //
    // String command = "oochange" + " -notitle " + bootFilePath;
    // TRACER_DEBUG.trace("Checking if FD: '" + bootFilePath + "' exists.");
    //
    // try
    // {
    // proc = Runtime.getRuntime().exec(command);
    // if (proc.waitFor() != 0)
    // {
    // dumpStream(proc.getErrorStream());
    // }
    // else
    // {
    // dumpStream(proc.getInputStream());
    // bRet = true;
    // }
    // }
    // catch (IOException e)
    // {
    // e.printStackTrace();
    // }
    // catch (InterruptedException e)
    // {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }

    return bRet;
  }

  // This is a temp solution to avoid unloaded schema.
  // We'll explicitly load the schema after creating the FD
  private boolean loadSchema()
  {
    boolean bRet = false;
    Process proc = null;

    String command = "ooschemaupgrade" + " -infile config" + File.separator + "schema.txt " + bootFilePath;
    TRACER_DEBUG.trace("Loading schema to FD: '" + bootFilePath + "'.");

    try
    {
      proc = Runtime.getRuntime().exec(command);
      if (proc.waitFor() != 0)
      {
        dumpStream(proc.getErrorStream());
      }
      else
      {
        dumpStream(proc.getInputStream());
        bRet = true;
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    catch (InterruptedException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return bRet;
  }

  public String getFdFileHost()
  {
    if (fdFileHost.equals(DEFAULT_VALUE))
    {
      // get local host
      try
      {
        InetAddress address = InetAddress.getLocalHost();
        fdFileHost = address.getHostName();
      }
      catch (UnknownHostException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    }
    return fdFileHost;
  }

  public void setFdFileHost(String fdFileHost)
  {
    this.fdFileHost = fdFileHost;
  }

  public String getFdDirPath()
  {
    return fdDirPath;
  }

  public void setFdDirPath(String fdDirPath)
  {
    this.fdDirPath = fdDirPath;
  }

  public String getLockServerHost()
  {
    if (lockserverHost.equals(DEFAULT_VALUE))
    {
      // get local host
      try
      {
        InetAddress address = InetAddress.getLocalHost();
        lockserverHost = address.getHostName();
      }
      catch (UnknownHostException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    }
    return lockserverHost;
  }

  public void setLockServerHost(String lockServerHost)
  {
    lockserverHost = lockServerHost;
  }

  public String getFdNumber()
  {
    return fdNumber;
  }

  public void setFdNumber(String fdNumber)
  {
    this.fdNumber = fdNumber;
  }

  public String getPageSize()
  {
    if (pageSize.equals(DEFAULT_VALUE))
    {
      pageSize = "8192";
    }
    return pageSize;
  }

  public void setPageSize(String pageSize)
  {
    this.pageSize = pageSize;
  }

  public String getFdName()
  {
    return fdName;
  }

  public void setFdName(String fdName)
  {
    this.fdName = fdName;
  }

  private void dumpStream(InputStream inStream)
  {
    BufferedInputStream inBuffStream = new BufferedInputStream(inStream);
    try
    {
      byte[] buffer = new byte[1024];
      int bytesRead = 0;
      while ((bytesRead = inBuffStream.read(buffer)) != -1)
      {
        String chunk = new String(buffer, 0, bytesRead);
        TRACER_DEBUG.trace(chunk);
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  public void configure()
  {
    initialize(false);
  }

  /**
   * TODO
   * 
   * @param storeConfig
   */
  public void configure(Element storeConfig)
  {
    // TODO Auto-generated method stub

  }

  public void configure(String name)
  {
    fdDirPath = fdDirPath + File.separator + name;
    // insure that path exist.
    File dir = new File(fdDirPath);
    if (!dir.exists())
    {
      // create the directory.
      dir.mkdirs();
    }
    Integer number = Math.abs(new Random().nextInt() % 65000);
    fdNumber = number.toString();
    initialize(false);
  }

  /**
   * Data cleanup code, that's mostly used by the test applications. This code will not remove schema.
   */
  public void removeData()
  {
    // ObjyConnection.INSTANCE.disconnect();
    // fdManager.resetFD();
    Session session = new Session();
    session.begin();
    Iterator<?> itr = session.getFD().containedDBs();
    ooDBObj dbObj = null;
    List<ooDBObj> dbList = new ArrayList<ooDBObj>();
    List<ooContObj> contList = new ArrayList<ooContObj>();
    while (itr.hasNext())
    {
      dbObj = (ooDBObj)itr.next();
      dbList.add(dbObj);
      {
        Iterator<?> contItr = dbObj.contains();
        while (contItr.hasNext())
        {
          contList.add((ooContObj)contItr.next());
        }
      }
    }

    for (ooContObj cont : contList)
    {
      cont.delete();
    }

    // for (ooDBObj db : dbList)
    // {
    // System.out.println("restFD() - deleting DB(" + db.getOid().getStoreString() + "):" + db.getName());
    // db.delete();
    // }

    session.commit();
    session.terminate();
  }
}
