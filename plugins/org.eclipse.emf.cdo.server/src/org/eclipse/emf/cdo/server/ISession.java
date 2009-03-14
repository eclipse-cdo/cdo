/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/230832        
 */
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.common.CDOCommonSession;

import org.eclipse.net4j.util.container.IContainer;

/**
 * @author Eike Stepper
 */
public interface ISession extends CDOCommonSession, IContainer<IView>
{
  public ISessionManager getSessionManager();

  /**
   * @since 2.0
   */
  public boolean isSubscribed();

  /**
   * @since 2.0
   */
  public IView openView(int viewID);

  /**
   * @since 2.0
   */
  public IAudit openAudit(int viewID, long timeStamp);

  /**
   * @since 2.0
   */
  public ITransaction openTransaction(int viewID);
}
