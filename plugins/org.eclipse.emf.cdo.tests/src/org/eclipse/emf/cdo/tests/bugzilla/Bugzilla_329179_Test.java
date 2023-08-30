/*
 * Copyright (c) 2021, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Skips;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.util.TestRevisionManager;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.io.IOUtil;

import java.util.Map;

/**
 * Bug 329179 - Make cdoPrefetch() robust against timeouts.
 *
 * @author Eike Stepper
 */
@Skips(IConfig.CAPABILITY_ALL) // This test runs very long on purpose; don't run it in suites.
public class Bugzilla_329179_Test extends AbstractCDOTest
{
  private static final int LEVELS = 3;

  private static final int CATEGORIES = 5;

  private static final int PRODUCTS = 10;

  @Override
  public synchronized Map<String, Object> getTestProperties()
  {
    TestRevisionManager revisionManager = new TestRevisionManager();
    revisionManager.setGetRevisionsDelay(100);

    Map<String, Object> testProperties = super.getTestProperties();
    testProperties.put(RepositoryConfig.PROP_TEST_REVISION_MANAGER, revisionManager);
    return testProperties;
  }

  public void testPrefetchTimeOut() throws Exception
  {
    {
      IOUtil.OUT().println("Creating...");
      Category root = getModel1Factory().createCategory();
      createModel(root, LEVELS, CATEGORIES, PRODUCTS);

      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("res1"));
      resource.getContents().add(root);

      IOUtil.OUT().println("Committing...");
      transaction.commit();
      session.close();
    }

    CDOSession session = openSession();
    ((CDONet4jSession)session).options().setSignalTimeout(2000);
    CDOView view = session.openView();
    CDOResource resource = view.getResource(getResourcePath("res1"));

    IOUtil.OUT().println("Prefetching...");
    resource.cdoPrefetch(CDORevision.DEPTH_INFINITE);
  }
}
