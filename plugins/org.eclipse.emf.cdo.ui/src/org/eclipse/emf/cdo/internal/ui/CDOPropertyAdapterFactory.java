/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.internal.cdo.session.SessionProperties;
import org.eclipse.emf.internal.cdo.view.ViewProperties;

import org.eclipse.net4j.util.ui.AbstractPropertyAdapterFactory;
import org.eclipse.net4j.util.ui.DefaultPropertySource;

import org.eclipse.ui.views.properties.IPropertySource;

/**
 * @author Eike Stepper
 */
public class CDOPropertyAdapterFactory extends AbstractPropertyAdapterFactory
{
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

    return null;
  }
}
