/*
 * Copyright (c) 2009-2012, 2019, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.common.revision;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;

import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.Map;

/**
 * Reference-adjustment policy backed by the ID mapping returned from a commit. It replaces temporary object IDs with
 * their repository-assigned IDs and can optionally retain temporary IDs that have no mapping.
 *
 * @author Simon McDuff
 * @since 2.0
 */
public class CDOIDMapper implements CDOReferenceAdjuster
{
  private Map<CDOID, CDOID> idMappings;

  private boolean allowUnmappedTempIDs;

  public CDOIDMapper(Map<CDOID, CDOID> idMappings)
  {
    this.idMappings = idMappings;
  }

  public Map<CDOID, CDOID> getIDMappings()
  {
    return idMappings;
  }

  /**
   * @since 3.0
   */
  public boolean isAllowUnmappedTempIDs()
  {
    return allowUnmappedTempIDs;
  }

  /**
   * @since 3.0
   */
  public void setAllowUnmappedTempIDs(boolean allowUnmappedTempIDs)
  {
    this.allowUnmappedTempIDs = allowUnmappedTempIDs;
  }

  /**
   * @since 4.0
   */
  @Override
  public Object adjustReference(Object value, EStructuralFeature feature, int index)
  {
    return CDORevisionUtil.remapID(value, idMappings, allowUnmappedTempIDs);
  }
}
