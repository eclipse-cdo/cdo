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
package org.eclipse.net4j.internal.buddies.protocol;

import org.eclipse.net4j.buddies.internal.protocol.Buddy;
import org.eclipse.net4j.buddies.internal.protocol.Collaboration;
import org.eclipse.net4j.buddies.internal.protocol.CollaborationLeftIndication;
import org.eclipse.net4j.internal.buddies.Self;

/**
 * @author Eike Stepper
 */
public class ClientCollaborationLeftIndication extends CollaborationLeftIndication
{
  public ClientCollaborationLeftIndication(Self self)
  {
    super(self.getSession(), self);
  }

  @Override
  protected void collaborationLeft(Buddy buddy, Collaboration collaboration)
  {
    buddy.removeMembership(collaboration);
    collaboration.removeMembership(buddy);
    super.collaborationLeft(buddy, collaboration);
  }
}
