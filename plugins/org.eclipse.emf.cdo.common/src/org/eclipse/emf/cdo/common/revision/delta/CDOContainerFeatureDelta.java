/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 *    Simon McDuff - bug 213402
 */
package org.eclipse.emf.cdo.common.revision.delta;

import org.eclipse.emf.cdo.common.id.CDOID;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

/**
 * A {@link CDOFeatureDelta feature delta} that represents a modification of the {@link EObject#eContainer() eContainer}
 * feature.
 *
 * @author Simon McDuff
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOContainerFeatureDelta extends CDOFeatureDelta
{
  /**
   * @since 2.0
   */
  public static final EReference CONTAINER_FEATURE = new org.eclipse.emf.cdo.internal.common.revision.delta.CDOContainerFeatureDeltaImpl.ContainerFeature();

  /**
   * @since 2.0
   */
  public CDOID getResourceID();

  /**
   * @since 2.0
   */
  public Object getContainerID();

  public int getContainerFeatureID();
}
