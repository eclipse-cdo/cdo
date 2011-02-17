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
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IScenario;
import org.eclipse.emf.cdo.tests.config.impl.ContainerConfig;
import org.eclipse.emf.cdo.tests.config.impl.ModelConfig;
import org.eclipse.emf.cdo.tests.config.impl.Scenario;
import org.eclipse.emf.cdo.tests.config.impl.SessionConfig.Net4j;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

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

    sleep(1000000L);
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
