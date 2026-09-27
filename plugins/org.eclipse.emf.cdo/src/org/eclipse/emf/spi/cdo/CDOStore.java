/*
 * Copyright (c) 2011, 2012, 2014, 2016, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Eike Stepper & Simon McDuff - bug 204890
 *    Simon McDuff - bug 246705
 *    Simon McDuff - bug 246622
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.InternalEObject.EStore;

/**
 * A CDO specific version of an {@link EStore}.
 * <p>
 * CDORevisions need to follow these rules:<br>
 * - Keep CDOID only when the object (!isNew &amp;&amp; !isTransient) // Only when CDOID will not changed.<br>
 * - Keep EObject for external reference, new, transient and that until commit time.<br>
 * It is important since these objects could changed and we need to keep a reference to {@link EObject} until the end.
 * It is the reason why {@link CDOStore} always call {@link InternalCDOView#convertObjectToID(Object, boolean)} with
 * true.
 *
 * @author Eike Stepper
 * @since 4.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOStore extends EStore
{
  /**
   * Returns the number of values in a many-valued feature without acquiring the view lock.
   * <p>
   * The calling thread must already hold the owning view's access lock for the complete operation that uses this
   * result. This method exists for composite list operations that keep that lock while performing their own index
   * validation and subsequent store operation. Call {@link #size(InternalEObject, EStructuralFeature)} when no such
   * outer lock is held.
   *
   * @param eObject the object that owns the feature.
   * @param feature the many-valued feature whose size is requested.
   * @return the current number of values in the feature.
   * @see #size(InternalEObject, EStructuralFeature)
   * @since 4.31
   */
  public int sizeUnsynced(InternalEObject eObject, EStructuralFeature feature);

  /**
   * Moves a value in a many-valued feature without acquiring the view lock.
   * <p>
   * The calling thread must already hold the owning view's access lock for the complete operation. This method
   * performs the same backing-store update and returns the same former value as {@link #move(InternalEObject,
   * EStructuralFeature, int, int)}. Call the synchronized method when no such outer lock is held.
   *
   * @param eObject the object that owns the feature.
   * @param feature the many-valued feature to update.
   * @param target the index to which the value is moved.
   * @param source the index from which the value is moved.
   * @return the value formerly at {@code source}.
   * @see #move(InternalEObject, EStructuralFeature, int, int)
   * @since 4.31
   */
  public Object moveUnsynced(InternalEObject eObject, EStructuralFeature feature, int target, int source);

  /**
   * Removes a value from a many-valued feature using the same backing-store operation as
   * {@link #remove(InternalEObject, EStructuralFeature, int)}, without acquiring the owning view's access lock.
   * <p>
   * The calling thread must already hold that lock for the complete operation. Call the synchronized method when no
   * such outer lock is held.
   *
   * @param eObject the object that owns the feature.
   * @param feature the many-valued feature to update.
   * @param index the index of the value to remove.
   * @return the removed value.
   * @see #remove(InternalEObject, EStructuralFeature, int)
   * @since 4.31
   */
  public Object removeUnsynced(InternalEObject eObject, EStructuralFeature feature, int index);

  /**
   * Adds a value to a many-valued feature using the same backing-store operation as
   * {@link #add(InternalEObject, EStructuralFeature, int, Object)}, without acquiring the owning view's access lock.
   * <p>
   * The calling thread must already hold that lock for the complete operation. The synchronized {@code add(...)}
   * method remains the independently safe EStore entry point and must be used when no such outer lock is held.
   *
   * @param eObject the object that owns the feature.
   * @param feature the many-valued feature to update.
   * @param index the index at which the value is added.
   * @param value the value to add.
   * @see #add(InternalEObject, EStructuralFeature, int, Object)
   * @since 4.31
   */
  public void addUnsynced(InternalEObject eObject, EStructuralFeature feature, int index, Object value);

  /**
   * @since 2.0
   */
  public InternalCDOView getView();

  /**
   * @since 2.0
   */
  public void setContainer(InternalEObject eObject, CDOResource newResource, InternalEObject newEContainer, int newContainerFeatureID);

  /**
   * Returns the container feature ID.
   * If the container isn't a navigable feature, this will be a negative ID indicating the inverse of the containment feature's ID.
   *
   * @return the container feature ID.
   * @see EObject#eContainmentFeature()
   * @see InternalEObject#EOPPOSITE_FEATURE_BASE
   * @since 4.29
   */
  public int getContainerFeatureID(InternalEObject eObject);

  /**
   * @since 2.0
   */
  public InternalEObject getResource(InternalEObject eObject);

  /**
   * @since 2.0
   */
  public Object resolveProxy(InternalCDORevision revision, EStructuralFeature feature, int index, Object value);

  /**
   * @since 3.0
   */
  public Object convertToCDO(InternalCDOObject object, EStructuralFeature feature, Object value);

  /**
   * @since 2.0
   */
  public Object convertToEMF(EObject eObject, InternalCDORevision revision, EStructuralFeature feature, int index, Object value);

  /**
   * @deprecated As of 4.29, replaced by {@link #getContainerFeatureID(InternalEObject)}.
   */
  @Deprecated
  public default int getContainingFeatureID(InternalEObject eObject)
  {
    return getContainerFeatureID(eObject);
  }
}
