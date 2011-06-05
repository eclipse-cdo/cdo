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

package org.eclipse.emf.cdo.server.internal.objectivity;

import org.eclipse.emf.cdo.server.internal.objectivity.db.FdManager;
import org.eclipse.emf.cdo.server.objectivity.IObjectivityStoreConfig;

import org.eclipse.net4j.util.lifecycle.Lifecycle;

import com.objy.db.app.Connection;
import com.objy.db.app.oo;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ObjectivityStoreConfig extends Lifecycle implements IObjectivityStoreConfig
{
  private static final long serialVersionUID = 1L;

  FdManager fdManager = new FdManager();

  private int sessionMinCacheSize = 0;

  private int sessionMaxCacheSize = 0;

  private int logOption = oo.LogNone;

  public ObjectivityStoreConfig()
  {
    // fdManager.deleteFD();
    fdManager.configure();
  }

  public ObjectivityStoreConfig(Element storeConfig)
  {
    // for now we'll just call the default configuration...
    getFdProperties(storeConfig);
    fdManager.configure();
  }

  public ObjectivityStoreConfig(String name)
  {
    // create an FD with that name.
    fdManager.configure(name);
  }

  @Override
  public void doActivate()
  {
    // System.out.println("ObjectivityStoreConfig.doActivate()");
    fdManager.deleteFD();
    fdManager.configure();
  }

  @Override
  public void doDeactivate()
  {
    fdManager.deleteFD();
  }

  public String getFdName()
  {
    return fdManager.getFd();
  }

  public void resetFD()
  {
    if (Connection.current() == null)
    {
      return;
    }

    fdManager.removeData();
  }

  private void getFdProperties(Element storeConfig)
  {
    NodeList fdConfigs = storeConfig.getElementsByTagName("fdConfig"); //$NON-NLS-1$
    if (fdConfigs.getLength() != 1)
    {
      throw new IllegalStateException("FD configuration is missing"); //$NON-NLS-1$
    }

    Element fdConfig = (Element)fdConfigs.item(0);
    String fdName = fdConfig.getAttribute("name"); //$NON-NLS-1$
    String lockServerHost = fdConfig.getAttribute("lockServerHost"); //$NON-NLS-1$
    String fdDirPath = fdConfig.getAttribute("fdDirPath"); //$NON-NLS-1$
    String dbDirPath = fdConfig.getAttribute("dbDirPath"); //$NON-NLS-1$
    String logDirPath = fdConfig.getAttribute("logDirPath");//$NON-NLS-1$
    String fdFileHost = fdConfig.getAttribute("fdFileHost"); //$NON-NLS-1$
    String fdNumber = fdConfig.getAttribute("fdNumber"); //$NON-NLS-1$
    String pageSize = fdConfig.getAttribute("pageSize"); //$NON-NLS-1$

    // Session Cache data.
    sessionMinCacheSize = getIntegerValue(fdConfig.getAttribute("SessionMinCacheSize"), 0); //$NON-NLS-1$
    sessionMaxCacheSize = getIntegerValue(fdConfig.getAttribute("SessionMaxCacheSize"), 0); //$NON-NLS-1$

    // Log options.
    String logOptionString = fdConfig.getAttribute("logOption");
    if (logOptionString.equalsIgnoreCase("LogAll"))
    {
      logOption = oo.LogAll;
    }
    else if (logOptionString.equalsIgnoreCase("LogSession"))
    {
      logOption = oo.LogSession;
    }

    fdManager.setFdName(fdName);
    fdManager.setFdDirPath(fdDirPath);
    fdManager.setlogDirPath(logDirPath);
    fdManager.setFdNumber(fdNumber);
    fdManager.setFdFileHost(fdFileHost);
    fdManager.setLockServerHost(lockServerHost);
    fdManager.setPageSize(pageSize);

  }

  private int getIntegerValue(String str, int defaultValue)
  {
    if (str.length() == 0) // once we move to JDK 6 we can use isEmpty().
    {
      return defaultValue;
    }

    return new Integer(str).intValue();
  }

  public int getSessionMinCacheSize()
  {
    return sessionMinCacheSize;
  }

  public int getSessionMaxCacheSize()
  {
    return sessionMaxCacheSize;
  }

  public String getLogPath()
  {
    return fdManager.getLogPath();
  }

  public int getLogOption()
  {
    return logOption;
  }
}
