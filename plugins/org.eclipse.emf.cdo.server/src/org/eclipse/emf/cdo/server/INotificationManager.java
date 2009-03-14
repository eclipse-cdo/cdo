/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public interface INotificationManager
{
  public IRepository getRepository();

  /**
   * Internal.
   */
  public void setRepository(IRepository repository);

  /**
   * Internal.
   */
  public void notifyCommit(ISession session, IStoreAccessor.CommitContext commitContext);
}
