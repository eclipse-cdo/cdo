/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - 289360: [DB] [maintenance] Support FeatureMaps
 */
package org.eclipse.emf.cdo.tests.objectivity;

import org.eclipse.emf.cdo.tests.AuditTest;
import org.eclipse.emf.cdo.tests.AuditTestSameSession;
import org.eclipse.emf.cdo.tests.BranchingTest;
import org.eclipse.emf.cdo.tests.BranchingTestSameSession;
import org.eclipse.emf.cdo.tests.ExternalReferenceTest;
import org.eclipse.emf.cdo.tests.XATransactionTest;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_252214_Test;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;


import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AllTestsObjyNonAudit extends ObjyDBConfigs
{
  public static Test suite()
  {
    return new AllTestsObjyNonAudit()
        .getTestSuite("CDO Tests (DBStoreRepositoryConfig Objectivity/DB - non-audit mode)");
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
  	ObjyStoreRepositoryConfig repoConfig = new ObjyStoreRepositoryConfig();
    addScenario(parent, COMBINED, repoConfig, JVM, NATIVE);
  }

  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses)
  {
    super.initTestClasses(testClasses);

    // non-audit mode - remove audit tests
    testClasses.remove(AuditTest.class);
    testClasses.remove(AuditTestSameSession.class);
    testClasses.remove(Bugzilla_252214_Test.class);
    // non-branching mode - remove branch tests.
    testClasses.remove(BranchingTest.class);
    testClasses.remove(BranchingTestSameSession.class);
    
    // Objy has a deadlock issue which prevent this test from completing.
//    testClasses.remove(ExternalReferenceTest.class);
//    testClasses.remove(XATransactionTest.class);
    
    
  }

}
