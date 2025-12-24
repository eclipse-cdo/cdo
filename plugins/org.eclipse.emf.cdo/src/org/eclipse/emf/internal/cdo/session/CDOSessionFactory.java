/*
 * Copyright (c) 2009-2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.internal.cdo.session;

import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.util.CDOURIUtil;

import org.eclipse.emf.internal.cdo.messages.Messages;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.factory.Factory;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class CDOSessionFactory extends Factory
{
  public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.sessions"; //$NON-NLS-1$

  public CDOSessionFactory(String type)
  {
    super(PRODUCT_GROUP, type);
  }

  @Override
  public CDOSession create(String description)
  {
    try
    {
      URI uri = new URI(description);
      String query = uri.getQuery();
      if (StringUtil.isEmpty(query))
      {
        throw new IllegalArgumentException(MessageFormat.format(Messages.getString("CDOSessionFactory.1"), description)); //$NON-NLS-1$
      }

      Map<String, String> parameters = CDOURIUtil.getParameters(query);
      return createSession(uri, parameters);
    }
    catch (URISyntaxException ex)
    {
      throw new IllegalArgumentException(ex);
    }
  }

  /**
   * @since 2.0
   * @deprecated As of 4.2 implement {@link #createSession(URI, Map)}.
   */
  @Deprecated
  protected InternalCDOSession createSession(String repositoryName, boolean automaticPackageRegistry)
  {
    throw new UnsupportedOperationException();
  }

  protected abstract InternalCDOSession createSession(URI uri, Map<String, String> parameters);
}
