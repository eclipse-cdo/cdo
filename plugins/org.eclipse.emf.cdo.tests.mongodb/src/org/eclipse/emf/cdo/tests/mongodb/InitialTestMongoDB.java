/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.mongodb;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.internal.mongodb.Commits;
import org.eclipse.emf.cdo.server.internal.mongodb.MongoDBStore;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IScenario;
import org.eclipse.emf.cdo.tests.config.impl.ContainerConfig;
import org.eclipse.emf.cdo.tests.config.impl.ModelConfig;
import org.eclipse.emf.cdo.tests.config.impl.Scenario;
import org.eclipse.emf.cdo.tests.config.impl.SessionConfig.Net4j;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import java.io.PrintStream;

/**
 * @author Eike Stepper
 */
public class InitialTestMongoDB extends AbstractCDOTest
{
  public void testGetResource() throws Exception
  {
    Supplier supplier = getModel1Factory().createSupplier();
    supplier.setName("Stepper");

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    resource.getContents().add(supplier);
    transaction.commit();

    query(new BasicDBObject(Commits.REVISIONS, new BasicDBObject("$elemMatch", new BasicDBObject(
        Commits.REVISIONS_RESOURCE, 1))));
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

  @Override
  protected IScenario getDefaultScenario()
  {
    Scenario scenario = new Scenario();
    scenario.setContainerConfig(ContainerConfig.Combined.INSTANCE);
    scenario.setRepositoryConfig(MongoDBStoreRepositoryConfig.INSTANCE);
    scenario.setSessionConfig(Net4j.JVM.INSTANCE);
    scenario.setModelConfig(ModelConfig.Native.INSTANCE);
    return scenario;
  }
}
