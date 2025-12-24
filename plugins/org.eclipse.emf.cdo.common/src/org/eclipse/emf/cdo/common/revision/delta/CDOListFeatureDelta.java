/*
 * Copyright (c) 2008, 2011-2013, 2019 Eike Stepper (Loehne, Germany) and others.
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

import java.util.List;

/**
 * A {@link CDOFeatureDelta feature delta} that represents any modification of the list of a many-valued
 * {@link EStructuralFeature feature}. The detailed changes to this list are returned by the {@link #getListChanges()}
 * method.
 *
 * @author Simon McDuff
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOListFeatureDelta extends CDOFeatureDelta, CDOOriginSizeProvider
{
  public List<CDOFeatureDelta> getListChanges();
}
