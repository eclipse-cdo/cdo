/*
 * Copyright (c) 2009, 2011, 2012, 2014, 2015, 2018-2021, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.view;

import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.eresource.CDOResourceFactory;

import org.eclipse.emf.internal.cdo.view.CDOViewSetImpl;

import org.eclipse.net4j.util.container.IContainer;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * A {@link ResourceSet} adapter to associate a set of {@link CDOView} instances.
 * <p>
 * <b>Note:</b> A view set must have exactly one resource set associated. A resource set can have only one view set
 * associated.
 *
 * @author Simon McDuff
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOViewSet extends Notifier, IContainer<CDOView>
{
  /**
   * @since 4.24
   */
  public static final IContainer<CDOViewSet> REGISTRY = CDOViewSetImpl.REGISTRY;

  /**
   * @since 4.24
   */
  public int getID();

  /**
   * @deprecated As of 4.4 use {@link #resolveView(URI)}.
   */
  @Deprecated
  public CDOView resolveView(String repositoryUUID);

  /**
   * @since 4.4
   */
  public CDOView resolveView(URI viewURI);

  public CDOView[] getViews();

  public CDOResourceFactory getResourceFactory();

  public EPackage.Registry getPackageRegistry();

  public ResourceSet getResourceSet();

  /**
   * @since 4.12
   */
  public CDOAdapterPolicy getDefaultClearAdapterPolicy();

  /**
   * @since 4.12
   */
  public void setDefaultClearAdapterPolicy(CDOAdapterPolicy defaultClearAdapterPolicy);

  /**
   * @since 4.24
   */
  public static CDOViewSet[] getViewSets()
  {
    return CDOViewSetImpl.REGISTRY.getElements();
  }

  /**
   * An unchecked {@link CDOException exception} being thrown from {@link CDOViewSet view sets}.
   *
   * @author Eike Stepper
   * @since 4.12
   */
  public static class CDOViewSetException extends CDOException
  {
    private static final long serialVersionUID = 1L;

    public CDOViewSetException()
    {
    }

    public CDOViewSetException(String message, Throwable cause)
    {
      super(message, cause);
    }

    public CDOViewSetException(String message)
    {
      super(message);
    }

    public CDOViewSetException(Throwable cause)
    {
      super(cause);
    }
  }
}
