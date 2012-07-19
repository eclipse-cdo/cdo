/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stefan Winkler - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_261218_Test;
import org.eclipse.emf.cdo.tests.config.IScenario;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Stefan Winkler
 */
public class AllTestsDBHsqldbNonAudit extends DBConfigs
{
  public static Test suite()
  {
    return new AllTestsDBHsqldbNonAudit().getTestSuite("CDO Tests (DBStore Hsql Horizontal Non-audit)");
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, COMBINED, new HsqldbConfig(false, false, IDGenerationLocation.STORE), TCP, NATIVE);
  }

  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses, IScenario scenario)
  {
    super.initTestClasses(testClasses, scenario);

    // this takes ages - so for now, we disable it
    testClasses.remove(Bugzilla_261218_Test.class);
  }
}
