/*
 * Copyright (c) 2011-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.properties;

import org.eclipse.emf.cdo.explorer.CDORepository;
import org.eclipse.emf.cdo.internal.explorer.properties.RepositoryProperties;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.net4j.util.ui.AbstractPropertyAdapterFactory;
import org.eclipse.net4j.util.ui.DefaultActionFilter;
import org.eclipse.net4j.util.ui.DefaultPropertySource;

import org.eclipse.ui.IActionFilter;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * @author Eike Stepper
 */
public class ExplorerPropertiesAdapterFactory extends AbstractPropertyAdapterFactory
{
  private static final IActionFilter REPOSITORY_ACTION_FILTER = new DefaultActionFilter<CDORepository>(
      RepositoryProperties.INSTANCE);

  public ExplorerPropertiesAdapterFactory()
  {
  }

  @Override
  protected IPropertySource createPropertySource(Object object)
  {
    if (object instanceof CDORepository)
    {
      return new DefaultPropertySource<CDORepository>((CDORepository)object, RepositoryProperties.INSTANCE);
    }

    return null;
  }

  @Override
  protected IActionFilter createActionFilter(Object object)
  {
    if (object instanceof CDOSession)
    {
      return REPOSITORY_ACTION_FILTER;
    }

    return super.createActionFilter(object);
  }
}
