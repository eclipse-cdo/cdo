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
package org.eclipse.net4j.buddies.server;

import org.eclipse.net4j.buddies.internal.protocol.BuddyContainer;

/**
 * @author Eike Stepper
 */
public class Collaboration extends BuddyContainer implements ICollaboration
{
  public Collaboration()
  {
  }

  public int getID()
  {
    return 0;
  }

  public String getType()
  {
    return null;
  }

  public boolean isPublic()
  {
    return false;
  }
}
