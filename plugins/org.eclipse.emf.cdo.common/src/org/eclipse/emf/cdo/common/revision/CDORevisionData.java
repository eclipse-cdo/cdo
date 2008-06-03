/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.common.revision;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOFeature;

/**
 * @author Eike Stepper
 */
public interface CDORevisionData
{
  public CDORevision getRevision();

  public CDOID getResourceID();

  public CDOID getContainerID();

  public int getContainingFeatureID();

  public Object get(CDOFeature feature, int index);

  public int size(CDOFeature feature);

  public boolean isEmpty(CDOFeature feature);

  public boolean contains(CDOFeature feature, Object value);

  public int indexOf(CDOFeature feature, Object value);

  public int lastIndexOf(CDOFeature feature, Object value);

  public boolean isSet(CDOFeature feature);

  public <T> T[] toArray(CDOFeature feature, T[] array);

  public Object[] toArray(CDOFeature feature);

  public int hashCode(CDOFeature feature);
}
