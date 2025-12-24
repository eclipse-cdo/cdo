/*
 * Copyright (c) 2010-2012, 2014, 2016, 2019-2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.etypes.impl;

import org.eclipse.emf.cdo.etypes.Annotation;
import org.eclipse.emf.cdo.etypes.EtypesPackage;
import org.eclipse.emf.cdo.etypes.ModelElement;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.InternalEList;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Model Element</b></em>'.
 *
 * @since 4.0 <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.etypes.impl.ModelElementImpl#getAnnotations <em>Annotations</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class ModelElementImpl extends CDOObjectImpl implements ModelElement
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected ModelElementImpl()
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
    return EtypesPackage.Literals.MODEL_ELEMENT;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected int eStaticFeatureCount()
  {
    return 0;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<Annotation> getAnnotations()
  {
    return (EList<Annotation>)eDynamicGet(EtypesPackage.MODEL_ELEMENT__ANNOTATIONS, EtypesPackage.Literals.MODEL_ELEMENT__ANNOTATIONS, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public Annotation getAnnotation(String source)
  {
    EList<Annotation> annotations = getAnnotations();
    if (annotations != null)
    {
      if (annotations instanceof BasicEList<?>)
      {
        int size = annotations.size();
        if (size > 0)
        {
          Annotation[] annotationArray = (Annotation[])((BasicEList<?>)annotations).data();
          if (source == null)
          {
            for (int i = 0; i < size; ++i)
            {
              Annotation annotation = annotationArray[i];
              if (annotation.getSource() == null)
              {
                return annotation;
              }
            }
          }
          else
          {
            for (int i = 0; i < size; ++i)
            {
              Annotation annotation = annotationArray[i];
              if (source.equals(annotation.getSource()))
              {
                return annotation;
              }
            }
          }
        }
      }
      else
      {
        if (source == null)
        {
          for (Annotation annotation : annotations)
          {
            if (annotation.getSource() == null)
            {
              return annotation;
            }
          }
        }
        else
        {
          for (Annotation annotation : annotations)
          {
            if (source.equals(annotation.getSource()))
            {
              return annotation;
            }
          }
        }
      }
    }

    return null;
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case EtypesPackage.MODEL_ELEMENT__ANNOTATIONS:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getAnnotations()).basicAdd(otherEnd, msgs);
    }
    return eDynamicInverseAdd(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case EtypesPackage.MODEL_ELEMENT__ANNOTATIONS:
      return ((InternalEList<?>)getAnnotations()).basicRemove(otherEnd, msgs);
    }
    return eDynamicInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case EtypesPackage.MODEL_ELEMENT__ANNOTATIONS:
      return getAnnotations();
    }
    return eDynamicGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case EtypesPackage.MODEL_ELEMENT__ANNOTATIONS:
      getAnnotations().clear();
      getAnnotations().addAll((Collection<? extends Annotation>)newValue);
      return;
    }
    eDynamicSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case EtypesPackage.MODEL_ELEMENT__ANNOTATIONS:
      getAnnotations().clear();
      return;
    }
    eDynamicUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case EtypesPackage.MODEL_ELEMENT__ANNOTATIONS:
      return !getAnnotations().isEmpty();
    }
    return eDynamicIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException
  {
    switch (operationID)
    {
    case EtypesPackage.MODEL_ELEMENT___GET_ANNOTATION__STRING:
      return getAnnotation((String)arguments.get(0));
    }
    return eDynamicInvoke(operationID, arguments);
  }

} // ModelElementImpl
