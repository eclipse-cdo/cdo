package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOSessionConfiguration;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IRepository.Props;
import org.eclipse.emf.cdo.server.hibernate.teneo.TeneoHibernateMappingProvider;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateStore;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.jvm.IJVMAcceptor;
import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.tests.AbstractOMTest;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.teneo.PersistenceOptions;

import reference.ReferenceFactory;
import reference.ReferencePackage;
import interface_.InterfacePackage;

import java.util.HashMap;
import java.util.Map;

public class CDOPackageRefTest extends AbstractOMTest
{
  private static final String DB_USER = "sa";

  private static final String DB_PASS = "";

  private static final String DB_NAME = "mydb";

  private static final String REPOSITORY_NAME = "repo1";

  private static final String RESOURCE_PATH = "/my/test/resource";

  private Resource resource;

  private CDOTransaction transaction;

  private IJVMAcceptor acceptor;

  private IStore store;

  private CDOSession session;

  private IConnector connector;

  private IRepository repository;

  @Override
  protected void doSetUp() throws Exception
  {
    try
    {
      IManagedContainer container = ContainerUtil.createContainer();
      Net4jUtil.prepareContainer(container);
      JVMUtil.prepareContainer(container);
      CDOUtil.prepareContainer(container, false);
      CDOServerUtil.prepareContainer(container);

      acceptor = JVMUtil.getAcceptor(container, "default");
      store = new HibernateStore(new TeneoHibernateMappingProvider());

      Map<String, String> props = new HashMap<String, String>();
      props.put(Props.PROP_OVERRIDE_UUID, "f8188187-65de-4c8a-8e75-e0ce5949837a");
      props.put(Props.PROP_SUPPORTING_AUDITS, "false");
      props.put(Props.PROP_SUPPORTING_REVISION_DELTAS, "false");
      props.put(Props.PROP_VERIFYING_REVISIONS, "false");
      props.put(Props.PROP_CURRENT_LRU_CAPACITY, "10000");
      props.put(Props.PROP_REVISED_LRU_CAPACITY, "10000");
      props.put(PersistenceOptions.ID_FEATURE_AS_PRIMARY_KEY, "false");
      props.put("hibernate.connection.autocommit", "true");
      props.put("hibernate.cache.provider_class", "org.hibernate.cache.HashtableCacheProvider");
      props.put("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
      props.put("hibernate.connection.url", "jdbc:hsqldb:mem:" + DB_NAME);
      props.put("hibernate.connection.username", DB_USER);
      props.put("hibernate.connection.password", DB_PASS);
      props.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
      props.put("hibernate.hbm2ddl.auto", "update");

      repository = CDOServerUtil.createRepository(REPOSITORY_NAME, store, props);
      CDOServerUtil.addRepository(container, repository);

      connector = JVMUtil.getConnector(container, "default");

      CDOSessionConfiguration configuration = CDOUtil.createSessionConfiguration();
      configuration.setConnector(connector);
      configuration.setRepositoryName(REPOSITORY_NAME);
      configuration.setLegacySupportEnabled(false);

      session = configuration.openSession();
      session.getPackageRegistry().putEPackage(InterfacePackage.eINSTANCE);
      session.getPackageRegistry().putEPackage(ReferencePackage.eINSTANCE);

      transaction = session.openTransaction();
      resource = transaction.getOrCreateResource(RESOURCE_PATH);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      throw e;
    }
  }

  @Override
  protected void doTearDown() throws Exception
  {
    session.close();
    LifecycleUtil.deactivate(repository);
    LifecycleUtil.deactivate(connector);
    LifecycleUtil.deactivate(acceptor);
  }

  public void testOnlyReference() throws Exception
  {
    try
    {
      resource.getContents().add(ReferenceFactory.eINSTANCE.createReference());
      transaction.commit();
    }
    catch (Exception e)
    {
      e.printStackTrace();
      throw e;
    }
  }
}
