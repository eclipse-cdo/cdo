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
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.tests.AllTestsAllConfigs;
import org.eclipse.emf.cdo.tests.AttributeTest;
import org.eclipse.emf.cdo.tests.ExternalReferenceTest;
import org.eclipse.emf.cdo.tests.FeatureMapTest;
import org.eclipse.emf.cdo.tests.XATransactionTest;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_259869_Test;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;

import java.util.List;

/**
 * @author Eike Stepper
 */
public abstract class DBConfigs extends AllTestsAllConfigs
{
  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses)
  {
    super.initTestClasses(testClasses);
    testClasses.add(DBStoreTest.class);
    testClasses.add(SQLQueryTest.class);
    testClasses.add(DBAnnotationsTest.class);

    // fails because of Bug 284109
    testClasses.remove(XATransactionTest.class);
    testClasses.add(DISABLE_XATransactionTest.class);

    // ------------ tests below only fail for PostgreSQL
    // ------------ therefore they are overridden and
    // ------------ skipConfig for PSQL is used temporarily
    // XXX [PSQL] disabled because of Bug 289445
    testClasses.remove(AttributeTest.class);
    testClasses.add(DISABLE_AttributeTest.class);

    testClasses.remove(FeatureMapTest.class);
    testClasses.add(DISABLE_FeatureMapTest.class);

    // XXX [PSQL] disabled because of Bug 290095
    // using skipconfig in DBAnnotationTest

    // XXX [PSQL] disabled because of Bug 290097
    testClasses.remove(ExternalReferenceTest.class);
    testClasses.add(DISABLE_ExternalReferenceTest.class);

    // XXX [PSQL] disabled because of Bug 290097
    testClasses.remove(Bugzilla_259869_Test.class);
    testClasses.add(DISABLE_Bugzilla_259869_Test.class);
  }
}
