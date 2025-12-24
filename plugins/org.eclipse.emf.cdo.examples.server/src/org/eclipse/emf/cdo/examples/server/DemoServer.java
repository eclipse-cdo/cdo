/*
 * Copyright (c) 2010-2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.server;

import org.eclipse.emf.cdo.examples.internal.server.OM;
import org.eclipse.emf.cdo.examples.server.DemoConfiguration.Mode;

import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.concurrent.Worker;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.EclipseLoggingBridge;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class DemoServer extends Lifecycle
{
  public static final String PROP_BROWSER_PORT = OM.BUNDLE_ID + ".browser.port"; //$NON-NLS-1$

  public static final int PORT = 3003;

  public static final int MAX_IDLE_MINUTES = 15;

  public static final long MAX_IDLE_MILLIS = MAX_IDLE_MINUTES * 60 * 1000;

  public static final DemoServer INSTANCE = new DemoServer();

  private IAcceptor acceptor;

  private Map<String, DemoConfiguration> configs = new HashMap<>();

  private Cleaner cleaner = new Cleaner();

  private DemoServer()
  {
  }

  public IAcceptor getAcceptor()
  {
    return acceptor;
  }

  public DemoConfiguration[] getConfigs()
  {
    synchronized (configs)
    {
      return configs.values().toArray(new DemoConfiguration[configs.size()]);
    }
  }

  public DemoConfiguration getConfig(String name)
  {
    synchronized (configs)
    {
      return configs.get(name);
    }
  }

  public DemoConfiguration addConfig(Mode mode)
  {
    DemoConfiguration config = new DemoConfiguration(mode, null);
    config.activate();

    synchronized (configs)
    {
      configs.put(config.getName(), config);
    }

    return config;
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    OMPlatform.INSTANCE.removeLogHandler(EclipseLoggingBridge.INSTANCE);
    OM.LOG.info("Demo server starting");

    IPluginContainer container = IPluginContainer.INSTANCE;
    acceptor = TCPUtil.getAcceptor(container, "0.0.0.0:" + PORT);

    String port = OMPlatform.INSTANCE.getProperty(PROP_BROWSER_PORT);
    if (port != null)
    {
      container.getElement("org.eclipse.emf.cdo.server.db.browsers", "default", port); //$NON-NLS-1$ //$NON-NLS-2$
    }

    cleaner.activate();
    OM.LOG.info("Demo server started");
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    OM.LOG.info("Demo server stopping");
    cleaner.deactivate();

    for (DemoConfiguration config : getConfigs())
    {
      config.deactivate();
    }

    configs.clear();

    if (acceptor != null)
    {
      LifecycleUtil.deactivate(acceptor);
      acceptor = null;
    }

    OM.LOG.info("Demo server stopped");
    super.doDeactivate();
  }

  /**
   * @author Eike Stepper
   */
  private final class Cleaner extends Worker
  {
    @Override
    protected String getThreadName()
    {
      return "DemoServerCleaner";
    }

    @Override
    protected void work(WorkContext context) throws Exception
    {
      for (DemoConfiguration config : getConfigs())
      {
        cleanIfNeeded(config);
      }

      context.nextWork(2000L);
    }

    protected void cleanIfNeeded(DemoConfiguration config)
    {
      if (config.getTimeoutMillis() == 0)
      {
        synchronized (configs)
        {
          configs.remove(config.getName());
        }

        config.deactivate();
      }
    }
  }
}
