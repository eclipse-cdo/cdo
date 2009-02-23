/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.view;

import org.eclipse.net4j.util.container.IContainer;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * A global registry of {@link CDOViewProvider view provider} implementations.
 * 
 * @author Victor Roldan Betancort
 * @since 2.0
 */
public interface CDOViewProviderRegistry extends IContainer<CDOViewProvider>
{
  public static final CDOViewProviderRegistry INSTANCE = org.eclipse.emf.internal.cdo.view.CDOViewProviderRegistryImpl.INSTANCE;

  /**
   * Returns a {@link CDOView view} that serves the given URI in the given {@link CDOViewSet view set}, or
   * <code>null</code> if no {@link CDOViewProvider view provider} in this registry can provide such a view
   */
  public CDOView provideView(URI uri, ResourceSet viewSet);

  /**
   * Returns an array of <code>CDOViewProvider</code> instances, determined and ordered by certain criteria based on the
   * argument URI.
   */
  public CDOViewProvider[] getViewProviders(URI uri);

  /**
   * Registers a new <code>CDOViewProvider</code> instance
   */
  public void addViewProvider(CDOViewProvider viewProvider);

  /**
   * Removes certain <code>CDOViewProvider</code> instance from the registry
   */
  public void removeViewProvider(CDOViewProvider viewProvider);
}
