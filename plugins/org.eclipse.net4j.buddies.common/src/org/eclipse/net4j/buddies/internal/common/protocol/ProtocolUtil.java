/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.internal.common.protocol;

import org.eclipse.net4j.buddies.common.IAccount;
import org.eclipse.net4j.buddies.common.IBuddy;
import org.eclipse.net4j.buddies.common.IBuddyContainer;
import org.eclipse.net4j.buddies.common.IMessage;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public final class ProtocolUtil
{
  private ProtocolUtil()
  {
  }

  public static void writeBuddies(ExtendedDataOutputStream out, Collection<IBuddy> buddies) throws IOException
  {
    if (buddies == null)
    {
      out.writeInt(0);
    }
    else
    {
      out.writeInt(buddies.size());
      for (IBuddy buddy : buddies)
      {
        out.writeString(buddy.getUserID());
      }
    }
  }

  public static Set<IBuddy> readBuddies(ExtendedDataInputStream in, IBuddyContainer buddyContainer) throws IOException
  {
    int size = in.readInt();
    Set<IBuddy> buddies = new HashSet<>();
    for (int i = 0; i < size; i++)
    {
      String userID = in.readString();
      IBuddy buddy = buddyContainer.getBuddy(userID);
      if (buddy != null)
      {
        buddies.add(buddy);
      }
    }

    return buddies;
  }

  public static String[] readUserIDs(ExtendedDataInputStream in) throws IOException
  {
    int size = in.readInt();
    String[] userIDs = new String[size];
    for (int i = 0; i < size; i++)
    {
      userIDs[i] = in.readString();
    }

    return userIDs;
  }

  public static void writeAccount(ExtendedDataOutputStream out, IAccount account) throws IOException
  {
    if (account != null)
    {
      out.writeBoolean(true);
      ObjectOutputStream oos = new ObjectOutputStream(out);
      oos.writeObject(account);
    }
    else
    {
      out.writeBoolean(false);
    }
  }

  public static IAccount readAccount(ExtendedDataInputStream in) throws IOException
  {
    boolean exists = in.readBoolean();
    if (!exists)
    {
      return null;
    }

    try
    {
      ObjectInputStream ois = new ObjectInputStream(in);
      return (IAccount)ois.readObject();
    }
    catch (IOException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public static void writeState(ExtendedDataOutputStream out, IBuddy.State state) throws IOException
  {
    switch (state)
    {
    case AVAILABLE:
      out.writeByte(ProtocolConstants.STATE_AVAILABLE);
      break;

    case LONESOME:
      out.writeByte(ProtocolConstants.STATE_LONESOME);
      break;

    case AWAY:
      out.writeByte(ProtocolConstants.STATE_AWAY);
      break;

    case DO_NOT_DISTURB:
      out.writeByte(ProtocolConstants.STATE_DO_NOT_DISTURB);
      break;

    default:
      throw new IllegalArgumentException("Illegal state: " + state); //$NON-NLS-1$
    }
  }

  public static IBuddy.State readState(ExtendedDataInputStream in) throws IOException
  {
    byte state = in.readByte();
    switch (state)
    {
    case ProtocolConstants.STATE_AVAILABLE:
      return IBuddy.State.AVAILABLE;

    case ProtocolConstants.STATE_LONESOME:
      return IBuddy.State.LONESOME;

    case ProtocolConstants.STATE_AWAY:
      return IBuddy.State.AWAY;

    case ProtocolConstants.STATE_DO_NOT_DISTURB:
      return IBuddy.State.DO_NOT_DISTURB;

    default:
      throw new IllegalArgumentException("Illegal state: " + state); //$NON-NLS-1$
    }
  }

  public static void writeFacilityTypes(ExtendedDataOutputStream out, String[] facilityTypes) throws IOException
  {
    if (facilityTypes == null)
    {
      out.writeInt(0);
    }
    else
    {
      out.writeInt(facilityTypes.length);
      for (String facilityType : facilityTypes)
      {
        out.writeString(facilityType);
      }
    }
  }

  public static String[] readFacilityTypes(ExtendedDataInputStream in) throws IOException
  {
    int size = in.readInt();
    String[] facilityTypes = new String[size];
    for (int i = 0; i < size; i++)
    {
      facilityTypes[i] = in.readString();
    }

    return facilityTypes;
  }

  public static void writeMessage(ExtendedDataOutputStream out, IMessage message) throws IOException
  {
    ObjectOutputStream oos = new ObjectOutputStream(out);
    oos.writeObject(message);
  }

  public static IMessage readMessage(ExtendedDataInputStream in, final ClassLoader classLoader) throws IOException
  {
    try
    {
      ObjectInputStream ois = new ObjectInputStream(in)
      {
        @Override
        protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException
        {
          String className = desc.getName();
          return classLoader.loadClass(className);
        }
      };

      return (IMessage)ois.readObject();
    }
    catch (IOException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }
}
