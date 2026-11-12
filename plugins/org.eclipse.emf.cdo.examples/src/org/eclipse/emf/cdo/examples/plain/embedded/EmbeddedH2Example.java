/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.plain.embedded;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.examples.company.Category;
import org.eclipse.emf.cdo.examples.company.Company;
import org.eclipse.emf.cdo.examples.company.CompanyFactory;
import org.eclipse.emf.cdo.examples.company.CompanyPackage;
import org.eclipse.emf.cdo.examples.company.Product;
import org.eclipse.emf.cdo.examples.company.VAT;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.embedded.CDOEmbeddedRepositoryConfig;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.h2.H2Adapter;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.ecore.EPackage;

import org.h2.jdbcx.JdbcDataSource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Runs a persistent embedded CDO repository backed by a file-based H2 database.
 * The Company model is created on first use and loaded on subsequent runs.
 *
 * @author Eike Stepper
 */
public final class EmbeddedH2Example
{
  private static final String RESOURCE_PATH = "/company";

  private static final long VIEW_UPDATE_TIMEOUT_MILLIS = 2000L;

  private EmbeddedH2Example()
  {
  }

  /**
   * Initializes the managed container and embedded repository, then runs the Company example.
   * Existing database contents are retained between application runs.
   *
   * @param args an optional database directory; defaults to {@code target/embedded-h2-data}.
   * @throws Exception if repository access, model loading, or resource cleanup fails.
   */
  public static void main(String[] args) throws Exception
  {
    Path databasePath = args.length == 0 ? Path.of("target", "embedded-h2-data") : Path.of(args[0]);
    Files.createDirectories(databasePath.toAbsolutePath().normalize());

    IManagedContainer container = ContainerUtil.createInitializedContainer();

    try
    {
      RepositoryConfig repositoryConfig = new RepositoryConfig(databasePath, container);

      try
      {
        repositoryConfig.activate();

        try (CDONet4jSession session = repositoryConfig.openClientSession())
        {
          example(session);
        }
      }
      finally
      {
        LifecycleUtil.deactivateNoisy(repositoryConfig);
      }
    }
    finally
    {
      LifecycleUtil.deactivateNoisy(container);
    }
  }

  /**
   * Reads the Company through one long-lived read-only view and creates it in a separate transaction if missing.
   * The view waits for the creation commit before reading the newly committed model.
   *
   * @param session the open CDO session
   * @throws Exception if committing the model or reading the view fails
   */
  private static void example(CDONet4jSession session) throws Exception
  {
    try (CDOView view = session.openView())
    {
      if (view.hasResource(RESOURCE_PATH))
      {
        System.out.println("LOADED");
      }
      else
      {
        CDOCommitInfo commitInfo;

        try (CDOTransaction transaction = session.openTransaction())
        {
          createCompany(transaction);
          commitInfo = transaction.commit();
        }

        if (!view.waitForUpdate(commitInfo.getTimeStamp(), VIEW_UPDATE_TIMEOUT_MILLIS))
        {
          throw new IllegalStateException("Read-only view did not receive the committed update within " + VIEW_UPDATE_TIMEOUT_MILLIS + " ms."); //$NON-NLS-1$
        }

        System.out.println("CREATED");
      }

      printCompany(view);
    }
  }

  /**
   * Adds the example Company, Category, and Products to a transaction.
   *
   * @param transaction the transaction that will commit the new model.
   */
  private static void createCompany(CDOTransaction transaction)
  {
    Product keyboard = CompanyFactory.eINSTANCE.createProduct();
    keyboard.setName("Keyboard"); //$NON-NLS-1$
    keyboard.setVat(VAT.VAT15);
    keyboard.setPrice(49.95f);

    Product mouse = CompanyFactory.eINSTANCE.createProduct();
    mouse.setName("Mouse"); //$NON-NLS-1$
    mouse.setVat(VAT.VAT15);
    mouse.setPrice(19.95f);

    Category category = CompanyFactory.eINSTANCE.createCategory();
    category.setName("Hardware"); //$NON-NLS-1$
    category.getProducts().add(keyboard);
    category.getProducts().add(mouse);

    Company company = CompanyFactory.eINSTANCE.createCompany();
    company.setName("Example Company"); //$NON-NLS-1$
    company.getCategories().add(category);

    CDOResource resource = transaction.createResource(RESOURCE_PATH);
    resource.getContents().add(company);
  }

  /**
   * Reads and prints the complete Company structure through a read-only view.
   *
   * @param view the view that supplies the Company model.
   */
  private static void printCompany(CDOView view)
  {
    CDOResource resource = view.getResource(RESOURCE_PATH);

    Company company = (Company)resource.getContents().get(0);
    System.out.println("Company: " + company.getName()); //$NON-NLS-1$

    for (Category category : company.getCategories())
    {
      System.out.println("  Category: " + category.getName()); //$NON-NLS-1$

      for (Product product : category.getProducts())
      {
        System.out.println("    Product: " + product.getName() //$NON-NLS-1$
            + " (" + product.getPrice() //$NON-NLS-1$
            + ", " + product.getVat() //$NON-NLS-1$
            + ")"); //$NON-NLS-1$
      }
    }
  }

  /**
   * Configures the persistent H2 store and registers the existing Company model with the embedded repository.
   *
   * @author Eike Stepper
   */
  private static final class RepositoryConfig extends CDOEmbeddedRepositoryConfig
  {
    private final Path databasePath;

    private final IManagedContainer container;

    private RepositoryConfig(Path databasePath, IManagedContainer container)
    {
      super("embedded-h2-example"); //$NON-NLS-1$
      this.databasePath = databasePath.toAbsolutePath().normalize();
      this.container = container;
    }

    @Override
    public IManagedContainer getContainer()
    {
      return container;
    }

    @Override
    public void initPackages(IRepository repository, List<EPackage> packages)
    {
      packages.add(CompanyPackage.eINSTANCE);
    }

    @Override
    public IStore createStore(IManagedContainer container)
    {
      String databaseFile = databasePath.resolve("company").toString().replace('\\', '/'); //$NON-NLS-1$

      JdbcDataSource dataSource = new JdbcDataSource();
      dataSource.setURL("jdbc:h2:file:" + databaseFile); //$NON-NLS-1$

      IMappingStrategy mappingStrategy = CDODBUtil.createHorizontalMappingStrategy(false);
      IDBAdapter dbAdapter = new H2Adapter();
      IDBConnectionProvider connectionProvider = dbAdapter.createConnectionProvider(dataSource);

      return CDODBUtil.createStore(mappingStrategy, dbAdapter, connectionProvider);
    }

    @Override
    public void initProperties(IManagedContainer container, Map<String, String> properties)
    {
      properties.put(IRepository.Props.SUPPORTING_AUDITS, "false"); //$NON-NLS-1$
    }
  }
}
