/*
 * Copyright (c) 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui;

import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOUserInfoManager;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.collection.Entity;
import org.eclipse.net4j.util.ui.chat.ChatMessage;
import org.eclipse.net4j.util.ui.chat.ChatMessage.Author;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class CDOAuthorCache
{
  private static final String PROPERTIES_KEY = CDOAuthorCache.class.getName();

  private final CDOSession session;

  private final ChatMessage.Author.Cache cache;

  public CDOAuthorCache(CDOSession session)
  {
    this.session = session;
    cache = new ChatMessage.Author.Cache(this::loadAuthors);
  }

  public final CDOSession getSession()
  {
    return session;
  }

  public Author getAuthor(String userID)
  {
    return cache.getAuthor(userID);
  }

  public Map<String, Author> getAuthors(Iterable<String> userIDs)
  {
    return cache.getAuthors(userIDs);
  }

  private Map<String, ChatMessage.Author> loadAuthors(Iterable<String> userIDs)
  {
    Map<String, ChatMessage.Author> authors = new HashMap<>();

    CDOUserInfoManager userInfoManager = session.getUserInfoManager();
    Map<String, Entity> userInfos = userInfoManager.getUserInfos(userIDs);

    for (String userID : userIDs)
    {
      ChatMessage.Author.Builder builder = ChatMessage.Author.builder(userID);

      Entity entity = userInfos.get(userID);
      if (entity != null)
      {
        builder.firstName(entity.property("givenName"));
        builder.lastName(entity.property("sn"));

        String fullName = entity.property("cn");
        if (!StringUtil.isEmpty(fullName))
        {
          builder.fullName(fullName);
        }

        String initials = entity.property("Initials");
        if (!StringUtil.isEmpty(initials))
        {
          builder.initials(initials);
        }

        String avatarUri = entity.property("avatarUri");
        if (!StringUtil.isEmpty(avatarUri))
        {
          try
          {
            builder.avatar(URI.create(avatarUri));
          }
          catch (Exception ex)
          {
            OM.LOG.warn("Malformed URI: " + avatarUri, ex);
          }
        }
        else if (StringUtil.isTrue(entity.property("gravatar")))
        {
          builder.gravatar(entity.property("mail"));
        }
      }

      authors.put(userID, builder.build());
    }

    return authors;
  }

  public static CDOAuthorCache of(CDOSession session)
  {
    return (CDOAuthorCache)session.properties().computeIfAbsent(PROPERTIES_KEY, k -> new CDOAuthorCache(session));
  }
}
