/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.server;

import org.eclipse.net4j.buddies.common.IAccount;
import org.eclipse.net4j.buddies.common.IBuddy;
import org.eclipse.net4j.buddies.common.IBuddyProvider;
import org.eclipse.net4j.buddies.common.ICollaboration;
import org.eclipse.net4j.buddies.common.ICollaborationContainer;
import org.eclipse.net4j.buddies.common.ISession;
import org.eclipse.net4j.buddies.common.ISessionProvider;
import org.eclipse.net4j.buddies.internal.server.BuddyAdmin;
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
