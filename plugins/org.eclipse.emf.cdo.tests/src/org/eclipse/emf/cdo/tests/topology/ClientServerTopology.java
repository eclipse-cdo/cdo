/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests.topology;


import org.eclipse.emf.cdo.client.ResourceManager;
import org.eclipse.emf.cdo.client.impl.AttributeConverterImpl;
import org.eclipse.emf.cdo.client.impl.PackageManagerImpl;
import org.eclipse.emf.cdo.client.impl.ResourceManagerImpl;
import org.eclipse.emf.cdo.client.protocol.ClientCDOProtocolImpl;
import org.eclipse.emf.cdo.client.protocol.ClientCDOResProtocolImpl;
import org.eclipse.emf.cdo.core.impl.OIDEncoderImpl;
import org.eclipse.emf.cdo.server.impl.ColumnConverterImpl;
import org.eclipse.emf.cdo.server.impl.MapperImpl;
import org.eclipse.emf.cdo.server.protocol.ServerCDOProtocolImpl;
import org.eclipse.emf.cdo.server.protocol.ServerCDOResProtocolImpl;

import org.eclipse.emf.ecore.resource.ResourceSet;

import org.eclipse.net4j.transport.BufferProvider;
import org.eclipse.net4j.transport.Connector;
import org.eclipse.net4j.transport.ProtocolFactory;
import org.eclipse.net4j.transport.tcp.TCPAcceptor;
import org.eclipse.net4j.transport.tcp.TCPSelector;
import org.eclipse.net4j.util.Net4jUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.registry.HashMapRegistry;
import org.eclipse.net4j.util.registry.IRegistry;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sql.DataSource;


public class ClientServerTopology implements ITopology
{
  public static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();

  private AttributeConverterImpl attributeConverter;

  private OIDEncoderImpl oidEncoder;

  private PackageManagerImpl clientPackageManager;

  private IRegistry<String, ProtocolFactory> clientRegistry;

  private IRegistry<String, ProtocolFactory> serverRegistry;

  private BufferProvider bufferPool;

  private TCPSelector selector;

  private TCPAcceptor acceptor;

  private Connector connector;

  private org.eclipse.emf.cdo.server.impl.PackageManagerImpl serverPackageManager;

  private org.eclipse.emf.cdo.server.impl.ResourceManagerImpl serverResourceManager;

  private ColumnConverterImpl columnConverter;

  private DriverManagerDataSource dataSource;

  private DataSourceTransactionManager transactionManager;

  private TransactionTemplate transactionTemplate;

  private JdbcTemplate jdbcTemplate;

  public ClientServerTopology()
  {
  }

  public String getName()
  {
    return ITopologyConstants.CLIENT_SERVER_MODE;
  }

  public void start() throws Exception
  {
    bufferPool = Net4jUtil.createBufferPool();
    LifecycleUtil.activate(bufferPool);

    selector = Net4jUtil.createTCPSelector();
    LifecycleUtil.activate(selector);

    oidEncoder = new OIDEncoderImpl();
    oidEncoder.setFragmentBits(48);
    LifecycleUtil.activate(oidEncoder);

    // Server
    columnConverter = new ColumnConverterImpl();
    LifecycleUtil.activate(columnConverter);

    serverPackageManager = new org.eclipse.emf.cdo.server.impl.PackageManagerImpl();
    LifecycleUtil.activate(serverPackageManager);

    serverResourceManager = new org.eclipse.emf.cdo.server.impl.ResourceManagerImpl();
    LifecycleUtil.activate(serverResourceManager);

    dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
    dataSource.setUrl("jdbc:hsqldb:.");
    dataSource.setUsername("sa");

    jdbcTemplate = new JdbcTemplate();
    jdbcTemplate.setDataSource(dataSource);

    MapperImpl mapper = new MapperImpl();
    mapper.setColumnConverter(columnConverter);
    mapper.setDataSource(dataSource);
    mapper.setJdbcTemplate(jdbcTemplate);
    mapper.setOidEncoder(oidEncoder);
    mapper.setPackageManager(serverPackageManager);
    mapper.setResourceManager(serverResourceManager);
    mapper.setSqlDialectName("HSQLDB");
    LifecycleUtil.activate(mapper);

    transactionManager = new DataSourceTransactionManager();
    transactionManager.setDataSource(dataSource);

    transactionTemplate = new TransactionTemplate();

    serverRegistry = new HashMapRegistry();
    serverRegistry.register(new ServerCDOResProtocolImpl.Factory(mapper, transactionTemplate));
    serverRegistry.register(new ServerCDOProtocolImpl.Factory(mapper, transactionTemplate));

    acceptor = Net4jUtil.createTCPAcceptor(bufferPool, selector);
    acceptor.setProtocolFactoryRegistry(serverRegistry);
    acceptor.setReceiveExecutor(THREAD_POOL);
    LifecycleUtil.activate(acceptor);

    // Client
    attributeConverter = new AttributeConverterImpl();
    LifecycleUtil.activate(attributeConverter);

    clientPackageManager = new PackageManagerImpl();
    clientPackageManager.setAttributeConverter(attributeConverter);
    clientPackageManager.setOidEncoder(oidEncoder);
    clientPackageManager.setAutoPersistent(true);
    LifecycleUtil.activate(clientPackageManager);

    clientRegistry = new HashMapRegistry();
    clientRegistry.register(new ClientCDOProtocolImpl.Factory());
    clientRegistry.register(new ClientCDOResProtocolImpl.Factory());

    connector = Net4jUtil.createTCPConnector(bufferPool, selector, "localhost");
    connector.setProtocolFactoryRegistry(clientRegistry);
    connector.setReceiveExecutor(THREAD_POOL);
    LifecycleUtil.activate(connector);
  }

  public void stop() throws Exception
  {
    LifecycleUtil.deactivate(connector);
    connector = null;

    LifecycleUtil.deactivate(selector);
    selector = null;

    LifecycleUtil.deactivate(bufferPool);
    bufferPool = null;

    LifecycleUtil.deactivate(clientPackageManager);
    clientPackageManager = null;

    LifecycleUtil.deactivate(oidEncoder);
    oidEncoder = null;

    LifecycleUtil.deactivate(attributeConverter);
    attributeConverter = null;
  }

  public ResourceManager createResourceManager(ResourceSet resourceSet) throws Exception
  {
    ResourceManagerImpl resourceManager = new ResourceManagerImpl();
    resourceManager.setConnector(connector);
    resourceManager.setPackageManager(clientPackageManager);
    resourceManager.setResourceSet(resourceSet);
    LifecycleUtil.activate(resourceManager);
    return resourceManager;
  }

  public DataSource getDataSource()
  {
    return dataSource;
  }
}
