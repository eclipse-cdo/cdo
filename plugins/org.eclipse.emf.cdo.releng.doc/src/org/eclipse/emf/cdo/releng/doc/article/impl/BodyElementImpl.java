/*
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.releng.doc.article.impl;

import org.eclipse.emf.cdo.releng.doc.article.ArticlePackage;
import org.eclipse.emf.cdo.releng.doc.article.BodyElement;
import org.eclipse.emf.cdo.releng.doc.article.BodyElementContainer;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.sun.javadoc.Tag;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Body Element</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.impl.BodyElementImpl#getContainer <em>Container</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.impl.BodyElementImpl#getTag <em>Tag</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public abstract class BodyElementImpl extends EObjectImpl implements BodyElement
{
  /**
   * The default value of the '{@link #getTag() <em>Tag</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getTag()
   * @generated
   * @ordered
   */
  protected static final Tag TAG_EDEFAULT = null;

  // TODO Is tag needed?
  private Tag tag;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected BodyElementImpl()
  {
    super();
  }

  BodyElementImpl(Tag tag)
  {
    this.tag = tag;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return ArticlePackage.Literals.BODY_ELEMENT;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public BodyElementContainer getContainer()
  {
    if (eContainerFeatureID() != ArticlePackage.BODY_ELEMENT__CONTAINER)
      return null;
    return (BodyElementContainer)eContainer();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public NotificationChain basicSetContainer(BodyElementContainer newContainer, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newContainer, ArticlePackage.BODY_ELEMENT__CONTAINER, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setContainer(BodyElementContainer newContainer)
  {
    if (newContainer != eInternalContainer()
        || (eContainerFeatureID() != ArticlePackage.BODY_ELEMENT__CONTAINER && newContainer != null))
    {
      if (EcoreUtil.isAncestor(this, newContainer))
        throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
      NotificationChain msgs = null;
      if (eInternalContainer() != null)
        msgs = eBasicRemoveFromContainer(msgs);
      if (newContainer != null)
        msgs = ((InternalEObject)newContainer).eInverseAdd(this, ArticlePackage.BODY_ELEMENT_CONTAINER__ELEMENTS,
            BodyElementContainer.class, msgs);
      msgs = basicSetContainer(newContainer, msgs);
      if (msgs != null)
        msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, ArticlePackage.BODY_ELEMENT__CONTAINER, newContainer,
          newContainer));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  public Tag getTag()
  {
    return tag;
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
    case ArticlePackage.BODY_ELEMENT__CONTAINER:
      if (eInternalContainer() != null)
        msgs = eBasicRemoveFromContainer(msgs);
      return basicSetContainer((BodyElementContainer)otherEnd, msgs);
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
    case ArticlePackage.BODY_ELEMENT__CONTAINER:
      return basicSetContainer(null, msgs);
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
    case ArticlePackage.BODY_ELEMENT__CONTAINER:
      return eInternalContainer().eInverseRemove(this, ArticlePackage.BODY_ELEMENT_CONTAINER__ELEMENTS,
          BodyElementContainer.class, msgs);
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
    case ArticlePackage.BODY_ELEMENT__CONTAINER:
      return getContainer();
    case ArticlePackage.BODY_ELEMENT__TAG:
      return getTag();
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
    case ArticlePackage.BODY_ELEMENT__CONTAINER:
      setContainer((BodyElementContainer)newValue);
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
    case ArticlePackage.BODY_ELEMENT__CONTAINER:
      setContainer((BodyElementContainer)null);
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
    case ArticlePackage.BODY_ELEMENT__CONTAINER:
      return getContainer() != null;
    case ArticlePackage.BODY_ELEMENT__TAG:
      return TAG_EDEFAULT == null ? getTag() != null : !TAG_EDEFAULT.equals(getTag());
    }
    return super.eIsSet(featureID);
  }

} // BodyElementImpl
