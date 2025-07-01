/*
 * Copyright (c) 2008-2013, 2016, 2019, 2020, 2025 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
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
   * Provides the input to the calculation of the feature in the container revision that actually holds this revision.
   * <p>
   * <b>Usage Example:</b>
   * <p>
   * <pre><code>
   * CDORevision revision = ...;
   * CDORevision container = <i>Util.getRevision</i>(revision.data().getContainerID());
   *
   * int containingFeatureID = revision.data().getContainingFeatureID();
   *
   * EStructuralFeature feature = containingFeatureID &lt;= InternalEObject.EOPPOSITE_FEATURE_BASE ?
   *     container.getEClass().getEStructuralFeature(InternalEObject.EOPPOSITE_FEATURE_BASE - containingFeatureID) :
   *     ((EReference)revision.getEClass().getEStructuralFeature(containingFeatureID)).getEOpposite();</code></pre>
   *
   * @see #calculateContainingReference(int, EClass, EClass)
   * @see DelegatingEcoreEList#inverseAdd(Object, NotificationChain)
   * @see BasicEObjectImpl#eContainingFeature()
   * @see #getContainerID()
   */
  public int getContainingFeatureID();

  /**
   * @see #getContainingFeatureID()
   * @see DelegatingEcoreEList#inverseAdd(Object, NotificationChain)
   * @since 4.26
   */
  public default EReference getContainingReference(EClass containerClass)
  {
    int containingFeatureID = getContainingFeatureID();
    EClass eClass = revision().getEClass();
    return calculateContainingReference(containingFeatureID, eClass, containerClass);
  }

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
   * @since 4.2
   * @deprecated As of 4.9 use {@link #accept(CDORevisionValueVisitor, java.util.function.Predicate)}.
   */
  @Deprecated
  public void accept(CDORevisionValueVisitor visitor, org.eclipse.net4j.util.Predicate<EStructuralFeature> filter);

  /**
   * @since 4.9
   */
  public void accept(CDORevisionValueVisitor visitor, java.util.function.Predicate<EStructuralFeature> filter);

  /**
   * @see DelegatingEcoreEList#inverseAdd(Object, NotificationChain)
   * @since 4.26
   */
  public default int calculateContainingReferenceID(EReference containingReference)
  {
    EClass eClass = revision().getEClass();
    return calculateContainingReferenceID(containingReference, eClass);
  }

  /**
   * @see #getContainingFeatureID()
   * @see DelegatingEcoreEList#inverseAdd(Object, NotificationChain)
   * @since 4.26
   */
  public static int calculateContainingReferenceID(EReference containingReference, EClass childClass)
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

    return InternalEObject.EOPPOSITE_FEATURE_BASE - containingReference.getFeatureID();
  }

  /**
   * Provides the input to the calculation of the feature in the container revision that actually holds this revision.
   * <p>
   * <b>Usage Example:</b>
   * <p>
   * <pre><code>
   * CDORevision revision = ...;
   * CDORevision container = <i>Util.getRevision</i>(revision.data().getContainerID());
   *
   * int containingFeatureID = revision.data().getContainingFeatureID();
   *
   * EStructuralFeature feature = containingFeatureID &lt;= InternalEObject.EOPPOSITE_FEATURE_BASE ?
   *     container.getEClass().getEStructuralFeature(InternalEObject.EOPPOSITE_FEATURE_BASE - containingFeatureID) :
   *     ((EReference)revision.getEClass().getEStructuralFeature(containingFeatureID)).getEOpposite();</code></pre>
   *
   * @see #getContainingFeatureID()
   * @see DelegatingEcoreEList#inverseAdd(Object, NotificationChain)
   * @since 4.26
   */
  public static EReference calculateContainingReference(int containingFeatureID, EClass childClass, EClass containerClass)
  {
    if (containingFeatureID <= InternalEObject.EOPPOSITE_FEATURE_BASE)
    {
      return (EReference)containerClass.getEStructuralFeature(InternalEObject.EOPPOSITE_FEATURE_BASE - containingFeatureID);
    }

    return ((EReference)childClass.getEStructuralFeature(containingFeatureID)).getEOpposite();
  }
}
