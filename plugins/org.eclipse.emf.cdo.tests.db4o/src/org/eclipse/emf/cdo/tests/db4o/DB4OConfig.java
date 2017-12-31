/*
 * Copyright (c) 2011, 2012, 2015, 2017 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db4o;

import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.internal.db4o.DB4OStore;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;

import org.eclipse.net4j.util.io.TMPUtil;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;
import java.util.Set;

/**
 * @author Victor Roldan Betancort
 */
public class DB4OConfig extends RepositoryConfig
{
  public static final String CAPABILITY_MEM = "DB4O.mem";

  public static final String CAPABILITY_NET = "DB4O.net";

  private static final Random RANDOM = new Random(System.currentTimeMillis());

  private static final long serialVersionUID = 1L;

  private boolean mem;

  private transient boolean optimizing = true;

  public DB4OConfig(boolean mem)
  {
    super("DB4O-" + (mem ? "mem" : "net"));
    this.mem = mem;
  }

  @Override
  public void initCapabilities(Set<String> capabilities)
  {
    super.initCapabilities(capabilities);
    if (mem)
    {
      capabilities.add(CAPABILITY_MEM);
    }
    else
    {
      capabilities.add(CAPABILITY_NET);
    }
  }

  @Override
  protected String getStoreName()
  {
    return "DB4O";
  }

  public boolean isMem()
  {
    return mem;
  }

  public IStore createStore(String repoName)
  {
    if (mem)
    {
      throw new UnsupportedOperationException();
      // if (!isRestarting())
      // {
      // MEMDB4OStore.clearContainer();
      // }
      //
      // return new MEMDB4OStore();
    }

    File tempFolder = TMPUtil.getTempFolder();
    File file = new File(tempFolder, "cdodb_" + repoName + ".db4o");
    if (file.exists() && !isRestarting())
    {
      file.delete();
    }

    int port = 0;
    boolean ok = false;
    do
    {
      ServerSocket sock = null;

      try
      {
        port = 1024 + RANDOM.nextInt(65536 - 1024);
        sock = new ServerSocket(port);
        ok = true;
      }
      catch (IOException e)
      {
      }
      finally
      {
        try
        {
          if (sock != null)
          {
            sock.close();
          }
        }
        catch (IOException e)
        {
        }
      }
    } while (!ok);

    return new DB4OStore(file.getPath(), port);
  }

  @Override
  protected boolean isOptimizing()
  {
    // Do NOT replace this with a hardcoded value!
    return optimizing;
  }
}
