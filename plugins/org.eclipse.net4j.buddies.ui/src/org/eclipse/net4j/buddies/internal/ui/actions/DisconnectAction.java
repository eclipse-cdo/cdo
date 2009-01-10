/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.buddies.internal.ui.actions;

import org.eclipse.net4j.buddies.ISessionManager;
import org.eclipse.net4j.util.ui.actions.SafeAction;

/**
 * @author Eike Stepper
 */
public final class DisconnectAction extends SafeAction
{
  public DisconnectAction()
  {
    super("Disonnect", "Disconnect from buddies server");
  }

  @Override
  protected void safeRun() throws Exception
  {
    ISessionManager.INSTANCE.disconnect();
  }
}
