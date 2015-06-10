/*
 * Copyright (c) 2007-2009, 2011, 2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.internal.ui.actions;

import org.eclipse.net4j.buddies.ISessionManager;
import org.eclipse.net4j.buddies.internal.ui.messages.Messages;
import org.eclipse.net4j.util.ui.actions.SafeAction;

/**
 * @author Eike Stepper
 */
public final class DisconnectAction extends SafeAction
{
  public DisconnectAction()
  {
    super(Messages.getString("DisconnectAction_0"), Messages.getString("DisconnectAction_1")); //$NON-NLS-1$ //$NON-NLS-2$
  }

  @Override
  protected void safeRun() throws Exception
  {
    ISessionManager.INSTANCE.disconnect();
  }
}
