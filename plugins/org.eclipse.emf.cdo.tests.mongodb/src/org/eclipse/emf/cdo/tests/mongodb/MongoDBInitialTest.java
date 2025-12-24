/*
 * Copyright (c) 2011, 2012, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.mongodb;

import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.internal.mongodb.MongoDBStore;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import java.io.PrintStream;

/**
 * @author Eike Stepper
 */
public class MongoDBInitialTest extends AbstractCDOTest
{
  public void testGetContentsClearedCache() throws Exception
  {
    {
      msg("Opening session");
      CDOSession session = openSession();

      msg("Opening transaction");
      CDOTransaction transaction = session.openTransaction();

      msg("Creating resource");
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));

      msg("Creating supplier");
      Supplier supplier = getModel1Factory().createSupplier();

      msg("Setting name");
      supplier.setName("Stepper");

      msg("Adding supplier");
      resource.getContents().add(supplier);

      msg("Committing");
      transaction.commit();
      session.close();
    }

    clearCache(getRepository().getRevisionManager());

    msg("Opening session");
    CDOSession session = openSession();

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Getting resource");
    CDOResource resource = transaction.getResource(getResourcePath("/test1"));

    msg("Getting contents");
    EList<EObject> contents = resource.getContents();
    assertNotNull(contents);

    Supplier supplier = (Supplier)contents.get(0);
    String name = supplier.getName();
    assertEquals("Stepper", name);
  }

  public void testGetResourceClearedCache() throws Exception
  {
    Supplier supplier = getModel1Factory().createSupplier();
    supplier.setName("Stepper");

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    resource.getContents().add(supplier);
    transaction.commit();

    clearCache(getRepository().getRevisionManager());

    // query(new BasicDBObject(Commits.REVISIONS, new BasicDBObject("$elemMatch", new BasicDBObject(
    // Commits.REVISIONS_RESOURCE, 1))));
    transaction = session.openTransaction();

    msg("Getting resource");
    resource = transaction.getResource(getResourcePath("/test1"), true);
    assertNotNull(resource);
    assertEquals(URI.createURI("cdo://" + session.getRepositoryInfo().getUUID() + getResourcePath("/test1")), resource.getURI());
    assertEquals(transaction.getResourceSet(), resource.getResourceSet());
    assertEquals(1, transaction.getResourceSet().getResources().size());
    assertEquals(CDOState.CLEAN, resource.cdoState());
    assertEquals(transaction, resource.cdoView());
    assertNotNull(resource.cdoRevision());
  }

  protected void query(DBObject query)
  {
    System.err.println();
    System.err.println("Query:");
    showDocument(query, "");
    System.err.println();
    System.err.println("Results:");

    MongoDBStore store = (MongoDBStore)getRepository().getStore();
    DBCollection collection = store.getCommits().getCollection();
    DBCursor cursor = collection.find(query);

    int i = 0;
    while (cursor.hasNext())
    {
      ++i;
      System.out.println("" + i + " = ");
      showDocument(cursor.next(), "    ");
    }

    ConcurrencyUtil.sleep(1000000L);
  }

  protected void showDocument(DBObject doc, String level)
  {
    PrintStream pout = System.out;
    for (String key : doc.keySet())
    {
      pout.print(level);
      pout.print(key);
      pout.print(" = ");

      Object value = doc.get(key);
      if (value instanceof DBObject)
      {
        DBObject child = (DBObject)value;
        pout.println();
        showDocument(child, level + "    ");
      }
      else
      {
        if (value instanceof String)
        {
          pout.print("\"");
        }

        pout.print(value);
        if (value instanceof String)
        {
          pout.print("\"");
        }

        pout.println();
      }
    }
  }
  //
  // @Override
  // protected IScenario getDefaultScenario()
  // {
  // Scenario scenario = new Scenario();
  // scenario.setContainerConfig(ContainerConfig.Combined.INSTANCE);
  // scenario.setRepositoryConfig(MongoDBStoreRepositoryConfig.INSTANCE);
  // scenario.setSessionConfig(Net4j.JVM.INSTANCE);
  // scenario.setModelConfig(ModelConfig.Native.INSTANCE);
  // return scenario;
  // }
}
