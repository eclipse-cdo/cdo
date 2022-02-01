/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui;

import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.net4j.util.StringUtil;

import org.eclipse.swt.graphics.Image;

import java.util.Objects;

/**
 * @author Eike Stepper
 * @since 4.12
 */
public interface CDOTopicProvider
{
  public Topic[] getTopics();

  public void addTopicListener(Listener listener);

  public void removeTopicListener(Listener listener);

  /**
   * @author Eike Stepper
   */
  public static final class Topic
  {
    private final CDOSession session;

    private final String id;

    private final Image image;

    private final String text;

    private final String description;

    public Topic(CDOSession session, String id, Image image, String text, String description)
    {
      this.session = session;
      this.id = id;
      this.image = image;
      this.text = text;
      this.description = description;
    }

    public CDOSession getSession()
    {
      return session;
    }

    public String getId()
    {
      return id;
    }

    public Image getImage()
    {
      return image;
    }

    public String getText()
    {
      return text;
    }

    public String getDescription()
    {
      return description;
    }

    @Override
    public int hashCode()
    {
      return Objects.hash(session, id);
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

      Topic other = (Topic)obj;
      return Objects.equals(session, other.session) && Objects.equals(id, other.id);
    }

    @Override
    public String toString()
    {
      String string = session.getRepositoryInfo().getName() + "/" + id;

      String userID = session.getUserID();
      if (!StringUtil.isEmpty(userID))
      {
        string = userID + "@" + string;
      }

      return string;
    }
  }

  /**
   * @author Eike Stepper
   */
  public interface Listener
  {
    public void topicAdded(CDOTopicProvider provider, Topic topic);

    public void topicRemoved(CDOTopicProvider provider, Topic topic);
  }
}
