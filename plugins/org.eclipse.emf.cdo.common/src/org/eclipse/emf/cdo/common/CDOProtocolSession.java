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
package org.eclipse.emf.cdo.common;

import org.eclipse.net4j.util.security.IUserAware;

/**
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOProtocolSession extends IUserAware
{
  public int getSessionID();

  public CDOProtocolView[] getViews();

  public CDOProtocolView getView(int viewID);

  /**
   * @since 2.0
   */
  public boolean isPassiveUpdateEnabled();

  /**
   * @since 2.0
   */
  public void close();

  /**
   * @since 2.0
   */
  public boolean isClosed();
}
