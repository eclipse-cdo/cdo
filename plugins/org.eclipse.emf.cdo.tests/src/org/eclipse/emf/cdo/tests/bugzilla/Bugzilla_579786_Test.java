/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.server.IQueryContext;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.IQueryHandlerProvider;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.view.CDOQuery;
import org.eclipse.emf.cdo.view.CDOView;

/**
 * @author Eike Stepper
 */
public class Bugzilla_579786_Test extends AbstractCDOTest
{
  private static final String LANG = "test-ql";

  private static final int RESULT = 4711;

  private static final int TIMEOUT = 1500; // Must be greater than 1000.

  @CleanRepositoriesBefore(reason = "Needs special query handler")
  @CleanRepositoriesAfter(reason = "Needs special query handler")
  public void testDisableResponseTimeout() throws Exception
  {
    registerQueryHandler(new IQueryHandler()
    {
      @Override
      public void executeQuery(CDOQueryInfo info, IQueryContext context)
      {
        sleep(TIMEOUT + 100);
        context.addResult(RESULT);
      }
    });

    CDOSession session = openSession();
    ((CDONet4jSession)session).options().setSignalTimeout(TIMEOUT);
    CDOView view = session.openView();

    CDOQuery query = view.createQuery(LANG, "test");
    query.setParameter(CDOQueryInfo.PARAM_DISABLE_RESPONSE_TIMEOUT, true);
    int result = query.getResultValue();
    assertEquals(RESULT, result);
  }

  @CleanRepositoriesBefore(reason = "Needs special query handler")
  @CleanRepositoriesAfter(reason = "Needs special query handler")
  public void testSlowQueryHandler() throws Exception
  {
    registerQueryHandler(new IQueryHandler.PotentiallySlow()
    {
      @Override
      public void executeQuery(CDOQueryInfo info, IQueryContext context)
      {
        sleep(TIMEOUT + 100);
        context.addResult(RESULT);
      }

      @Override
      public boolean isSlow(CDOQueryInfo info)
      {
        return true;
      }
    });

    CDOSession session = openSession();
    ((CDONet4jSession)session).options().setSignalTimeout(TIMEOUT);
    CDOView view = session.openView();

    CDOQuery query = view.createQuery(LANG, "test");
    int result = query.getResultValue();
    assertEquals(RESULT, result);
  }

  private void registerQueryHandler(IQueryHandler handler)
  {
    getTestProperties().put(RepositoryConfig.PROP_TEST_QUERY_HANDLER_PROVIDER, new IQueryHandlerProvider()
    {
      @Override
      public IQueryHandler getQueryHandler(CDOQueryInfo info)
      {
        return handler;
      }
    });
  }
}
