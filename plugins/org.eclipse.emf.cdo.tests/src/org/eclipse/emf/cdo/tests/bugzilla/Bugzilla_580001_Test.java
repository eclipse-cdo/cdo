/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.model.CDOType.Handler;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.server.IQueryContext;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.IQueryHandlerProvider;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.view.CDOQuery;
import org.eclipse.emf.cdo.view.CDOView;

import java.io.IOException;

/**
 * Bug 580001 - Provide a CDOType.Handler.Registry that supports custom data types in query parameters and results.
 *
 * @author Eike Stepper
 */
public class Bugzilla_580001_Test extends AbstractCDOTest
{
  private static final String LANG = "my-data-ql";

  @CleanRepositoriesBefore(reason = "Needs special query handler")
  @CleanRepositoriesAfter(reason = "Needs special query handler")
  public void testTypeHandler() throws Exception
  {
    CDOType.Handler.Registry.INSTANCE.addHandler(new Handler("my-data-type")
    {
      @Override
      public boolean canHandle(Object value)
      {
        return value instanceof MyDataType;
      }

      @Override
      public void writeValue(CDODataOutput out, Object value) throws IOException
      {
        out.writeString(((MyDataType)value).getValue());
      }

      @Override
      public Object readValue(CDODataInput in) throws IOException
      {
        return new MyDataType(in.readString());
      }
    });

    registerQueryHandler(new IQueryHandler()
    {
      @Override
      public void executeQuery(CDOQueryInfo info, IQueryContext context)
      {
        context.addResult(new MyDataType(info.getQueryString()));
      }
    });

    CDOSession session = openSession();
    CDOView view = session.openView();

    CDOQuery query = view.createQuery(LANG, "RESULT");
    MyDataType result = query.getResultValue();
    assertEquals("RESULT", result.getValue());
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

  /**
   * @author Eike Stepper
   */
  public static final class MyDataType
  {
    private final String value;

    public MyDataType(String value)
    {
      this.value = value;
    }

    public String getValue()
    {
      return value;
    }

    @Override
    public String toString()
    {
      return value;
    }
  }
}
