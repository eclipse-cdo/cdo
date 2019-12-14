/*
 * Copyright (c) 2007-2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.internal.ui.dnd;

import org.eclipse.net4j.buddies.IBuddySession;
import org.eclipse.net4j.buddies.common.IBuddy;
import org.eclipse.net4j.buddies.internal.ui.bundle.OM;
import org.eclipse.net4j.buddies.internal.ui.messages.Messages;
import org.eclipse.net4j.internal.buddies.SessionManager;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.ui.dnd.DNDTransfer;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class BuddiesTransfer extends DNDTransfer<IBuddy[]>
{
  public static final String TYPE_NAME = "buddies-transfer-format"; //$NON-NLS-1$

  public static final BuddiesTransfer INSTANCE = new BuddiesTransfer();

  public BuddiesTransfer()
  {
    super(TYPE_NAME);
  }

  @Override
  protected void writeObject(ExtendedDataOutputStream out, IBuddy[] buddies) throws IOException
  {
    out.writeInt(buddies.length);
    for (IBuddy buddy : buddies)
    {
      out.writeString(buddy.getUserID());
    }
  }

  @Override
  protected IBuddy[] readObject(ExtendedDataInputStream in) throws IOException
  {
    IBuddySession session = SessionManager.INSTANCE.getSession();
    if (session == null)
    {
      OM.LOG.warn(Messages.getString("BuddiesTransfer_1")); //$NON-NLS-1$
      return null;
    }

    List<IBuddy> buddies = new ArrayList<>();
    int size = in.readInt();
    for (int i = 0; i < size; i++)
    {
      String userID = in.readString();
      IBuddy buddy = session.getBuddy(userID);
      if (buddy != null)
      {
        buddies.add(buddy);
      }
      else
      {
        OM.LOG.warn(MessageFormat.format(Messages.getString("BuddiesTransfer_2"), userID)); //$NON-NLS-1$
      }
    }

    return buddies.toArray(new IBuddy[buddies.size()]);
  }
}
