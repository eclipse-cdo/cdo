/******************************************************************************
 * Copyright (c) 2007, 2009 IBM Corporation and others.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 ****************************************************************************/
package org.eclipse.emf.cdo.gmf.notation.impl;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.HintedDiagramLinkStyle;
import org.eclipse.gmf.runtime.notation.NotationPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Hinted
 * Diagram Link Style</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.HintedDiagramLinkStyleImpl#getHint <em>Hint</em>}</li>
 * </ul>
 *
 * @generated
 */
public class HintedDiagramLinkStyleImpl extends DiagramLinkStyleImpl implements HintedDiagramLinkStyle
{
  /**
   * The default value of the '{@link #getHint() <em>Hint</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getHint()
   * @generated
   * @ordered
   */
  protected static final String HINT_EDEFAULT = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected HintedDiagramLinkStyleImpl()
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
    return NotationPackage.Literals.HINTED_DIAGRAM_LINK_STYLE;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getHint()
  {
    return (String)eDynamicGet(NotationPackage.HINTED_DIAGRAM_LINK_STYLE__HINT, NotationPackage.Literals.HINTED_DIAGRAM_LINK_STYLE__HINT, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setHint(String newHint)
  {
    eDynamicSet(NotationPackage.HINTED_DIAGRAM_LINK_STYLE__HINT, NotationPackage.Literals.HINTED_DIAGRAM_LINK_STYLE__HINT, newHint);
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
    case NotationPackage.HINTED_DIAGRAM_LINK_STYLE__DIAGRAM_LINK:
      if (resolve)
      {
        return getDiagramLink();
      }
      return basicGetDiagramLink();
    case NotationPackage.HINTED_DIAGRAM_LINK_STYLE__HINT:
      return getHint();
    }
    return eDynamicGet(featureID, resolve, coreType);
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
    case NotationPackage.HINTED_DIAGRAM_LINK_STYLE__DIAGRAM_LINK:
      setDiagramLink((Diagram)newValue);
      return;
    case NotationPackage.HINTED_DIAGRAM_LINK_STYLE__HINT:
      setHint((String)newValue);
      return;
    }
    eDynamicSet(featureID, newValue);
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
    case NotationPackage.HINTED_DIAGRAM_LINK_STYLE__DIAGRAM_LINK:
      setDiagramLink((Diagram)null);
      return;
    case NotationPackage.HINTED_DIAGRAM_LINK_STYLE__HINT:
      setHint(HINT_EDEFAULT);
      return;
    }
    eDynamicUnset(featureID);
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
    case NotationPackage.HINTED_DIAGRAM_LINK_STYLE__DIAGRAM_LINK:
      return basicGetDiagramLink() != null;
    case NotationPackage.HINTED_DIAGRAM_LINK_STYLE__HINT:
      return HINT_EDEFAULT == null ? getHint() != null : !HINT_EDEFAULT.equals(getHint());
    }
    return eDynamicIsSet(featureID);
  }

} // HintedDiagramLinkStyleImpl
