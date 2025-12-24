/*
 * Copyright (c) 2015, 2016, 2019, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.properties;

import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.internal.explorer.checkouts.CDOCheckoutProperties;
import org.eclipse.emf.cdo.internal.explorer.repositories.CDORepositoryProperties;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.session.SessionProperties;
import org.eclipse.emf.internal.cdo.view.ViewProperties;

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
  private static final IActionFilter REPOSITORY_ACTION_FILTER = new DefaultActionFilter<>(CDORepositoryProperties.INSTANCE);

  private static final IActionFilter CHECKOUT_ACTION_FILTER = new DefaultActionFilter<>(CDOCheckoutProperties.INSTANCE);

  public ExplorerPropertiesAdapterFactory()
  {
  }

  @Override
  protected IPropertySource createPropertySource(Object object)
  {
    if (object instanceof CDORepository)
    {
      CDORepository repository = (CDORepository)object;
      CDOSession session = repository.getSession();

      return new DefaultPropertySource.Augmented<CDORepository, CDOSession>(repository, CDORepositoryProperties.INSTANCE, session)
      {
        @Override
        protected IPropertySource createAugmentingPropertySource(CDOSession session)
        {
          return new DefaultPropertySource<>(session, SessionProperties.INSTANCE);
        }
      }.extendDescriptors();
    }

    if (object instanceof CDOCheckout)
    {
      CDOCheckout checkout = (CDOCheckout)object;
      CDOView view = checkout.getView();

      return new DefaultPropertySource.Augmented<CDOCheckout, CDOView>(checkout, CDOCheckoutProperties.INSTANCE, view)
      {
        @Override
        protected IPropertySource createAugmentingPropertySource(CDOView view)
        {
          return new DefaultPropertySource<>(view, ViewProperties.INSTANCE);
        }
      }.extendDescriptors();
    }

    return null;
  }

  @Override
  protected IActionFilter createActionFilter(Object object)
  {
    if (object instanceof CDORepository)
    {
      return REPOSITORY_ACTION_FILTER;
    }

    if (object instanceof CDOCheckout)
    {
      return CHECKOUT_ACTION_FILTER;
    }

    return super.createActionFilter(object);
  }
}
