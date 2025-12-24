/*
 * Copyright (c) 2009-2013, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *
 *  Initial Publication:
 *    Eclipse Magazin - http://www.eclipse-magazin.de
 */
package org.gastro.internal.server;

import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.net4j.CDONet4jServerUtil;

import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.h2.H2Adapter;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.OSGiActivator;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.trace.OMTracer;

import org.h2.jdbcx.JdbcDataSource;

import java.util.HashMap;
import java.util.Map;

/**
 * The <em>Operations & Maintenance</em> class of this bundle.
 *
 * @author Eike Stepper
 */
public abstract class OM
{
  public static final String BUNDLE_ID = "org.gastro.server"; //$NON-NLS-1$

  public static final OMBundle BUNDLE = OMPlatform.INSTANCE.bundle(BUNDLE_ID, OM.class);

  public static final OMTracer DEBUG = BUNDLE.tracer("debug"); //$NON-NLS-1$

  public static final OMLogger LOG = BUNDLE.logger();

  private static IAcceptor acceptor;

  public static IRepository repository;

  /**
   * @author Eike Stepper
   */
  public static final class Activator extends OSGiActivator
  {
    private String serverPort;

    public Activator()
    {
      super(BUNDLE);
    }

    @Override
    protected void doStart() throws Exception
    {
      serverPort = System.getProperty("org.gastro.server.port");
      if (serverPort == null)
      {
        return;
      }

      OM.LOG.info("Gastro server starting");
      JdbcDataSource dataSource = new JdbcDataSource();
      dataSource.setURL("jdbc:h2:database/gastro");

      IMappingStrategy mappingStrategy = CDODBUtil.createHorizontalMappingStrategy(true);
      IDBAdapter dbAdapter = new H2Adapter();
      IDBConnectionProvider dbConnectionProvider = dbAdapter.createConnectionProvider(dataSource);
      IStore store = CDODBUtil.createStore(mappingStrategy, dbAdapter, dbConnectionProvider);

      Map<String, String> props = new HashMap<>();
      props.put(IRepository.Props.OVERRIDE_UUID, "gastro");
      props.put(IRepository.Props.SUPPORTING_AUDITS, "true");
      props.put(IRepository.Props.SUPPORTING_BRANCHES, "false");

      repository = CDOServerUtil.createRepository("gastro", store, props);
      CDOServerUtil.addRepository(IPluginContainer.INSTANCE, repository);
      CDONet4jServerUtil.prepareContainer(IPluginContainer.INSTANCE);

      String description = "0.0.0.0:" + serverPort;
      acceptor = (IAcceptor)IPluginContainer.INSTANCE.getElement("org.eclipse.net4j.acceptors", "tcp", description);
      OM.LOG.info("Gastro server started");
    }

    @Override
    protected void doStop() throws Exception
    {
      if (serverPort == null)
      {
        return;
      }

      OM.LOG.info("Gastro server stopping");
      LifecycleUtil.deactivate(acceptor);
      LifecycleUtil.deactivate(repository);
      OM.LOG.info("Gastro server stopped");
    }
  }
}
