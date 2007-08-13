/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.internal.server.protocol.CDOServerProtocol;

import org.eclipse.net4j.util.container.IContainer;

/**
 * @author Eike Stepper
 */
public interface ISession extends IContainer<IView>
{
  public ISessionManager getSessionManager();

  public CDOServerProtocol getProtocol();

  public int getSessionID();

  public IView[] getViews();

  public IView getView(int viewID);
}
