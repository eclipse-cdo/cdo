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

import org.eclipse.emf.ecore.EStructuralFeature;

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

  public Object get(EStructuralFeature feature, int index);

  public int size(EStructuralFeature feature);

  public boolean isEmpty(EStructuralFeature feature);

  public boolean contains(EStructuralFeature feature, Object value);

  public int indexOf(EStructuralFeature feature, Object value);

  public int lastIndexOf(EStructuralFeature feature, Object value);

  public <T> T[] toArray(EStructuralFeature feature, T[] array);

  public Object[] toArray(EStructuralFeature feature);

  public int hashCode(EStructuralFeature feature);
}
