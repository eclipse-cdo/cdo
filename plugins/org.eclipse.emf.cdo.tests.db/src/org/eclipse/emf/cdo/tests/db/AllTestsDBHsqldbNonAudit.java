/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stefan Winkler - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_261218_Test;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;
import org.eclipse.emf.cdo.tests.db.verifier.DBStoreVerifier;
import org.eclipse.emf.cdo.tests.db.verifier.DBStoreVerifier.NonAudit;

import java.util.List;
import java.util.Map;

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
    addScenario(parent, COMBINED, AllTestsDBHsqldbNonAudit.HsqldbNonAudit.INSTANCE, TCP, NATIVE);
  }

  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses)
  {
    super.initTestClasses(testClasses);

    // this takes ages - so for now, we disable it
    testClasses.remove(Bugzilla_261218_Test.class);
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

  public static class HsqldbNonAudit extends AllTestsDBHsqldb.Hsqldb
  {
    private static final long serialVersionUID = 1L;

    public static final AllTestsDBHsqldbNonAudit.HsqldbNonAudit INSTANCE = new HsqldbNonAudit(
        "DBStore: Hsqldb (non audit)");

    public HsqldbNonAudit(String name)
    {
      super(name);
    }

    @Override
    protected void initRepositoryProperties(Map<String, String> props)
    {
      super.initRepositoryProperties(props);
      props.put(IRepository.Props.SUPPORTING_AUDITS, "false");
    }

    @Override
    protected IMappingStrategy createMappingStrategy()
    {
      return CDODBUtil.createHorizontalMappingStrategy(false);
    }

    @Override
    protected DBStoreVerifier getVerifier(IRepository repository)
    {
      return new NonAudit(repository);
    }
  }
}
