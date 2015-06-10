/*
 * Copyright (c) 2011, 2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.doc.article.impl;

import org.eclipse.emf.cdo.releng.doc.article.ArticlePackage;
import org.eclipse.emf.cdo.releng.doc.article.Documentation;
import org.eclipse.emf.cdo.releng.doc.article.EmbeddableElement;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.sun.javadoc.Doc;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Embeddable Element</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.impl.EmbeddableElementImpl#getId <em>Id</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.impl.EmbeddableElementImpl#getDocumentation <em>Documentation</em>}
 * </li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class EmbeddableElementImpl extends EObjectImpl implements EmbeddableElement
{
  protected static final String NL = System.getProperty("line.separator");

  /**
   * The default value of the '{@link #getId() <em>Id</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getId()
   * @generated
   * @ordered
   */
  protected static final Object ID_EDEFAULT = null;

  /**
   * The default value of the '{@link #getDoc() <em>Doc</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getDoc()
   * @generated
   * @ordered
   */
  protected static final Doc DOC_EDEFAULT = null;

  private Doc doc;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected EmbeddableElementImpl()
  {
    super();
  }

  public EmbeddableElementImpl(Documentation documentation, Doc doc)
  {
    this.doc = doc;
    setDocumentation(documentation);
    documentation.getContext().register(getId(), this);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return ArticlePackage.Literals.EMBEDDABLE_ELEMENT;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  public Object getId()
  {
    return doc;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public Documentation getDocumentation()
  {
    if (eContainerFeatureID() != ArticlePackage.EMBEDDABLE_ELEMENT__DOCUMENTATION)
    {
      return null;
    }
    return (Documentation)eContainer();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public NotificationChain basicSetDocumentation(Documentation newDocumentation, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newDocumentation, ArticlePackage.EMBEDDABLE_ELEMENT__DOCUMENTATION,
        msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public void setDocumentation(Documentation newDocumentation)
  {
    if (newDocumentation != eInternalContainer()
        || eContainerFeatureID() != ArticlePackage.EMBEDDABLE_ELEMENT__DOCUMENTATION && newDocumentation != null)
    {
      if (EcoreUtil.isAncestor(this, newDocumentation))
      {
        throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
      }
      NotificationChain msgs = null;
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      if (newDocumentation != null)
      {
        msgs = ((InternalEObject)newDocumentation).eInverseAdd(this, ArticlePackage.DOCUMENTATION__EMBEDDABLE_ELEMENTS,
            Documentation.class, msgs);
      }
      msgs = basicSetDocumentation(newDocumentation, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, ArticlePackage.EMBEDDABLE_ELEMENT__DOCUMENTATION,
          newDocumentation, newDocumentation));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  public Doc getDoc()
  {
    return doc;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case ArticlePackage.EMBEDDABLE_ELEMENT__DOCUMENTATION:
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      return basicSetDocumentation((Documentation)otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case ArticlePackage.EMBEDDABLE_ELEMENT__DOCUMENTATION:
      return basicSetDocumentation(null, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs)
  {
    switch (eContainerFeatureID())
    {
    case ArticlePackage.EMBEDDABLE_ELEMENT__DOCUMENTATION:
      return eInternalContainer().eInverseRemove(this, ArticlePackage.DOCUMENTATION__EMBEDDABLE_ELEMENTS,
          Documentation.class, msgs);
    }
    return super.eBasicRemoveFromContainerFeature(msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case ArticlePackage.EMBEDDABLE_ELEMENT__ID:
      return getId();
    case ArticlePackage.EMBEDDABLE_ELEMENT__DOCUMENTATION:
      return getDocumentation();
    case ArticlePackage.EMBEDDABLE_ELEMENT__DOC:
      return getDoc();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case ArticlePackage.EMBEDDABLE_ELEMENT__DOCUMENTATION:
      setDocumentation((Documentation)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case ArticlePackage.EMBEDDABLE_ELEMENT__DOCUMENTATION:
      setDocumentation((Documentation)null);
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case ArticlePackage.EMBEDDABLE_ELEMENT__ID:
      return ID_EDEFAULT == null ? getId() != null : !ID_EDEFAULT.equals(getId());
    case ArticlePackage.EMBEDDABLE_ELEMENT__DOCUMENTATION:
      return getDocumentation() != null;
    case ArticlePackage.EMBEDDABLE_ELEMENT__DOC:
      return DOC_EDEFAULT == null ? getDoc() != null : !DOC_EDEFAULT.equals(getDoc());
    }
    return super.eIsSet(featureID);
  }

} // EmbeddableElementImpl
