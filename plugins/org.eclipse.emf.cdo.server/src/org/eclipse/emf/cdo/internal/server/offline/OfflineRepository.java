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
package org.eclipse.emf.cdo.internal.server.offline;

import org.eclipse.emf.cdo.internal.server.DelegatingRepository;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

/**
 * @author Eike Stepper
 */
public class OfflineRepository extends DelegatingRepository
{
  private InternalRepository delegate;

  private String userID;

  public OfflineRepository(InternalRepository delegate, String userID)
  {
    this.delegate = delegate;
    this.userID = userID;
  }

  @Override
  protected InternalRepository getDelegate()
  {
    return delegate;
  }

  public String getUserID()
  {
    return userID;
  }
}
