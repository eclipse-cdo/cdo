/*
 * Copyright (c) 2008-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.ui.defs.impl;

import org.eclipse.emf.cdo.ui.defs.CDOUIDefsPackage;
import org.eclipse.emf.cdo.ui.defs.EditorDef;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.defs.impl.DefImpl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>Editor Def</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.ui.defs.impl.EditorDefImpl#getEditorID <em>Editor ID</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EditorDefImpl extends DefImpl implements EditorDef
{
  /**
   * The default value of the '{@link #getEditorID() <em>Editor ID</em>}' attribute.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getEditorID()
   * @generated
   * @ordered
   */
  protected static final String EDITOR_ID_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getEditorID() <em>Editor ID</em>}' attribute.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getEditorID()
   * @generated
   * @ordered
   */
  protected String editorID = EDITOR_ID_EDEFAULT;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected EditorDefImpl()
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
    return CDOUIDefsPackage.Literals.EDITOR_DEF;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public String getEditorID()
  {
    return editorID;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void setEditorID(String newEditorID)
  {
    String oldEditorID = editorID;
    editorID = newEditorID;
    if (eNotificationRequired())
    {
      eNotify(
          new ENotificationImpl(this, Notification.SET, CDOUIDefsPackage.EDITOR_DEF__EDITOR_ID, oldEditorID, editorID));
    }
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
    case CDOUIDefsPackage.EDITOR_DEF__EDITOR_ID:
      return getEditorID();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case CDOUIDefsPackage.EDITOR_DEF__EDITOR_ID:
      setEditorID((String)newValue);
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
    case CDOUIDefsPackage.EDITOR_DEF__EDITOR_ID:
      setEditorID(EDITOR_ID_EDEFAULT);
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
    case CDOUIDefsPackage.EDITOR_DEF__EDITOR_ID:
      return EDITOR_ID_EDEFAULT == null ? editorID != null : !EDITOR_ID_EDEFAULT.equals(editorID);
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
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
    result.append(" (editorID: ");
    result.append(editorID);
    result.append(')');
    return result.toString();
  }

  @Override
  protected Object createInstance()
  {
    throw new UnsupportedOperationException("not implemented yet!");
  }

  @Override
  protected void validateDefinition()
  {
    CheckUtil.checkState(getEditorID() != null && getEditorID().length() >= 0, "editor id not set!");
  }
} // EditorDefImpl
