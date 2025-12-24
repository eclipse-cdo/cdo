/**
 * Copyright (c) 2018, 2019 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   IBM - Initial API and implementation
 */
package org.eclipse.emf.cdo.ecore.impl;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EAnnotation</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.ecore.impl.EAnnotationImpl#getSource <em>Source</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EAnnotationImpl#getDetails <em>Details</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EAnnotationImpl#getEModelElement <em>EModel Element</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EAnnotationImpl#getContents <em>Contents</em>}</li>
 *   <li>{@link org.eclipse.emf.ecore.impl.EAnnotationImpl#getReferences <em>References</em>}</li>
 * </ul>
 *
 * @generated
 */
public class EAnnotationImpl extends EModelElementImpl implements EAnnotation
{
  /**
  * The default value of the '{@link #getSource() <em>Source</em>}' attribute.
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @see #getSource()
  * @generated
  * @ordered
  */
  protected static final String SOURCE_EDEFAULT = null;

  // /**
  // * The cached value of the '{@link #getSource() <em>Source</em>}' attribute.
  // * <!-- begin-user-doc -->
  // * <!-- end-user-doc -->
  // * @see #getSource()
  // * @generated
  // * @ordered
  // */
  // protected String source = SOURCE_EDEFAULT;
  //
  // /**
  // * The cached value of the '{@link #getDetails() <em>Details</em>}' map.
  // * <!-- begin-user-doc -->
  // * <!-- end-user-doc -->
  // * @see #getDetails()
  // * @generated
  // * @ordered
  // */
  // protected EMap<String, String> details;
  //
  // /**
  // * The cached value of the '{@link #getContents() <em>Contents</em>}' containment reference list.
  // * <!-- begin-user-doc -->
  // * <!-- end-user-doc -->
  // * @see #getContents()
  // * @generated
  // * @ordered
  // */
  // protected EList<EObject> contents;
  //
  // /**
  // * The cached value of the '{@link #getReferences() <em>References</em>}' reference list.
  // * <!-- begin-user-doc -->
  // * <!-- end-user-doc -->
  // * @see #getReferences()
  // * @generated
  // * @ordered
  // */
  // protected EList<EObject> references;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected EAnnotationImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return EcorePackage.Literals.EANNOTATION;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public String getSource()
  {
    return (String)eDynamicGet(EcorePackage.Literals.EANNOTATION__SOURCE, true);
  }

  @Override
  public void setSource(String newSource)
  {
    setSourceGen(newSource == null ? null : newSource.intern());
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public void setSourceGen(String newSource)
  {
    eDynamicSet(EcorePackage.Literals.EANNOTATION__SOURCE, newSource);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  @SuppressWarnings("unchecked")
  public EMap<String, String> getDetails()
  {
    // In EMF the returned map specializes the ensureEntryDataExists() method to be thread-safe.
    // In CDO that method is already marked "synchronized", so no specialization is needed here.
    return (EMap<String, String>)eDynamicGet(EcorePackage.Literals.EANNOTATION__DETAILS, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public EModelElement getEModelElement()
  {
    return (EModelElement)eDynamicGet(EcorePackage.Literals.EANNOTATION__EMODEL_ELEMENT, true);
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated NOT
  */
  public NotificationChain basicSetEModelElement(EModelElement newEModelElement, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newEModelElement, EcorePackage.EANNOTATION__EMODEL_ELEMENT, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public void setEModelElement(EModelElement newEModelElement)
  {
    eDynamicSet(EcorePackage.Literals.EANNOTATION__EMODEL_ELEMENT, newEModelElement);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<EObject> getContents()
  {
    return (EList<EObject>)eDynamicGet(EcorePackage.Literals.EANNOTATION__CONTENTS, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<EObject> getReferences()
  {
    return (EList<EObject>)eDynamicGet(EcorePackage.Literals.EANNOTATION__REFERENCES, true);
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
    case EcorePackage.EANNOTATION__EANNOTATIONS:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getEAnnotations()).basicAdd(otherEnd, msgs);
    case EcorePackage.EANNOTATION__EMODEL_ELEMENT:
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      return basicSetEModelElement((EModelElement)otherEnd, msgs);
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
    case EcorePackage.EANNOTATION__EANNOTATIONS:
      return ((InternalEList<?>)getEAnnotations()).basicRemove(otherEnd, msgs);
    case EcorePackage.EANNOTATION__DETAILS:
      return ((InternalEList<?>)getDetails()).basicRemove(otherEnd, msgs);
    case EcorePackage.EANNOTATION__EMODEL_ELEMENT:
      return basicSetEModelElement(null, msgs);
    case EcorePackage.EANNOTATION__CONTENTS:
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
    case EcorePackage.EANNOTATION__EMODEL_ELEMENT:
      return eInternalContainer().eInverseRemove(this, EcorePackage.EMODEL_ELEMENT__EANNOTATIONS, EModelElement.class, msgs);
    }
    return eDynamicBasicRemoveFromContainer(msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case EcorePackage.EANNOTATION__EANNOTATIONS:
      return getEAnnotations();
    case EcorePackage.EANNOTATION__SOURCE:
      return getSource();
    case EcorePackage.EANNOTATION__DETAILS:
      if (coreType)
      {
        return getDetails();
      }
      return getDetails().map();
    case EcorePackage.EANNOTATION__EMODEL_ELEMENT:
      return getEModelElement();
    case EcorePackage.EANNOTATION__CONTENTS:
      return getContents();
    case EcorePackage.EANNOTATION__REFERENCES:
      return getReferences();
    }
    return eDynamicGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case EcorePackage.EANNOTATION__EANNOTATIONS:
    {
      EList<EAnnotation> eAnnotations = getEAnnotations();
      eAnnotations.clear();
      eAnnotations.addAll((Collection<? extends EAnnotation>)newValue);
      return;
    }
    case EcorePackage.EANNOTATION__SOURCE:
      setSource((String)newValue);
      return;
    case EcorePackage.EANNOTATION__DETAILS:
      ((EStructuralFeature.Setting)getDetails()).set(newValue);
      return;
    case EcorePackage.EANNOTATION__EMODEL_ELEMENT:
      setEModelElement((EModelElement)newValue);
      return;
    case EcorePackage.EANNOTATION__CONTENTS:
    {
      EList<EObject> contents = getContents();
      contents.clear();
      contents.addAll((Collection<? extends EObject>)newValue);
      return;
    }
    case EcorePackage.EANNOTATION__REFERENCES:
    {
      EList<EObject> references = getReferences();
      references.clear();
      references.addAll((Collection<? extends EObject>)newValue);
      return;
    }
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
    case EcorePackage.EANNOTATION__EANNOTATIONS:
      getEAnnotations().clear();
      return;
    case EcorePackage.EANNOTATION__SOURCE:
      setSource(SOURCE_EDEFAULT);
      return;
    case EcorePackage.EANNOTATION__DETAILS:
      getDetails().clear();
      return;
    case EcorePackage.EANNOTATION__EMODEL_ELEMENT:
      setEModelElement((EModelElement)null);
      return;
    case EcorePackage.EANNOTATION__CONTENTS:
      getContents().clear();
      return;
    case EcorePackage.EANNOTATION__REFERENCES:
      getReferences().clear();
      return;
    }
    eDynamicUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case EcorePackage.EANNOTATION__EANNOTATIONS:
    {
      EList<EAnnotation> eAnnotations = getEAnnotations();
      return eAnnotations != null && !eAnnotations.isEmpty();
    }
    case EcorePackage.EANNOTATION__SOURCE:
    {
      return getSource() != null;
    }
    case EcorePackage.EANNOTATION__DETAILS:
    {
      EMap<String, String> details = getDetails();
      return details != null && !details.isEmpty();
    }
    case EcorePackage.EANNOTATION__EMODEL_ELEMENT:
      return getEModelElement() != null;
    case EcorePackage.EANNOTATION__CONTENTS:
    {
      EList<EObject> contents = getContents();
      return contents != null && !contents.isEmpty();
    }
    case EcorePackage.EANNOTATION__REFERENCES:
    {
      EList<EObject> references = getReferences();
      return references != null && !references.isEmpty();
    }
    }
    return eDynamicIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public String toString()
  {
    if (eIsProxy())
    {
      return super.toString();
    }

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (source: ");
    result.append(getSource());
    result.append(')');
    return result.toString();
  }

} // EAnnotationImpl
