/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - introduced variable mapping strategies
 */
package org.eclipse.emf.cdo.tests.mongodb;

import org.eclipse.emf.cdo.tests.AllConfigs;
import org.eclipse.emf.cdo.tests.MEMStoreQueryTest;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AllTestsMongoDB extends AllConfigs
{
  public static Test suite()
  {
    return new AllTestsMongoDB().getTestSuite("CDO Tests (MongoDB)");
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, COMBINED, MongoDBStoreRepositoryConfig.INSTANCE, JVM, NATIVE);
    addScenario(parent, COMBINED, MongoDBStoreRepositoryConfig.AUDITING, JVM, NATIVE);
    // addScenario(parent, COMBINED, MongoDBStoreRepositoryConfig.BRANCHING, JVM, NATIVE);
  }

  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses)
  {
    testClasses.add(MongoDBInitialTest.class);
    super.initTestClasses(testClasses);
    testClasses.remove(MEMStoreQueryTest.class);
  }
}
