/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IView;

/**
 * @author Eike Stepper
 */
public class NOOPStore extends LongIDStore
{
  public static final String TYPE = "noop";

  public NOOPStore()
  {
    super(TYPE);
  }

  public boolean hasBranchingSupport()
  {
    return false;
  }

  public boolean hasWriteDeltaSupport()
  {
    return true;
  }

  public boolean hasAuditingSupport()
  {
    return true;
  }

  public void repairAfterCrash()
  {
  }

  @Override
  public NOOPStoreAccessor createReader(ISession session)
  {
    return new NOOPStoreAccessor(this, session);
  }

  @Override
  public NOOPStoreAccessor createWriter(IView view)
  {
    return new NOOPStoreAccessor(this, view);
  }
}
