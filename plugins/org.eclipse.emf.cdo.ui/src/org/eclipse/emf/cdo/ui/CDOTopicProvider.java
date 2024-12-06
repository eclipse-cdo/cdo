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
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionManager;
import org.eclipse.emf.cdo.session.remote.CDORemoteTopic;

import org.eclipse.net4j.util.AdapterUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.event.Notifier;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.graphics.Image;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Provides an array of {@link Topic topics} and supports the registration of {@link Listener listeners}
 * that are notified about {@link Listener#topicAdded(CDOTopicProvider, Topic) additions} to and
 * {@link Listener#topicRemoved(CDOTopicProvider, Topic) removals} from this array.
 *
 * @author Eike Stepper
 * @since 4.12
 * @see DefaultTopicProvider
 */
public interface CDOTopicProvider
{
  public Topic[] getTopics();

  public void addTopicListener(Listener listener);

  public void removeTopicListener(Listener listener);

  /**
   * @since 4.14
   */
  default public void changeTopic(Topic topic, Image newImage, String newText, String newDescription)
  {
    topic.change(newImage, newText, newDescription);
  }

  /**
   * An {@link #getId() identifiable} topic in the scope of a {@link #getSession() session}.
   * <p>
   * For displaying a topic carries an {@link #getImage() image}, a {@link #getText() text} (label),
   * and a {@link #getDescription() description}. Notifies {@link #addListener(org.eclipse.net4j.util.event.IListener) registered listeners}
   * about changes in any of these attributes with {@link TopicChangedEvent topic changed events}.
   *
   * @author Eike Stepper
   */
  public static final class Topic extends Notifier implements IAdaptable
  {
    private final Map<String, Object> properties = new HashMap<>();

    private final CDOSession session;

    private final String id;

    private Image image;

    private String text;

    private String description;

    private Function<Class<?>, Object> adapterProvider = type -> null;

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

    /**
     * Returns a modifiable map of properties.
     *
     * @since 4.14
     */
    public Map<String, Object> getProperties()
    {
      return properties;
    }

    @Override
    public <T> T getAdapter(Class<T> type)
    {
      T adapter = AdapterUtil.adapt(this, type, false);
      if (adapter != null)
      {
        return adapter;
      }

      adapter = provideAdapter(type);
      if (adapter != null)
      {
        return adapter;
      }

      if (type == CDORemoteTopic.class)
      {
        CDORemoteSessionManager remoteSessionManager = session.getRemoteSessionManager();

        @SuppressWarnings("unchecked")
        T subscribedTopic = (T)remoteSessionManager.getSubscribedTopic(id);
        return subscribedTopic;
      }

      return null;
    }

    /**
     * @since 4.14
     */
    public Topic addAdapterProvider(Function<Class<?>, Object> newAdapterProvider)
    {
      Function<Class<?>, Object> oldAdapterProvider = adapterProvider;

      adapterProvider = type -> {
        Object adapter = newAdapterProvider.apply(type);
        if (adapter != null)
        {
          return adapter;
        }

        return oldAdapterProvider.apply(type);
      };

      return this;
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

    private <T> T provideAdapter(Class<T> type)
    {
      if (adapterProvider != null)
      {
        try
        {
          @SuppressWarnings("unchecked")
          T adapter = (T)adapterProvider.apply(type);
          return adapter;
        }
        catch (ClassCastException ex)
        {
          //$FALL-THROUGH$
        }
      }

      return null;
    }

    private void change(Image newImage, String newText, String newDescription)
    {
      Image oldImage;
      String oldText;
      String oldDescription;
      boolean changed = false;

      synchronized (this)
      {
        oldImage = image;
        if (!Objects.equals(oldImage, newImage))
        {
          image = newImage;
          changed = true;
        }

        oldText = text;
        if (!Objects.equals(oldText, newText))
        {
          text = newText;
          changed = true;
        }

        oldDescription = description;
        if (!Objects.equals(oldDescription, newDescription))
        {
          description = newDescription;
          changed = true;
        }
      }

      if (changed)
      {
        fireEvent(new TopicChangedEvent(this, oldImage, newImage, oldText, newText, oldDescription, newDescription));
      }
    }

    /**
     * An {@link Event event} fired from a {@link Topic topic} when the {@link #getImage() image}, the {@link #getText() text} (label),
     * or the {@link #getDescription() description} attribute of the topic were changed.
     *
     * @author Eike Stepper
     * @since 4.14
     */
    public static final class TopicChangedEvent extends Event
    {
      private static final long serialVersionUID = 1L;

      private final Image oldImage;

      private final Image newImage;

      private final String oldText;

      private final String newText;

      private final String oldDescription;

      private final String newDescription;

      private TopicChangedEvent(Topic topic, Image oldImage, Image newImage, String oldText, String newText, String oldDescription, String newDescription)
      {
        super(topic);
        this.oldImage = oldImage;
        this.newImage = newImage;
        this.oldText = oldText;
        this.newText = newText;
        this.oldDescription = oldDescription;
        this.newDescription = newDescription;
      }

      @Override
      public Topic getSource()
      {
        return (Topic)super.getSource();
      }

      public Image getOldImage()
      {
        return oldImage;
      }

      public Image getNewImage()
      {
        return newImage;
      }

      public String getOldText()
      {
        return oldText;
      }

      public String getNewText()
      {
        return newText;
      }

      public String getOldDescription()
      {
        return oldDescription;
      }

      public String getNewDescription()
      {
        return newDescription;
      }
    }
  }

  /**
   * Notifies about {@link Listener#topicAdded(CDOTopicProvider, Topic) additions} to and
   * {@link Listener#topicRemoved(CDOTopicProvider, Topic) removals} from the array of topics of a {@link CDOTopicProvider topic provider}.
   *
   * @author Eike Stepper
   */
  public interface Listener
  {
    public void topicAdded(CDOTopicProvider provider, Topic topic);

    public void topicRemoved(CDOTopicProvider provider, Topic topic);
  }
}
