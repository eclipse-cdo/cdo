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
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.common.CDOQueryInfo;
import org.eclipse.emf.cdo.internal.server.QueryResult;
import org.eclipse.emf.cdo.server.IView;

/**
 * @author Eike Stepper
 */
public interface InternalQueryManager
{
  public InternalRepository getRepository();

  public void setRepository(InternalRepository repository);

  public QueryResult execute(IView view, CDOQueryInfo queryInfo);

  public boolean isRunning(int queryID);

  public void cancel(int queryID);
}
