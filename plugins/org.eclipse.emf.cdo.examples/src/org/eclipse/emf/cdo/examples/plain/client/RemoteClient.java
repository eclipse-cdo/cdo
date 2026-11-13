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
package org.eclipse.emf.cdo.examples.plain.client;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.examples.company.Category;
import org.eclipse.emf.cdo.examples.company.Company;
import org.eclipse.emf.cdo.examples.company.CompanyFactory;
import org.eclipse.emf.cdo.examples.company.Product;
import org.eclipse.emf.cdo.examples.company.VAT;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.tcp.ITCPConnector;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * Connects to a remote CDO repository and demonstrates first-write and read-first model loading.
 * The optional address argument can be a port, a hostname, or a {@code hostname:port} pair. The
 * default connection address is {@code localhost:2036}; for example, use {@code 2036},
 * {@code server.example.org}, or {@code server.example.org:2036}. The hostname is the address
 * the client connects to, so it must be reachable from the client machine.
 *
 * @author Eike Stepper
 */
public final class RemoteClient
{
  private static final String RESOURCE_PATH = "/company"; //$NON-NLS-1$

  private static final long VIEW_UPDATE_TIMEOUT_MILLIS = 2000L;

  private RemoteClient()
  {
  }

  /**
   * Opens a remote CDO session and runs the Company example. The optional argument may be a port
   * (for example, {@code 2036}), a hostname (for example, {@code server.example.org}), or a
   * {@code hostname:port} pair (for example, {@code server.example.org:2036}). With no argument,
   * the client connects to {@code localhost:2036}. This is the remote address the client connects
   * to, not a server bind address.
   *
   * @param args zero or one connection address, hostname, or port
   * @throws Exception if connection, model access, or cleanup fails
   */
  public static void main(String[] args) throws Exception
  {
    String address = parseAddress(args);
    IManagedContainer container = ContainerUtil.createInitializedContainer();

    try
    {
      ITCPConnector connector = TCPUtil.getConnector(container, address);

      CDONet4jSessionConfiguration configuration = CDONet4jUtil.createNet4jSessionConfiguration();
      configuration.setConnector(connector);
      configuration.setRepositoryName("repo1"); //$NON-NLS-1$

      try (CDONet4jSession session = configuration.openNet4jSession())
      {
        example(session);
      }
    }
    finally
    {
      container.deactivate();
    }
  }

  /**
   * Parses the optional client connection argument.
   *
   * @param args zero or one hostname, port, or {@code hostname:port} argument
   * @return the Net4j connector address, defaulting to {@code localhost:2036}
   */
  private static String parseAddress(String[] args)
  {
    if (args.length > 1)
    {
      throw new IllegalArgumentException("Expected at most one connection address, hostname, or port."); //$NON-NLS-1$
    }

    if (args.length == 0)
    {
      return "localhost:" + ITCPConnector.DEFAULT_PORT; //$NON-NLS-1$
    }

    String argument = args[0];
    if (argument.indexOf(':') >= 0)
    {
      return argument;
    }

    if (argument.chars().allMatch(Character::isDigit))
    {
      return "localhost:" + argument; //$NON-NLS-1$
    }

    return argument + ":" + ITCPConnector.DEFAULT_PORT; //$NON-NLS-1$
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
}
