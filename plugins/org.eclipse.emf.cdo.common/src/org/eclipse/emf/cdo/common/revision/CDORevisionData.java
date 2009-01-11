/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.revision;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOFeature;

/**
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDORevisionData
{
  /**
   * @since 2.0
   */
  public CDORevision revision();

  public CDOID getResourceID();

  /**
   * @since 2.0
   */
  public Object getContainerID();

  public int getContainingFeatureID();

  public Object get(CDOFeature feature, int index);

  public int size(CDOFeature feature);

  public boolean isEmpty(CDOFeature feature);

  public boolean contains(CDOFeature feature, Object value);

  public int indexOf(CDOFeature feature, Object value);

  public int lastIndexOf(CDOFeature feature, Object value);

  public <T> T[] toArray(CDOFeature feature, T[] array);

  public Object[] toArray(CDOFeature feature);

  public int hashCode(CDOFeature feature);
}
