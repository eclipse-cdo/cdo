/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.tests.AuditTest;
import org.eclipse.emf.cdo.tests.AuditTestSameSession;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_252214_Test;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;

import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AllTestsObjyNonAudit extends ObjyDBConfigs
{
  public static Test suite()
  {
    return new AllTestsObjyNonAudit().getTestSuite("CDO Tests (ObjectivityStore - non-audit mode)"); //$NON-NLS-1$
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    // ObjyStoreRepositoryConfig repoConfig = ObjyNonAuditConfig.INSTANCE;
    ObjyStoreRepositoryConfig repoConfig = new ObjyNonAuditConfig("ObjectivityStore: (non-audit)"); //$NON-NLS-1$
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
    // testClasses.remove(BranchingTest.class);
    // testClasses.remove(BranchingTestSameSession.class);

    // Objy has a deadlock issue which prevent this test from completing.
    // testClasses.remove(ExternalReferenceTest.class);
    // testClasses.remove(XATransactionTest.class);

  }

  @Override
  protected boolean hasAuditSupport()
  {
    return false;
  }

  @Override
  protected boolean hasBranchingSupport()
  {
    return false;
  }

  public static class ObjyNonAuditConfig extends ObjyStoreRepositoryConfig
  {
    private static final long serialVersionUID = 1L;

    public static final AllTestsObjyNonAudit.ObjyNonAuditConfig INSTANCE = new ObjyNonAuditConfig(
        "ObjectivityStore: (non-audit)"); //$NON-NLS-1$

    public ObjyNonAuditConfig(String name)
    {
      super(name);

      org.eclipse.emf.cdo.server.internal.objectivity.bundle.OM.DEBUG.setEnabled(true);
      org.eclipse.emf.cdo.server.internal.objectivity.bundle.OM.INFO.setEnabled(true);
    }

    @Override
    protected void initRepositoryProperties(Map<String, String> props)
    {
      super.initRepositoryProperties(props);
      props.put(IRepository.Props.SUPPORTING_AUDITS, "false"); //$NON-NLS-1$
      props.put(IRepository.Props.SUPPORTING_BRANCHES, "false"); //$NON-NLS-1$
    }
  }
}
