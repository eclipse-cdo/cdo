/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.plain;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.server.bundle.CDOStandaloneServerApplication;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.spi.server.IAppExtension;
import org.eclipse.emf.cdo.spi.server.IAppExtension5;
import org.eclipse.emf.cdo.spi.server.RepositoryConfigurator;
import org.eclipse.emf.cdo.tests.config.impl.PlainTest;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.tcp.ITCPAcceptor;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.XMLConfiguration;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EcoreFactory;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.spi.net4j.AcceptorFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * Exercises the standard server configuration lifecycle from XML through a real TCP client commit.
 *
 * @author Eike Stepper
 */
public class CDOServerXMLConfigurationIntegrationTest extends PlainTest
{
  public void testStandardLifecycleLoadsAndUsesPreparedConfigurationOnce() throws Exception
  {
    File directory = Files.createTempDirectory("cdo-server-xml-integration").toFile(); //$NON-NLS-1$
    File configFile = new File(directory, "server.xml"); //$NON-NLS-1$
    File databaseFile = new File(directory, "database"); //$NON-NLS-1$
    String xml = "<?xml version=\"1.0\"?>\n" + //$NON-NLS-1$
        "<cdoServer xmlns:if=\"urn:org.eclipse.net4j:xml:if\">\n" + //$NON-NLS-1$
        "  <acceptor type=\"tcp\" listenAddr=\"127.0.0.1\" port=\"0\" if:true=\"$enableTCP\"/>\n" + //$NON-NLS-1$
        "  <repository name=\"$firstName\">\n" + //$NON-NLS-1$
        "    <initialPackage nsURI=\"http://www.eclipse.org/emf/2002/Ecore\"/>\n" + //$NON-NLS-1$
        "    <store type=\"db\">\n" + //$NON-NLS-1$
        "      <mappingStrategy type=\"horizontal\"/>\n" + //$NON-NLS-1$
        "      <dbAdapter name=\"h2\" if:true=\"$useH2\"/>\n" + //$NON-NLS-1$
        "      <dataSource class=\"org.h2.jdbcx.JdbcDataSource\" URL=\"jdbc:h2:$databaseFile\" if:true=\"$useH2\"/>\n" + //$NON-NLS-1$
        "      <dbAdapter name=\"postgresql\" if:true=\"$usePostgres\"/>\n" + //$NON-NLS-1$
        "      <dataSource class=\"org.postgresql.ds.PGSimpleDataSource\" serverName=\"localhost\" databaseName=\"unused\" if:true=\"$usePostgres\"/>\n" + //$NON-NLS-1$
        "    </store>\n" + //$NON-NLS-1$
        "  </repository>\n" + //$NON-NLS-1$
        "</cdoServer>\n"; //$NON-NLS-1$
    Files.writeString(configFile.toPath(), xml);

    IManagedContainer serverContainer = ContainerUtil.createInitializedContainer();
    IManagedContainer clientContainer = ContainerUtil.createInitializedContainer();
    IDBAdapter previousH2Adapter = IDBAdapter.Registry.INSTANCE.getAdapter("h2"); //$NON-NLS-1$
    assertNotNull("The test runtime must provide the H2 adapter bundle", previousH2Adapter); //$NON-NLS-1$

    String expandedDatabaseFile = databaseFile.getAbsolutePath() + "-$databaseFile"; //$NON-NLS-1$
    TestApplication application = new TestApplication(configFile, serverContainer, expandedDatabaseFile);
    CDONet4jSession session = null;

    try
    {
      application.activate();
      XMLConfiguration configuration = application.configuration;
      assertNotNull(configuration);
      assertTrue(configuration.isPrepared());
      assertEquals(1, application.repositoryConfigurationUses);
      assertEquals(1, application.transportConfigurationUses);

      Document preparedDocument = configuration.getDocument();
      NodeList adapters = preparedDocument.getElementsByTagName("dbAdapter"); //$NON-NLS-1$
      NodeList sources = preparedDocument.getElementsByTagName("dataSource"); //$NON-NLS-1$
      assertEquals(1, adapters.getLength());
      assertEquals("h2", ((Element)adapters.item(0)).getAttribute("name")); //$NON-NLS-1$ //$NON-NLS-2$
      assertEquals("org.h2.jdbcx.JdbcDataSource", ((Element)sources.item(0)).getAttribute("class")); //$NON-NLS-1$ //$NON-NLS-2$
      assertEquals("jdbc:h2:" + expandedDatabaseFile, ((Element)sources.item(0)).getAttribute("URL")); //$NON-NLS-1$ //$NON-NLS-2$
      assertTrue(XMLConfiguration.isExpanded(adapters.item(0).getAttributes().getNamedItem("name"))); //$NON-NLS-1$

      IRepository repository = CDOServerUtil.getRepository(serverContainer, "repo-$firstName"); //$NON-NLS-1$
      assertNotNull("The non-idempotent repository name was expanded exactly once", repository); //$NON-NLS-1$
      assertEquals("h2", ((IDBStore)repository.getStore()).getDBAdapter().getName()); //$NON-NLS-1$

      ITCPAcceptor tcpAcceptor = null;
      for (Object acceptor : serverContainer.getElements(AcceptorFactory.PRODUCT_GROUP))
      {
        if (acceptor instanceof ITCPAcceptor)
        {
          assertNull("Only one TCP acceptor is configured", tcpAcceptor); //$NON-NLS-1$
          tcpAcceptor = (ITCPAcceptor)acceptor;
        }
      }

      assertNotNull("The conditional TCP acceptor was started", tcpAcceptor); //$NON-NLS-1$
      int port = tcpAcceptor.getBoundPort();
      assertTrue("The TCP acceptor reports its dynamically assigned port", port > 0); //$NON-NLS-1$
      IConnector connector = TCPUtil.getConnector(clientContainer, "127.0.0.1:" + port); //$NON-NLS-1$
      CDONet4jSessionConfiguration sessionConfiguration = CDONet4jUtil.createNet4jSessionConfiguration();
      sessionConfiguration.setConnector(connector);
      sessionConfiguration.setRepositoryName("repo-$firstName"); //$NON-NLS-1$
      session = sessionConfiguration.openNet4jSession();

      CDOTransaction writeTransaction = session.openTransaction();
      String path = getResourcePath("/xml-configuration-integration"); //$NON-NLS-1$
      CDOResource resource = writeTransaction.createResource(path);
      EClass eClass = EcoreFactory.eINSTANCE.createEClass();
      eClass.setName("OnceExpanded"); //$NON-NLS-1$
      resource.getContents().add(eClass);
      writeTransaction.commit();
      writeTransaction.close();
      assertTrue("The H2 database was created at the once-expanded path", new File(expandedDatabaseFile + ".mv.db").isFile()); //$NON-NLS-1$

      CDOTransaction readTransaction = session.openTransaction();
      CDOResource readResource = readTransaction.getResource(path);
      assertEquals(1, readResource.getContents().size());
      assertEquals("OnceExpanded", ((EClass)CDOUtil.getEObject(readResource.getContents().get(0))).getName()); //$NON-NLS-1$
      readTransaction.close();
    }
    finally
    {
      LifecycleUtil.deactivate(session);
      LifecycleUtil.deactivate(application);
      LifecycleUtil.deactivate(clientContainer);
      LifecycleUtil.deactivate(serverContainer);
      deleteRecursively(directory);
    }
  }

