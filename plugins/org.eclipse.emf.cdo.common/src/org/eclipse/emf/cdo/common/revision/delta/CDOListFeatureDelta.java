/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.common.revision.delta;

import java.util.List;

/**
 * @author Simon McDuff
 */
public interface CDOListFeatureDelta extends CDOFeatureDelta
{
  public List<CDOFeatureDelta> getListChanges();
}
