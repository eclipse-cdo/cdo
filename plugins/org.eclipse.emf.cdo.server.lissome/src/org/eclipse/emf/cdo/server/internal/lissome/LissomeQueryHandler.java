/*
 * Copyright (c) 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.lissome;

import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.server.IQueryContext;
import org.eclipse.emf.cdo.server.IQueryHandler;

/**
 * @author Eike Stepper
 */
public class LissomeQueryHandler implements IQueryHandler
{
  public static final String QUERY_LANGUAGE = "lissome";

  private LissomeStoreReader reader;

  public LissomeQueryHandler(LissomeStoreReader reader)
  {
    this.reader = reader;
  }

  public LissomeStoreReader getReader()
  {
    return reader;
  }

  @Override
  public void executeQuery(CDOQueryInfo info, IQueryContext context)
  {
    // TODO: implement LissomeQueryHandler.executeQuery(info, context)
    throw new UnsupportedOperationException();
  }
}
