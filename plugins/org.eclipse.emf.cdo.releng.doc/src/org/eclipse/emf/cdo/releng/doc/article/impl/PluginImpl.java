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
import org.eclipse.emf.cdo.releng.doc.article.ExtensionPoint;
import org.eclipse.emf.cdo.releng.doc.article.JavaPackage;
import org.eclipse.emf.cdo.releng.doc.article.Plugin;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Plugin</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.impl.PluginImpl#getName <em>Name</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.impl.PluginImpl#getPackages <em>Packages</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.impl.PluginImpl#getLabel <em>Label</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.impl.PluginImpl#getExtensionPoints <em>Extension Points</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class PluginImpl extends EObjectImpl implements Plugin
{
  /**
   * The default value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @see #getName()
   * @generated
   * @ordered
   */
  protected static final String NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getName()
   * @generated
   * @ordered
   */
  protected String name = NAME_EDEFAULT;

  /**
   * The cached value of the '{@link #getPackages() <em>Packages</em>}' containment reference list. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   *
   * @see #getPackages()
   * @generated
   * @ordered
   */
  protected EList<JavaPackage> packages;

  /**
   * The default value of the '{@link #getLabel() <em>Label</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @see #getLabel()
   * @generated
   * @ordered
   */
  protected static final String LABEL_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getLabel() <em>Label</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @see #getLabel()
   * @generated
   * @ordered
   */
  protected String label = LABEL_EDEFAULT;

  /**
   * The cached value of the '{@link #getExtensionPoints() <em>Extension Points</em>}' containment reference list. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getExtensionPoints()
   * @generated
   * @ordered
   */
  protected EList<ExtensionPoint> extensionPoints;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected PluginImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return ArticlePackage.Literals.PLUGIN;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public void setName(String newName)
  {
    String oldName = name;
    name = newName;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, ArticlePackage.PLUGIN__NAME, oldName, name));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public EList<JavaPackage> getPackages()
  {
    if (packages == null)
    {
      packages = new EObjectContainmentWithInverseEList<JavaPackage>(JavaPackage.class, this,
          ArticlePackage.PLUGIN__PACKAGES, ArticlePackage.JAVA_PACKAGE__PLUGIN);
    }
    return packages;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public String getLabel()
  {
    return label;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public void setLabel(String newLabel)
  {
    String oldLabel = label;
    label = newLabel;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, ArticlePackage.PLUGIN__LABEL, oldLabel, label));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public EList<ExtensionPoint> getExtensionPoints()
  {
    if (extensionPoints == null)
    {
      extensionPoints = new EObjectContainmentWithInverseEList<ExtensionPoint>(ExtensionPoint.class, this,
          ArticlePackage.PLUGIN__EXTENSION_POINTS, ArticlePackage.EXTENSION_POINT__PLUGIN);
    }
    return extensionPoints;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case ArticlePackage.PLUGIN__PACKAGES:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getPackages()).basicAdd(otherEnd, msgs);
    case ArticlePackage.PLUGIN__EXTENSION_POINTS:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getExtensionPoints()).basicAdd(otherEnd, msgs);
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
    case ArticlePackage.PLUGIN__PACKAGES:
      return ((InternalEList<?>)getPackages()).basicRemove(otherEnd, msgs);
    case ArticlePackage.PLUGIN__EXTENSION_POINTS:
      return ((InternalEList<?>)getExtensionPoints()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
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
    case ArticlePackage.PLUGIN__NAME:
      return getName();
    case ArticlePackage.PLUGIN__PACKAGES:
      return getPackages();
    case ArticlePackage.PLUGIN__LABEL:
      return getLabel();
    case ArticlePackage.PLUGIN__EXTENSION_POINTS:
      return getExtensionPoints();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case ArticlePackage.PLUGIN__NAME:
      setName((String)newValue);
      return;
    case ArticlePackage.PLUGIN__PACKAGES:
      getPackages().clear();
      getPackages().addAll((Collection<? extends JavaPackage>)newValue);
      return;
    case ArticlePackage.PLUGIN__LABEL:
      setLabel((String)newValue);
      return;
    case ArticlePackage.PLUGIN__EXTENSION_POINTS:
      getExtensionPoints().clear();
      getExtensionPoints().addAll((Collection<? extends ExtensionPoint>)newValue);
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
    case ArticlePackage.PLUGIN__NAME:
      setName(NAME_EDEFAULT);
      return;
    case ArticlePackage.PLUGIN__PACKAGES:
      getPackages().clear();
      return;
    case ArticlePackage.PLUGIN__LABEL:
      setLabel(LABEL_EDEFAULT);
      return;
    case ArticlePackage.PLUGIN__EXTENSION_POINTS:
      getExtensionPoints().clear();
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
    case ArticlePackage.PLUGIN__NAME:
      return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
    case ArticlePackage.PLUGIN__PACKAGES:
      return packages != null && !packages.isEmpty();
    case ArticlePackage.PLUGIN__LABEL:
      return LABEL_EDEFAULT == null ? label != null : !LABEL_EDEFAULT.equals(label);
    case ArticlePackage.PLUGIN__EXTENSION_POINTS:
      return extensionPoints != null && !extensionPoints.isEmpty();
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy())
    {
      return super.toString();
    }

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (name: ");
    result.append(name);
    result.append(", label: ");
    result.append(label);
    result.append(')');
    return result.toString();
  }

} // PluginImpl
