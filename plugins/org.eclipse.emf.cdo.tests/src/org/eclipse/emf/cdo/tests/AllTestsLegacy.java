/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_246622_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_248915_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_251263_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_254489_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_258933_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_259695_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_260756_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_266982_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_273565_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_279982_Test;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Martin Fluegge
 */
public class AllTestsLegacy extends AllTestsAllConfigs
{
  public static Test suite()
  {
    return new AllTestsLegacy().getTestSuite(AllTestsAllConfigs.class.getName());
  }

  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses)
  {
    // testClasses.add(ChunkingTest.class);
    super.initTestClasses(testClasses);

    testClasses.remove(ComplexTest.class);
    testClasses.remove(UnsetTest.class);
    testClasses.remove(PushTransactionTest.class); // ArrayStoreException
    testClasses.remove(ContainmentTest.class); // ArrayStoreException/NullpointerException in Transaction
    testClasses.remove(RollbackTest.class); // Failures
    testClasses.remove(CrossReferenceTest.class); // Failures
    testClasses.remove(ChunkingTest.class); // ArrayStoreException / ClassCastExecption
    testClasses.remove(ChunkingWithMEMTest.class); // java.lang.ClassCastException
    testClasses.remove(MetaTest.class); // wa // NullPointer
    testClasses.remove(AutoAttacherTest.class); // transaction failure
    testClasses.remove(SavePointTest.class);
    testClasses.remove(ChangeSubscriptionTest.class); // timeout
    testClasses.remove(ExternalReferenceTest.class); // NullPointerException / ObjectNotFoundException
    testClasses.remove(XATransactionTest.class);
    testClasses.remove(LockingManagerTest.class); // Locking not support in Legacy Mode
    testClasses.remove(MultiValuedOfAttributeTest.class); // java.lang.ArrayStoreException
    testClasses.remove(ConflictResolverTest.class); // null value in Attribute

    //
    // Bugzilla verifications
    testClasses.remove(Bugzilla_246622_Test.class);
    testClasses.remove(Bugzilla_248915_Test.class); // Failure incomplete resource
    testClasses.remove(Bugzilla_251263_Test.class);
    testClasses.remove(Bugzilla_254489_Test.class); // timeout
    testClasses.remove(Bugzilla_258933_Test.class);
    testClasses.remove(Bugzilla_259695_Test.class); // ArrayIndexOutOfBounds
    testClasses.remove(Bugzilla_260756_Test.class); // ArrayStoreException
    testClasses.remove(Bugzilla_266982_Test.class);
    testClasses.remove(Bugzilla_273565_Test.class); // locking not supported
    testClasses.remove(Bugzilla_279982_Test.class);
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, COMBINED, MEM, JVM, LEGACY);
  }
}
