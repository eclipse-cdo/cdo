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
import org.eclipse.emf.cdo.releng.doc.article.BodyElement;
import org.eclipse.emf.cdo.releng.doc.article.Link;
import org.eclipse.emf.cdo.releng.doc.article.LinkTarget;
import org.eclipse.emf.cdo.releng.doc.article.StructuralElement;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

import com.sun.javadoc.SeeTag;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Link</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.impl.LinkImpl#getTarget <em>Target</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class LinkImpl extends BodyElementImpl implements Link
{
  /**
   * The cached value of the '{@link #getTarget() <em>Target</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @see #getTarget()
   * @generated
   * @ordered
   */
  protected LinkTarget target;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected LinkImpl()
  {
    super();
  }

  LinkImpl(SeeTag tag, LinkTarget target)
  {
    super(tag);
    this.target = target;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return ArticlePackage.Literals.LINK;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public LinkTarget getTarget()
  {
    return target;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public void setTarget(LinkTarget newTarget)
  {
    LinkTarget oldTarget = target;
    target = newTarget;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, ArticlePackage.LINK__TARGET, oldTarget, target));
    }
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
    case ArticlePackage.LINK__TARGET:
      return getTarget();
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
    case ArticlePackage.LINK__TARGET:
      setTarget((LinkTarget)newValue);
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
    case ArticlePackage.LINK__TARGET:
      setTarget((LinkTarget)null);
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
    case ArticlePackage.LINK__TARGET:
      return target != null;
    }
    return super.eIsSet(featureID);
  }

  @Override
  public SeeTag getTag()
  {
    return (SeeTag)super.getTag();
  }

  public BodyElement copy()
  {
    return new LinkImpl(getTag(), target);
  }

  public void generate(PrintWriter out, StructuralElement linkSource) throws IOException
  {
    target.generateLink(out, linkSource, getTag());
  }

} // LinkImpl
