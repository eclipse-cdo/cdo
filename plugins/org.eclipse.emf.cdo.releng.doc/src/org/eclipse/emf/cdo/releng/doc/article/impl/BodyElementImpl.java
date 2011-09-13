/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.releng.doc.article.impl;

import org.eclipse.emf.cdo.releng.doc.article.ArticlePackage;
import org.eclipse.emf.cdo.releng.doc.article.Body;
import org.eclipse.emf.cdo.releng.doc.article.BodyElement;
import org.eclipse.emf.cdo.releng.doc.article.Callout;

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
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.impl.BodyElementImpl#getBody <em>Body</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.impl.BodyElementImpl#getHtml <em>Html</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.impl.BodyElementImpl#getCallout <em>Callout</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public abstract class BodyElementImpl extends EObjectImpl implements BodyElement
{
  /**
   * The default value of the '{@link #getHtml() <em>Html</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @see #getHtml()
   * @generated
   * @ordered
   */
  protected static final String HTML_EDEFAULT = null;

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

  BodyElementImpl(Body body, Tag tag)
  {
    setBody(body);
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
  public Body getBody()
  {
    if (eContainerFeatureID() != ArticlePackage.BODY_ELEMENT__BODY)
      return null;
    return (Body)eContainer();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public NotificationChain basicSetBody(Body newBody, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newBody, ArticlePackage.BODY_ELEMENT__BODY, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setBody(Body newBody)
  {
    if (newBody != eInternalContainer()
        || (eContainerFeatureID() != ArticlePackage.BODY_ELEMENT__BODY && newBody != null))
    {
      if (EcoreUtil.isAncestor(this, newBody))
        throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
      NotificationChain msgs = null;
      if (eInternalContainer() != null)
        msgs = eBasicRemoveFromContainer(msgs);
      if (newBody != null)
        msgs = ((InternalEObject)newBody).eInverseAdd(this, ArticlePackage.BODY__ELEMENTS, Body.class, msgs);
      msgs = basicSetBody(newBody, msgs);
      if (msgs != null)
        msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, ArticlePackage.BODY_ELEMENT__BODY, newBody, newBody));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  public abstract String getHtml();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Callout getCallout()
  {
    if (eContainerFeatureID() != ArticlePackage.BODY_ELEMENT__CALLOUT)
      return null;
    return (Callout)eContainer();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public NotificationChain basicSetCallout(Callout newCallout, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newCallout, ArticlePackage.BODY_ELEMENT__CALLOUT, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setCallout(Callout newCallout)
  {
    if (newCallout != eInternalContainer()
        || (eContainerFeatureID() != ArticlePackage.BODY_ELEMENT__CALLOUT && newCallout != null))
    {
      if (EcoreUtil.isAncestor(this, newCallout))
        throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
      NotificationChain msgs = null;
      if (eInternalContainer() != null)
        msgs = eBasicRemoveFromContainer(msgs);
      if (newCallout != null)
        msgs = ((InternalEObject)newCallout).eInverseAdd(this, ArticlePackage.CALLOUT__ELEMENTS, Callout.class, msgs);
      msgs = basicSetCallout(newCallout, msgs);
      if (msgs != null)
        msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, ArticlePackage.BODY_ELEMENT__CALLOUT, newCallout,
          newCallout));
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
    case ArticlePackage.BODY_ELEMENT__BODY:
      if (eInternalContainer() != null)
        msgs = eBasicRemoveFromContainer(msgs);
      return basicSetBody((Body)otherEnd, msgs);
    case ArticlePackage.BODY_ELEMENT__CALLOUT:
      if (eInternalContainer() != null)
        msgs = eBasicRemoveFromContainer(msgs);
      return basicSetCallout((Callout)otherEnd, msgs);
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
    case ArticlePackage.BODY_ELEMENT__BODY:
      return basicSetBody(null, msgs);
    case ArticlePackage.BODY_ELEMENT__CALLOUT:
      return basicSetCallout(null, msgs);
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
    case ArticlePackage.BODY_ELEMENT__BODY:
      return eInternalContainer().eInverseRemove(this, ArticlePackage.BODY__ELEMENTS, Body.class, msgs);
    case ArticlePackage.BODY_ELEMENT__CALLOUT:
      return eInternalContainer().eInverseRemove(this, ArticlePackage.CALLOUT__ELEMENTS, Callout.class, msgs);
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
    case ArticlePackage.BODY_ELEMENT__BODY:
      return getBody();
    case ArticlePackage.BODY_ELEMENT__HTML:
      return getHtml();
    case ArticlePackage.BODY_ELEMENT__CALLOUT:
      return getCallout();
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
    case ArticlePackage.BODY_ELEMENT__BODY:
      setBody((Body)newValue);
      return;
    case ArticlePackage.BODY_ELEMENT__CALLOUT:
      setCallout((Callout)newValue);
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
    case ArticlePackage.BODY_ELEMENT__BODY:
      setBody((Body)null);
      return;
    case ArticlePackage.BODY_ELEMENT__CALLOUT:
      setCallout((Callout)null);
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
    case ArticlePackage.BODY_ELEMENT__BODY:
      return getBody() != null;
    case ArticlePackage.BODY_ELEMENT__HTML:
      return HTML_EDEFAULT == null ? getHtml() != null : !HTML_EDEFAULT.equals(getHtml());
    case ArticlePackage.BODY_ELEMENT__CALLOUT:
      return getCallout() != null;
    }
    return super.eIsSet(featureID);
  }

} // BodyElementImpl
