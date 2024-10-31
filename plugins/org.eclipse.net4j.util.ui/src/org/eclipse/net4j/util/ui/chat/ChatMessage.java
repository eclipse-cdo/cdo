/*
 * Copyright (c) 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.chat;

import java.io.Serializable;
import java.net.URI;
import java.util.Objects;

/**
 * @author Eike Stepper
 * @since 3.19
 */
public interface ChatMessage extends Comparable<ChatMessage>
{
  public Object getID();

  public Author getAuthor();

  public long getCreationTime();

  public long getEditTime();

  public String getContent();

  public ChatMessage getReplyTo();

  @Override
  public default int compareTo(ChatMessage o)
  {
    return Long.compare(getCreationTime(), o.getCreationTime());
  }

  /**
   * @author Eike Stepper
   */
  public static final class Author implements Serializable, Comparable<Author>
  {
    private static final long serialVersionUID = 1L;

    private final String userID;

    private final String fullName;

    private final String shortName;

    // https://www.gravatar.com/avatar/
    private final URI avatar;

    public Author(String userID, String fullName, String shortName, URI avatar)
    {
      this.userID = Objects.requireNonNull(userID);
      this.fullName = fullName;
      this.shortName = shortName;
      this.avatar = avatar;
    }

    public String getUserID()
    {
      return userID;
    }

    public String getFullName()
    {
      return fullName;
    }

    public String getShortName()
    {
      return shortName;
    }

    public URI getAvatar()
    {
      return avatar;
    }

    @Override
    public int compareTo(Author o)
    {
      return userID.compareTo(o.userID);
    }

    @Override
    public int hashCode()
    {
      return Objects.hash(userID);
    }

    @Override
    public boolean equals(Object obj)
    {
      if (this == obj)
      {
        return true;
      }

      if (obj == null)
      {
        return false;
      }

      if (getClass() != obj.getClass())
      {
        return false;
      }

      Author other = (Author)obj;
      return Objects.equals(userID, other.userID);
    }

    @Override
    public String toString()
    {
      return "Author[" + userID + "]";
    }
  }

  /**
   * @author Eike Stepper
   */
  @FunctionalInterface
  public interface Provider
  {
    public ChatMessage[] getMessages();
  }
}
