/*
 * Copyright (c) 2010-2012, 2014, 2016, 2019-2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.etypes.impl;

import org.eclipse.emf.cdo.etypes.Annotation;
import org.eclipse.emf.cdo.etypes.EtypesPackage;
import org.eclipse.emf.cdo.etypes.ModelElement;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Annotation</b></em>'.
 *
 * @since 4.0
 * @noextend This interface is not intended to be extended by clients. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.etypes.impl.AnnotationImpl#getSource <em>Source</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.etypes.impl.AnnotationImpl#getDetails <em>Details</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.etypes.impl.AnnotationImpl#getModelElement <em>Model Element</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.etypes.impl.AnnotationImpl#getContents <em>Contents</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.etypes.impl.AnnotationImpl#getReferences <em>References</em>}</li>
 * </ul>
 *
 * @generated
 */
public class AnnotationImpl extends ModelElementImpl implements Annotation
{
  /**
   * The default value of the '{@link #getSource() <em>Source</em>}' attribute.
   * <!-- begin-user-doc -->
   * @since 4.11
   * <!-- end-user-doc -->
   * @see #getSource()
   * @generated
   * @ordered
   */
  protected static final String SOURCE_EDEFAULT = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected AnnotationImpl()
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
    return EtypesPackage.Literals.ANNOTATION;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getSource()
  {
    return (String)eDynamicGet(EtypesPackage.ANNOTATION__SOURCE, EtypesPackage.Literals.ANNOTATION__SOURCE, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setSource(String newSource)
  {
    eDynamicSet(EtypesPackage.ANNOTATION__SOURCE, EtypesPackage.Literals.ANNOTATION__SOURCE, newSource);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EMap<String, String> getDetails()
  {
    return (EMap<String, String>)eDynamicGet(EtypesPackage.ANNOTATION__DETAILS, EtypesPackage.Literals.ANNOTATION__DETAILS, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ModelElement getModelElement()
  {
    return (ModelElement)eDynamicGet(EtypesPackage.ANNOTATION__MODEL_ELEMENT, EtypesPackage.Literals.ANNOTATION__MODEL_ELEMENT, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.11
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetModelElement(ModelElement newModelElement, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newModelElement, EtypesPackage.ANNOTATION__MODEL_ELEMENT, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setModelElement(ModelElement newModelElement)
  {
    eDynamicSet(EtypesPackage.ANNOTATION__MODEL_ELEMENT, EtypesPackage.Literals.ANNOTATION__MODEL_ELEMENT, newModelElement);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<EObject> getContents()
  {
    return (EList<EObject>)eDynamicGet(EtypesPackage.ANNOTATION__CONTENTS, EtypesPackage.Literals.ANNOTATION__CONTENTS, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<EObject> getReferences()
  {
    return (EList<EObject>)eDynamicGet(EtypesPackage.ANNOTATION__REFERENCES, EtypesPackage.Literals.ANNOTATION__REFERENCES, true, true);
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
    case EtypesPackage.ANNOTATION__ANNOTATIONS:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getAnnotations()).basicAdd(otherEnd, msgs);
    case EtypesPackage.ANNOTATION__MODEL_ELEMENT:
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      return basicSetModelElement((ModelElement)otherEnd, msgs);
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
    case EtypesPackage.ANNOTATION__ANNOTATIONS:
      return ((InternalEList<?>)getAnnotations()).basicRemove(otherEnd, msgs);
    case EtypesPackage.ANNOTATION__DETAILS:
      return ((InternalEList<?>)getDetails()).basicRemove(otherEnd, msgs);
    case EtypesPackage.ANNOTATION__MODEL_ELEMENT:
      return basicSetModelElement(null, msgs);
    case EtypesPackage.ANNOTATION__CONTENTS:
      return ((InternalEList<?>)getContents()).basicRemove(otherEnd, msgs);
    }
    return eDynamicInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs)
  {
    switch (eContainerFeatureID())
    {
    case EtypesPackage.ANNOTATION__MODEL_ELEMENT:
      return eInternalContainer().eInverseRemove(this, EtypesPackage.MODEL_ELEMENT__ANNOTATIONS, ModelElement.class, msgs);
    }
    return eDynamicBasicRemoveFromContainer(msgs);
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
    case EtypesPackage.ANNOTATION__ANNOTATIONS:
      return getAnnotations();
    case EtypesPackage.ANNOTATION__SOURCE:
      return getSource();
    case EtypesPackage.ANNOTATION__DETAILS:
      if (coreType)
      {
        return getDetails();
      }
      return getDetails().map();
    case EtypesPackage.ANNOTATION__MODEL_ELEMENT:
      return getModelElement();
    case EtypesPackage.ANNOTATION__CONTENTS:
      return getContents();
    case EtypesPackage.ANNOTATION__REFERENCES:
      return getReferences();
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
    case EtypesPackage.ANNOTATION__ANNOTATIONS:
      getAnnotations().clear();
      getAnnotations().addAll((Collection<? extends Annotation>)newValue);
      return;
    case EtypesPackage.ANNOTATION__SOURCE:
      setSource((String)newValue);
      return;
    case EtypesPackage.ANNOTATION__DETAILS:
      ((EStructuralFeature.Setting)getDetails()).set(newValue);
      return;
    case EtypesPackage.ANNOTATION__MODEL_ELEMENT:
      setModelElement((ModelElement)newValue);
      return;
    case EtypesPackage.ANNOTATION__CONTENTS:
      getContents().clear();
      getContents().addAll((Collection<? extends EObject>)newValue);
      return;
    case EtypesPackage.ANNOTATION__REFERENCES:
      getReferences().clear();
      getReferences().addAll((Collection<? extends EObject>)newValue);
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
    case EtypesPackage.ANNOTATION__ANNOTATIONS:
      getAnnotations().clear();
      return;
    case EtypesPackage.ANNOTATION__SOURCE:
      setSource(SOURCE_EDEFAULT);
      return;
    case EtypesPackage.ANNOTATION__DETAILS:
      getDetails().clear();
      return;
    case EtypesPackage.ANNOTATION__MODEL_ELEMENT:
      setModelElement((ModelElement)null);
      return;
    case EtypesPackage.ANNOTATION__CONTENTS:
      getContents().clear();
      return;
    case EtypesPackage.ANNOTATION__REFERENCES:
      getReferences().clear();
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
    case EtypesPackage.ANNOTATION__ANNOTATIONS:
      return !getAnnotations().isEmpty();
    case EtypesPackage.ANNOTATION__SOURCE:
      return SOURCE_EDEFAULT == null ? getSource() != null : !SOURCE_EDEFAULT.equals(getSource());
    case EtypesPackage.ANNOTATION__DETAILS:
      return !getDetails().isEmpty();
    case EtypesPackage.ANNOTATION__MODEL_ELEMENT:
      return getModelElement() != null;
    case EtypesPackage.ANNOTATION__CONTENTS:
      return !getContents().isEmpty();
    case EtypesPackage.ANNOTATION__REFERENCES:
      return !getReferences().isEmpty();
    }
    return eDynamicIsSet(featureID);
  }

} // AnnotationImpl
