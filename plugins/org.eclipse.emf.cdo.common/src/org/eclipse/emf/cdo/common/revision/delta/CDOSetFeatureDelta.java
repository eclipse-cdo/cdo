/*
 * Copyright (c) 2008, 2010-2012, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.common.revision.delta;

import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * A {@link CDOFeatureDelta feature delta} that represents a value change of a single-valued {@link EStructuralFeature
 * feature}.
 *
 * @author Simon McDuff
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOSetFeatureDelta extends CDOFeatureDelta
{
  /**
   * @deprecated As of 4.6 use {@link CDOFeatureDelta#UNKNOWN_VALUE}.
   * @since 4.0
   */
  @Deprecated
  public static final Object UNSPECIFIED = UNKNOWN_VALUE;

  public int getIndex();

  public Object getValue();

  /**
   * @since 4.0
   */
  public Object getOldValue();
}
