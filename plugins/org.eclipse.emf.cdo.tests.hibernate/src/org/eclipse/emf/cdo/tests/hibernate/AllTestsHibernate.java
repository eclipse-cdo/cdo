/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.tests.AllTestsAllConfigs;
import org.eclipse.emf.cdo.tests.AuditTest;
import org.eclipse.emf.cdo.tests.ExternalReferenceTest;
import org.eclipse.emf.cdo.tests.MEMStoreQueryTest;
import org.eclipse.emf.cdo.tests.MetaTest;
import org.eclipse.emf.cdo.tests.XATransactionTest;
import org.eclipse.emf.cdo.tests.AuditTest.LocalAuditTest;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_259869_Test;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AllTestsHibernate extends AllTestsAllConfigs
{
  public static final RepositoryConfig HIBERNATE = HibernateConfig.INSTANCE;

  public static Test suite()
  {
    return new AllTestsHibernate().getTestSuite("CDO Tests (Hibernate)");
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, COMBINED, HIBERNATE, TCP, NATIVE);
  }

  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses)
  {
    super.initTestClasses(testClasses);

    // audit support to do
    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=244141
    testClasses.remove(AuditTest.class);
    testClasses.remove(LocalAuditTest.class);

    // MemStore is not relevant
    testClasses.remove(MEMStoreQueryTest.class);

    // XATransactions are not yet supported
    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=282649
    testClasses.remove(XATransactionTest.class);
    testClasses.remove(Bugzilla_259869_Test.class);

    // External refs are not yet supported
    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=282651
    testClasses.remove(ExternalReferenceTest.class);

    // Metaref is not yet supported
    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=282655
    testClasses.remove(MetaTest.class);

    // add the hibernate query test
    testClasses.add(HibernateQueryTest.class);

    // testClasses.clear();
    // testClasses.add(AttributeTest.class);
    // testClasses.add(Bugzilla_258933_Test.class);
  }
}
