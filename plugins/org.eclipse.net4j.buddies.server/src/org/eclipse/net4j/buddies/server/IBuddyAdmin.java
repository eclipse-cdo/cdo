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
package org.eclipse.net4j.buddies.server;

import org.eclipse.net4j.buddies.internal.server.BuddyAdmin;
import org.eclipse.net4j.buddies.protocol.IAccount;
import org.eclipse.net4j.buddies.protocol.IBuddy;
import org.eclipse.net4j.buddies.protocol.IBuddyProvider;
import org.eclipse.net4j.buddies.protocol.ICollaboration;
import org.eclipse.net4j.buddies.protocol.ICollaborationContainer;
import org.eclipse.net4j.buddies.protocol.ISession;
import org.eclipse.net4j.buddies.protocol.ISessionProvider;
import org.eclipse.net4j.channel.IChannel;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public interface IBuddyAdmin extends ICollaborationContainer, IBuddyProvider, ISessionProvider
{
  public static final IBuddyAdmin INSTANCE = BuddyAdmin.INSTANCE;

  public Map<String, IAccount> getAccounts();

  public ISession openSession(IChannel channel, String userID, String password, String[] facilityTypes);

  public ICollaboration initiateCollaboration(IBuddy initiator, String... userIDs);
}
