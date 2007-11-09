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

import org.eclipse.net4j.buddies.protocol.IBuddy;
import org.eclipse.net4j.buddies.protocol.ICollaboration;
import org.eclipse.net4j.buddies.protocol.IFacility;

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
