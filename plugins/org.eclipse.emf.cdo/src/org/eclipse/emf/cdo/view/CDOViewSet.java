/*
 * Copyright (c) 2009, 2011, 2012, 2014, 2015 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.eresource.CDOResourceFactory;
import org.eclipse.emf.cdo.session.CDOSession;

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
 * @apiviz.landmark
 * @apiviz.composedOf {@link CDOView} - - views
 * @apiviz.has {@link org.eclipse.emf.ecore.EPackage.Registry}
 * @apiviz.has {@link org.eclipse.emf.ecore.resource.ResourceSet}
 * @apiviz.has {@link org.eclipse.emf.cdo.eresource.CDOResourceFactory}
 */
public interface CDOViewSet extends Notifier
{
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

  /**
   * @since 4.7
   */
  public CDOSession[] getSessions();

  public CDOResourceFactory getResourceFactory();

  public EPackage.Registry getPackageRegistry();

  public ResourceSet getResourceSet();
}
