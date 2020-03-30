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

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.DiagramLinkStyle;
import org.eclipse.gmf.runtime.notation.NotationPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Diagram
 * Link Style</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.DiagramLinkStyleImpl#getDiagramLink <em>Diagram Link</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DiagramLinkStyleImpl extends CDOObjectImpl implements DiagramLinkStyle
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected DiagramLinkStyleImpl()
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
    return NotationPackage.Literals.DIAGRAM_LINK_STYLE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected int eStaticFeatureCount()
  {
    return 0;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Diagram getDiagramLink()
  {
    return (Diagram)eDynamicGet(NotationPackage.DIAGRAM_LINK_STYLE__DIAGRAM_LINK, NotationPackage.Literals.DIAGRAM_LINK_STYLE__DIAGRAM_LINK, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public Diagram basicGetDiagramLink()
  {
    return (Diagram)eDynamicGet(NotationPackage.DIAGRAM_LINK_STYLE__DIAGRAM_LINK, NotationPackage.Literals.DIAGRAM_LINK_STYLE__DIAGRAM_LINK, false, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setDiagramLink(Diagram newDiagramLink)
  {
    eDynamicSet(NotationPackage.DIAGRAM_LINK_STYLE__DIAGRAM_LINK, NotationPackage.Literals.DIAGRAM_LINK_STYLE__DIAGRAM_LINK, newDiagramLink);
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
    case NotationPackage.DIAGRAM_LINK_STYLE__DIAGRAM_LINK:
      if (resolve)
      {
        return getDiagramLink();
      }
      return basicGetDiagramLink();
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
    case NotationPackage.DIAGRAM_LINK_STYLE__DIAGRAM_LINK:
      setDiagramLink((Diagram)newValue);
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
    case NotationPackage.DIAGRAM_LINK_STYLE__DIAGRAM_LINK:
      setDiagramLink((Diagram)null);
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
    case NotationPackage.DIAGRAM_LINK_STYLE__DIAGRAM_LINK:
      return basicGetDiagramLink() != null;
    }
    return eDynamicIsSet(featureID);
  }

} // DiagramLinkStyleImpl
