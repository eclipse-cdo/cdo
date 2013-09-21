/*
 * Copyright (c) 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui;

import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.object.ObjectProperties;
import org.eclipse.emf.internal.cdo.session.SessionProperties;
import org.eclipse.emf.internal.cdo.view.ViewProperties;

import org.eclipse.net4j.util.ui.AbstractPropertyAdapterFactory;
import org.eclipse.net4j.util.ui.DefaultActionFilter;
import org.eclipse.net4j.util.ui.DefaultPropertySource;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.ui.IActionFilter;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * @author Eike Stepper
 */
public class CDOPropertyAdapterFactory extends AbstractPropertyAdapterFactory
{
  private static final IActionFilter SESSION_ACTION_FILTER = new DefaultActionFilter<CDOSession>(
      SessionProperties.INSTANCE);

  private static final IActionFilter VIEW_ACTION_FILTER = new DefaultActionFilter<CDOView>(ViewProperties.INSTANCE);

  private static final IActionFilter OBJECT_ACTION_FILTER = new DefaultActionFilter<EObject>(ObjectProperties.INSTANCE);

  public CDOPropertyAdapterFactory()
  {
  }

  @Override
  protected IPropertySource createPropertySource(Object object)
  {
    if (object instanceof CDOSession)
    {
      return new DefaultPropertySource<CDOSession>((CDOSession)object, SessionProperties.INSTANCE);
    }

    if (object instanceof CDOView)
    {
      return new DefaultPropertySource<CDOView>((CDOView)object, ViewProperties.INSTANCE);
    }

    if (object instanceof EObject)
    {
      return new DefaultPropertySource<EObject>((EObject)object, ObjectProperties.INSTANCE);
    }

    return null;
  }

  @Override
  protected IActionFilter createActionFilter(Object object)
  {
    if (object instanceof CDOSession)
    {
      return SESSION_ACTION_FILTER;
    }

    if (object instanceof CDOView)
    {
      return VIEW_ACTION_FILTER;
    }

    if (object instanceof EObject)
    {
      return OBJECT_ACTION_FILTER;
    }

    return super.createActionFilter(object);
  }
}
