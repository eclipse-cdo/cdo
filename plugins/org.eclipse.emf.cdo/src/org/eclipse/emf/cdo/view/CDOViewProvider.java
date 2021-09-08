/*
 * Copyright (c) 2009-2012, 2015, 2020, 2021 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * Capable of opening a <code>CDOView</code> on a target repository, defined by a URI. A regular expression and the
 * priority are used to determine the most suitable provider.
 *
 * @since 2.0
 * @author Victor Roldan Betancort
 */
public interface CDOViewProvider
{
  public static final int DEFAULT_PRIORITY = 500;

  /**
   * Returns the priority of this provider. Usually used to choose between several <code>CDOViewProviders</code> that
   * match the same repository URI. A higher priority value is preferred in that decision.
   */
  public int getPriority();

  /**
   * Returns the regular expression that determines if the provider can handle certain URI
   */
  public String getRegex();

  /**
   * Checks if the URI matches with the regular expression of this provider
   */
  public boolean matchesRegex(URI uri);

  /**
   * Receives a URI and returns an opened <code>CDOView</code> against the repository.
   */
  public CDOView getView(URI uri, ResourceSet resourceSet);

  /**
   * @since 4.0
   */
  public URI getResourceURI(CDOView view, String path);

  /**
   * A {@link CDOViewProvider view provider} with advanced {@link #getViewURI(CDOView) view URI}
   * and {@link #getPath(URI) path} information methods.
   *
   * @author Eike Stepper
   * @since 4.4
   * @see AbstractCDOViewProvider
   */
  public interface CDOViewProvider2 extends CDOViewProvider
  {
    public URI getViewURI(CDOView view);

    public URI getViewURI(URI uri);

    public String getPath(URI uri);
  }
}
