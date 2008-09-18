/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.spi.common;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.revision.CDOList;

import java.util.Map;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public interface InternalCDOList extends CDOList
{
  /**
   * Adjust references according to idMappings and resynchronizes indexes.
   */
  public void adjustReferences(Map<CDOIDTemp, CDOID> idMappings);

  /**
   * Clones the list.
   */
  public InternalCDOList clone(CDOType type);
}
