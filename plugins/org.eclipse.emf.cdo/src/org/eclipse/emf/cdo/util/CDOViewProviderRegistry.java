/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.util;

import org.eclipse.net4j.util.container.IContainer;

import org.eclipse.emf.common.util.URI;

/**
 * A registry of CDOViewProvider implementations.
 * 
 * @author Victor Roldan Betancort
 * @since 2.0
 */
public interface CDOViewProviderRegistry extends IContainer<CDOViewProvider>
{
  public static final CDOViewProviderRegistry INSTANCE = org.eclipse.emf.internal.cdo.util.CDOViewProviderRegistryImpl.INSTANCE;

  /**
   * Returns a <code>CDOViewProvider</code> instance, determined by certain criteria based on the argument URI.
   */
  public CDOViewProvider getViewProvider(URI uri);

  /**
   * Registers a new <code>CDOViewProvider</code> instance
   */
  public void addViewProvider(CDOViewProvider viewProvider);

  /**
   * Removes certain <code>CDOViewProvider</code> instance from the registry
   */
  public void removeViewProvider(CDOViewProvider viewProvider);
}
