/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.server;

import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.concurrent.Worker;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.OSGiApplication;
import org.eclipse.net4j.util.om.log.EclipseLoggingBridge;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class DemoServer extends OSGiApplication
{
  public static final String ID = OM.BUNDLE_ID + ".app"; //$NON-NLS-1$

  public static final String PROP_BROWSER_PORT = OM.BUNDLE_ID + ".browser.port"; //$NON-NLS-1$

  public static final int PORT = 3003;

  public static final long MAX_IDLE_TIME = 2 * 60 * 1000;

  public static DemoServer INSTANCE;

  private IAcceptor acceptor;

  private Map<String, DemoConfiguration> configs = new HashMap<String, DemoConfiguration>();

  private Cleaner cleaner = new Cleaner();

  public DemoServer()
  {
    super(ID);
    INSTANCE = this;
  }

  public IAcceptor getAcceptor()
  {
    return acceptor;
  }

  public DemoConfiguration getConfig(String name)
  {
    synchronized (configs)
    {
      return configs.get(name);
    }
  }

  public void addConfig(DemoConfiguration config)
  {
    synchronized (configs)
    {
      configs.put(config.getName(), config);
    }
  }

  @Override
  protected void doStart() throws Exception
  {
    super.doStart();
    OMPlatform.INSTANCE.removeLogHandler(EclipseLoggingBridge.INSTANCE);
    OM.LOG.info("Demo server starting");

    IPluginContainer container = IPluginContainer.INSTANCE;
    acceptor = TCPUtil.getAcceptor(container, "0.0.0.0:" + PORT);

    String port = System.getProperty(PROP_BROWSER_PORT);
    if (port != null)
    {
      container.getElement("org.eclipse.emf.cdo.server.db.browsers", "default", port); //$NON-NLS-1$ //$NON-NLS-2$
    }

    cleaner.activate();
    OM.LOG.info("Demo server started");
  }

  @Override
  protected void doStop() throws Exception
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
    super.doStop();
  }

  protected DemoConfiguration[] getConfigs()
  {
    synchronized (configs)
    {
      return configs.values().toArray(new DemoConfiguration[configs.size()]);
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class Cleaner extends Worker
  {
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
      if (config.getIdleTime() > MAX_IDLE_TIME)
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
