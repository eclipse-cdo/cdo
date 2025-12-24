/*
 * Copyright (c) 2008, 2010-2012, 2015 Eike Stepper (Loehne, Germany) and others.
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
 * A {@link CDOFeatureDelta feature delta} that represents a removal from a many-valued {@link EStructuralFeature
 * feature}.
 *
 * @author Simon McDuff
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDORemoveFeatureDelta extends CDOFeatureDelta
{
  public int getIndex();

  /**
   * @since 3.0
   */
  public Object getValue();
}
