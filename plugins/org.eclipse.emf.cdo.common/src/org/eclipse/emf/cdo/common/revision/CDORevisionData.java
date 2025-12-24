/*
 * Copyright (c) 2008-2013, 2016, 2019, 2020, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.revision;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Internal.DynamicValueHolder;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.BasicEObjectImpl;
import org.eclipse.emf.ecore.util.DelegatingEcoreEList;

/**
 * Encapsulates the modeled information and the EMF system values of a {@link CDORevision revision}.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDORevisionData
{
  /**
   * An object used to represent being set to <code>null</code>
   * as opposed to having no value and hence being in the default state.
   * <p>
   * The equivalent of <code>DynamicValueHolder.NIL</code> (i.e. explicit <code>null</code>).
   *
   * @since 3.0
   */
  public static final Object NIL = DynamicValueHolder.NIL;

  /**
   * @since 2.0
   */
  public CDORevision revision();

  public CDOID getResourceID();

  /**
   * @since 2.0
   */
  public Object getContainerID();

  /**
   * Returns the container feature ID.
   * If the container isn't a navigable feature, this will be a negative ID indicating the inverse of the containment feature's ID.
   * <p>
   * Provides the input to the calculation of the feature in the container revision that actually holds this revision.
   * <p>
   * <b>Usage Example:</b>
   * <p>
   * <pre>
   * CDORevision revision = ...;
   * CDORevision container = <i>Util.getRevision</i>(revision.data().getContainerID());
   *
   * int containerFeatureID = revision.data().getContainerFeatureID();
   *
   * EStructuralFeature feature = containerFeatureID &lt;= InternalEObject.EOPPOSITE_FEATURE_BASE ?
   *     container.getEClass().getEStructuralFeature(InternalEObject.EOPPOSITE_FEATURE_BASE - containerFeatureID) :
   *     ((EReference)revision.getEClass().getEStructuralFeature(containerFeatureID)).getEOpposite();
   * </pre>
   *
   * @return the container feature ID.
   * @see EObject#eContainmentFeature()
   * @see BasicEObjectImpl#eContainingFeature()
   * @see DelegatingEcoreEList#inverseAdd(Object, NotificationChain)
   * @see InternalEObject#EOPPOSITE_FEATURE_BASE
   * @see #calculateContainingReference(int, EClass, EClass)
   * @see #getContainerID()
   * @since 4.27
   */
  public int getContainerFeatureID();

  /**
   * @see #getContainerFeatureID()
   * @see DelegatingEcoreEList#inverseAdd(Object, NotificationChain)
   * @since 4.26
   */
  public default EReference getContainingReference(EClass containerClass)
  {
    int containerFeatureID = getContainerFeatureID();
    EClass eClass = revision().getEClass();
    return calculateContainingReference(containerFeatureID, eClass, containerClass);
  }

  /**
   * Should never return {@link InternalCDORevision#NIL}
   * @since 4.27
   */
  public Object getValue(EStructuralFeature feature);

  /**
   * @since 4.27
   */
  public CDOList getListOrNull(EStructuralFeature feature);

  /**
   * @since 2.0
   */
  public Object get(EStructuralFeature feature, int index);

  /**
   * @since 2.0
   */
  public int size(EStructuralFeature feature);

  /**
   * @since 2.0
   */
  public boolean isEmpty(EStructuralFeature feature);

  /**
   * @since 2.0
   */
  public boolean contains(EStructuralFeature feature, Object value);

  /**
   * @since 2.0
   */
  public int indexOf(EStructuralFeature feature, Object value);

  /**
   * @since 2.0
   */
  public int lastIndexOf(EStructuralFeature feature, Object value);

  /**
   * @since 2.0
   */
  public <T> T[] toArray(EStructuralFeature feature, T[] array);

  /**
   * @since 2.0
   */
  public Object[] toArray(EStructuralFeature feature);

  /**
   * @since 2.0
   */
  public int hashCode(EStructuralFeature feature);

  /**
   * @since 4.2
   */
  public void accept(CDORevisionValueVisitor visitor);

  /**
   * @since 4.9
   */
  public void accept(CDORevisionValueVisitor visitor, java.util.function.Predicate<EStructuralFeature> filter);

  /**
   * @see DelegatingEcoreEList#inverseAdd(Object, NotificationChain)
   * @since 4.27
   */
  public default int calculateContainerReferenceID(EReference containingReference)
  {
    EClass eClass = revision().getEClass();
    return calculateContainerReferenceID(containingReference, eClass);
  }

  /**
   * @see #getContainerFeatureID()
   * @see DelegatingEcoreEList#inverseAdd(Object, NotificationChain)
   * @since 4.27
   */
  public static int calculateContainerReferenceID(EReference containingReference, EClass childClass)
  {
    EReference inverseReference = containingReference.getEOpposite();
    if (inverseReference != null) // hasNavigableInverse()
    {
      Class<?> instanceClass = containingReference.getEType().getInstanceClass();
      if (instanceClass == null)
      {
        return childClass.getFeatureID(inverseReference);
      }

      return inverseReference.getFeatureID();
    }

    // NOT hasNavigableInverse()
    return InternalEObject.EOPPOSITE_FEATURE_BASE - containingReference.getFeatureID();
  }

  /**
   * Provides the input to the calculation of the feature in the container revision that actually holds this revision.
   * <p>
   * <b>Usage Example:</b>
   * <p>
   * <pre>
   * CDORevision revision = ...;
   * CDORevision container = <i>Util.getRevision</i>(revision.data().getContainerID());
   *
   * int containerFeatureID = revision.data().getContainerFeatureID();
   *
   * EStructuralFeature feature = containerFeatureID &lt;= InternalEObject.EOPPOSITE_FEATURE_BASE ?
   *     container.getEClass().getEStructuralFeature(InternalEObject.EOPPOSITE_FEATURE_BASE - containerFeatureID) :
   *     ((EReference)revision.getEClass().getEStructuralFeature(containerFeatureID)).getEOpposite();
   * </pre>
   *
   * @see #getContainerFeatureID()
   * @see DelegatingEcoreEList#inverseAdd(Object, NotificationChain)
   * @since 4.26
   */
  public static EReference calculateContainingReference(int containerFeatureID, EClass childClass, EClass containerClass)
  {
    if (containerFeatureID <= InternalEObject.EOPPOSITE_FEATURE_BASE)
    {
      // NOT hasNavigableInverse()
      return (EReference)containerClass.getEStructuralFeature(InternalEObject.EOPPOSITE_FEATURE_BASE - containerFeatureID);
    }

    // hasNavigableInverse()
    return ((EReference)childClass.getEStructuralFeature(containerFeatureID)).getEOpposite();
  }

  /**
   * @since 4.2
   * @deprecated As of 4.9 use {@link #accept(CDORevisionValueVisitor, java.util.function.Predicate)}.
   */
  @Deprecated
  public void accept(CDORevisionValueVisitor visitor, org.eclipse.net4j.util.Predicate<EStructuralFeature> filter);

  /**
  * @deprecated As of 4.27 use {@link #getContainerFeatureID()}.
  */
  @Deprecated
  public int getContainingFeatureID();

  /**
   * @since 4.26
   * @deprecated As of 4.27 use {@link #calculateContainerReferenceID(EReference)}.
   */
  @Deprecated
  public default int calculateContainingReferenceID(EReference containingReference)
  {
    return calculateContainerReferenceID(containingReference);
  }

  /**
   * @since 4.26
   * @deprecated As of 4.27 use {@link #calculateContainerReferenceID(EReference, EClass)}.
   */
  @Deprecated
  public static int calculateContainingReferenceID(EReference containingReference, EClass childClass)
  {
    return calculateContainerReferenceID(containingReference, childClass);
  }
}
