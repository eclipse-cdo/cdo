/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Ibrahim Sallam - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.objectivity;

import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.server.IQueryContext;
import org.eclipse.emf.cdo.server.IQueryHandler;

public class ObjectivityQueryHandler implements IQueryHandler
{

  public static final String QUERY_LANGUAGE = "OBJY";

  protected ObjectivityStoreAccessor storeAccessor = null;

  public ObjectivityQueryHandler(ObjectivityStoreAccessor storeAccessor)
  {
    this.storeAccessor = storeAccessor;
  }

  public void executeQuery(CDOQueryInfo info, IQueryContext context)
  {
    // TODO
  }

}
