/*
 * Copyright (c) 2007-2009, 2011, 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies;

import org.eclipse.net4j.buddies.common.IBuddy;
import org.eclipse.net4j.buddies.common.ICollaboration;
import org.eclipse.net4j.buddies.common.IFacility;

/**
 * @author Eike Stepper
 */
public interface IBuddyCollaboration extends ICollaboration
{
  public IBuddySession getSession();

  public IFacility installFacility(String type);

  public IBuddy[] invite(String... userIDs);

  public void invite(IBuddy... buddies);

  public void leave();
}