  private static void deleteRecursively(File file)
  {
    File[] children = file.listFiles();
    if (children != null)
    {
      for (File child : children)
      {
        deleteRecursively(child);
      }
    }

    file.delete();
  }

  /**
   * Captures shared-configuration consumers while retaining the standard application implementation.
   *
   * @author Eike Stepper
   */
  private static final class TestApplication extends CDOStandaloneServerApplication
  {
    private final File configFile;

    private final IManagedContainer container;

    private final String databaseFile;

    private XMLConfiguration configuration;

    private int repositoryConfigurationUses;

    private int transportConfigurationUses;

    private TestApplication(File configFile, IManagedContainer container, String databaseFile)
    {
      this.configFile = configFile;
      this.container = container;
      this.databaseFile = databaseFile;
    }

    @Override
    protected File getConfigFile()
    {
      return configFile;
    }

    @Override
    protected IManagedContainer getApplicationContainer()
    {
      return container;
    }

    @Override
    protected RepositoryConfigurator getConfigurator(IManagedContainer container)
    {
      RepositoryConfigurator configurator = super.getConfigurator(container);
      configurator.setParameter("$enableTCP", "true"); //$NON-NLS-1$ //$NON-NLS-2$
      configurator.setParameter("$useH2", "true"); //$NON-NLS-1$ //$NON-NLS-2$
      configurator.setParameter("$usePostgres", "false"); //$NON-NLS-1$ //$NON-NLS-2$
      configurator.setParameter("$databaseFile", databaseFile); //$NON-NLS-1$
      configurator.setParameter("$firstName", "repo-$firstName"); //$NON-NLS-1$
      return configurator;
    }

    @Override
    protected void startRepositories(XMLConfiguration configuration, RepositoryConfigurator configurator, File configFile)
        throws ParserConfigurationException, SAXException, IOException, CoreException
    {
      this.configuration = configuration;
      ++repositoryConfigurationUses;
      super.startRepositories(configuration, configurator, configFile);
    }

    @Override
    protected void startExtensions(XMLConfiguration configuration, List<IAppExtension> extensions)
    {
      if (this.configuration == null)
      {
        this.configuration = configuration;
      }
      else if (this.configuration != configuration)
      {
        throw new IllegalStateException("Application extensions received a different XML configuration"); //$NON-NLS-1$
      }

      for (IAppExtension extension : extensions)
      {
        if (extension instanceof IAppExtension5 && "Net4j".equals(((IAppExtension5)extension).getName())) //$NON-NLS-1$
        {
          ++transportConfigurationUses;
        }
      }

      super.startExtensions(configuration, extensions);
    }
  }
}
