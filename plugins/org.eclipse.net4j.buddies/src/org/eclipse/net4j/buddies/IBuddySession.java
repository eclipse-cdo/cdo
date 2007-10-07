/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.buddies;

import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.buddies.protocol.IBuddyAccount;
import org.eclipse.net4j.util.container.IContainer;

import java.util.Set;

/**
 * @author Eike Stepper
 */
public interface IBuddySession extends IContainer<String>
{
  public IChannel getChannel();

  public IBuddyAccount getAccount();

  public Set<String> getBuddies();

  public void close();
}
