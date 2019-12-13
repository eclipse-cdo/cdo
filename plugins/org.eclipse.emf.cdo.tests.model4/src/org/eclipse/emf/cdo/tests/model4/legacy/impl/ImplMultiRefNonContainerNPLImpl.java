/*
 * Copyright (c) 2013, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model4.legacy.impl;

import org.eclipse.emf.cdo.tests.model4.ImplMultiRefNonContainerNPL;
import org.eclipse.emf.cdo.tests.model4.legacy.model4Package;
import org.eclipse.emf.cdo.tests.model4interfaces.IContainedElementNoParentLink;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Impl Multi Ref Non Container NPL</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model4.legacy.impl.ImplMultiRefNonContainerNPLImpl#getElements <em>Elements</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ImplMultiRefNonContainerNPLImpl extends EObjectImpl implements ImplMultiRefNonContainerNPL
{
  /**
   * The cached value of the '{@link #getElements() <em>Elements</em>}' reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getElements()
   * @generated
   * @ordered
   */
  protected EList<IContainedElementNoParentLink> elements;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected ImplMultiRefNonContainerNPLImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return model4Package.eINSTANCE.getImplMultiRefNonContainerNPL();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<IContainedElementNoParentLink> getElements()
  {
    if (elements == null)
    {
      elements = new EObjectResolvingEList<IContainedElementNoParentLink>(IContainedElementNoParentLink.class, this,
          model4Package.IMPL_MULTI_REF_NON_CONTAINER_NPL__ELEMENTS);
    }
    return elements;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case model4Package.IMPL_MULTI_REF_NON_CONTAINER_NPL__ELEMENTS:
      return getElements();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case model4Package.IMPL_MULTI_REF_NON_CONTAINER_NPL__ELEMENTS:
      getElements().clear();
      getElements().addAll((Collection<? extends IContainedElementNoParentLink>)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case model4Package.IMPL_MULTI_REF_NON_CONTAINER_NPL__ELEMENTS:
      getElements().clear();
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case model4Package.IMPL_MULTI_REF_NON_CONTAINER_NPL__ELEMENTS:
      return elements != null && !elements.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} // ImplMultiRefNonContainerNPLImpl
