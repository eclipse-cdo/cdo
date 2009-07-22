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
import org.eclipse.emf.cdo.tests.FeatureMapTest;
import org.eclipse.emf.cdo.tests.ResourceTest;
import org.eclipse.emf.cdo.tests.XATransactionTest;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_258933_Test;
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

    // fails because of Bug 283992
    testClasses.remove(ResourceTest.class);
    testClasses.add(DISABLE_ResourceTest.class);

    // fails because of Bug 284109
    testClasses.remove(XATransactionTest.class);
    testClasses.add(DISABLE_XATransactionTest.class);

    // fails because of Bug 284110
    testClasses.remove(Bugzilla_258933_Test.class);
    testClasses.add(DISABLE_Bugzilla_258933_Test.class);

    // fails because of Bug 254455
    testClasses.remove(FeatureMapTest.class);
  }
}
