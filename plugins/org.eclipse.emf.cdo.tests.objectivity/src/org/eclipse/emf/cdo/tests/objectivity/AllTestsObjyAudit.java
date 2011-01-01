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
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;

import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AllTestsObjyAudit extends ObjyDBConfigs
{
  public static Test suite()
  {
    return new AllTestsObjyAudit().getTestSuite("CDO Tests (ObjectivityStore - audit mode)"); //$NON-NLS-1$
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    ObjyStoreRepositoryConfig repoConfig = ObjyAuditConfig.INSTANCE;
    addScenario(parent, COMBINED, repoConfig, JVM, NATIVE);
  }

  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses)
  {
    super.initTestClasses(testClasses);
  }

  @Override
  protected boolean hasAuditSupport()
  {
    return true;
  }

  @Override
  protected boolean hasBranchingSupport()
  {
    return false;
  }

  public static class ObjyAuditConfig extends ObjyStoreRepositoryConfig
  {
    private static final long serialVersionUID = 1L;

    public static final AllTestsObjyAudit.ObjyAuditConfig INSTANCE = new ObjyAuditConfig("ObjectivityStore: (audit)"); //$NON-NLS-1$

    public ObjyAuditConfig(String name)
    {
      super(name);

      org.eclipse.emf.cdo.server.internal.objectivity.bundle.OM.DEBUG.setEnabled(true);
      org.eclipse.emf.cdo.server.internal.objectivity.bundle.OM.INFO.setEnabled(true);
    }

    @Override
    protected void initRepositoryProperties(Map<String, String> props)
    {
      super.initRepositoryProperties(props);
      props.put(IRepository.Props.SUPPORTING_AUDITS, "true"); //$NON-NLS-1$
      props.put(IRepository.Props.SUPPORTING_BRANCHES, "false"); //$NON-NLS-1$
    }
  }
}
