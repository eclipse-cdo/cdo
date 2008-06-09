/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOSession;

import org.eclipse.emf.internal.cdo.util.CDOPackageRegistryImpl;

import org.eclipse.net4j.signal.failover.IFailOverStrategy;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.Factory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author Eike Stepper
 */
public class CDOSessionFactory extends Factory
{
  public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.sessions";

  public static final String TYPE = "cdo";

  private static final String TRUE = Boolean.TRUE.toString();

  public CDOSessionFactory()
  {
    super(PRODUCT_GROUP, TYPE);
  }

  public CDOSession create(String description)
  {
    try
    {
      URI uri = new URI(description);
      String query = uri.getQuery();
      if (StringUtil.isEmpty(query))
      {
        throw new IllegalArgumentException("Query is empty: " + description);
      }

      Map<String, String> result = new HashMap<String, String>();
      StringTokenizer tokenizer = new StringTokenizer(query, "&");
      while (tokenizer.hasMoreTokens())
      {
        String parameter = tokenizer.nextToken();
        if (!StringUtil.isEmpty(parameter))
        {
          int pos = parameter.indexOf('=');
          if (pos == -1)
          {
            String key = parameter.trim();
            result.put(key, "");
          }
          else
          {
            String key = parameter.substring(0, pos).trim();
            String value = parameter.substring(pos + 1);
            result.put(key, value);
          }
        }
      }

      String repositoryName = result.get("repositoryName");
      boolean legacySupportEnabled = TRUE.equals(result.get("legacySupportEnabled"));
      boolean automaticPackageRegistry = TRUE.equals(result.get("automaticPackageRegistry"));
      return createSession(repositoryName, legacySupportEnabled, automaticPackageRegistry, null);
    }
    catch (URISyntaxException ex)
    {
      throw new IllegalArgumentException(ex);
    }
  }

  public static CDOSession get(IManagedContainer container, String description)
  {
    return (CDOSession)container.getElement(PRODUCT_GROUP, TYPE, description);
  }

  public static CDOSessionImpl createSession(String repositoryName, boolean legacySupportEnabled,
      boolean automaticPackageRegistry, IFailOverStrategy failOverStrategy)
  {
    CDOSessionImpl session = new CDOSessionImpl();
    if (automaticPackageRegistry)
    {
      CDOPackageRegistryImpl.SelfPopulating packageRegistry = new CDOPackageRegistryImpl.SelfPopulating();
      packageRegistry.setSession(session);
      session.setPackageRegistry(packageRegistry);
    }

    session.setRepositoryName(repositoryName);
    session.setLegacySupportEnabled(legacySupportEnabled);
    session.setFailOverStrategy(failOverStrategy);
    return session;
  }
}
