/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.releng.doc.article.Callout;
import org.eclipse.emf.cdo.releng.doc.article.Snippet;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.sun.javadoc.Tag;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Callout</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.impl.CalloutImpl#getSnippet <em>Snippet</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class CalloutImpl extends BodyElementContainerImpl implements Callout
{
  private Tag tag;

  private int index;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected CalloutImpl()
  {
    super();
  }

  CalloutImpl(Snippet snippet, Tag tag, int index)
  {
    setSnippet(snippet);
    this.tag = tag;
    this.index = index;

    BodyElementContainerImpl.analyzeTags(getElements(), tag.inlineTags(), false);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return ArticlePackage.Literals.CALLOUT;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Snippet getSnippet()
  {
    if (eContainerFeatureID() != ArticlePackage.CALLOUT__SNIPPET)
    {
      return null;
    }
    return (Snippet)eContainer();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public NotificationChain basicSetSnippet(Snippet newSnippet, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newSnippet, ArticlePackage.CALLOUT__SNIPPET, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setSnippet(Snippet newSnippet)
  {
    if (newSnippet != eInternalContainer() || eContainerFeatureID() != ArticlePackage.CALLOUT__SNIPPET
        && newSnippet != null)
    {
      if (EcoreUtil.isAncestor(this, newSnippet))
      {
        throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
      }
      NotificationChain msgs = null;
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      if (newSnippet != null)
      {
        msgs = ((InternalEObject)newSnippet).eInverseAdd(this, ArticlePackage.SNIPPET__CALLOUTS, Snippet.class, msgs);
      }
      msgs = basicSetSnippet(newSnippet, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, ArticlePackage.CALLOUT__SNIPPET, newSnippet, newSnippet));
    }
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
    case ArticlePackage.CALLOUT__SNIPPET:
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      return basicSetSnippet((Snippet)otherEnd, msgs);
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
    case ArticlePackage.CALLOUT__SNIPPET:
      return basicSetSnippet(null, msgs);
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
    case ArticlePackage.CALLOUT__SNIPPET:
      return eInternalContainer().eInverseRemove(this, ArticlePackage.SNIPPET__CALLOUTS, Snippet.class, msgs);
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
    case ArticlePackage.CALLOUT__SNIPPET:
      return getSnippet();
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
    case ArticlePackage.CALLOUT__SNIPPET:
      setSnippet((Snippet)newValue);
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
    case ArticlePackage.CALLOUT__SNIPPET:
      setSnippet((Snippet)null);
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
    case ArticlePackage.CALLOUT__SNIPPET:
      return getSnippet() != null;
    }
    return super.eIsSet(featureID);
  }

  public Tag getTag()
  {
    return tag;
  }

  public int getIndex()
  {
    return index;
  }

} // CalloutImpl
