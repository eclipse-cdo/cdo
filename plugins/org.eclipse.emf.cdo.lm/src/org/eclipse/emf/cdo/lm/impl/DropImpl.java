/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.impl;

import org.eclipse.emf.cdo.common.branch.CDOBranchPointRef;
import org.eclipse.emf.cdo.etypes.EtypesFactory;
import org.eclipse.emf.cdo.etypes.EtypesPackage;
import org.eclipse.emf.cdo.lm.Drop;
import org.eclipse.emf.cdo.lm.DropType;
import org.eclipse.emf.cdo.lm.LMPackage;
import org.eclipse.emf.cdo.lm.Stream;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

import java.lang.reflect.InvocationTargetException;

/**
 * <!-- begin-user-doc --> An implementation of the model object
 * '<em><b>Drop</b></em>'.
 * @noextend This class is not intended to be subclassed by clients.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.DropImpl#getType <em>Type</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.DropImpl#getLabel <em>Label</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.DropImpl#getBranchPoint <em>Branch Point</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DropImpl extends FixedBaselineImpl implements Drop
{
  /**
   * The default value of the '{@link #getLabel() <em>Label</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getLabel()
   * @generated
   * @ordered
   */
  protected static final String LABEL_EDEFAULT = null;

  /**
   * The default value of the '{@link #getBranchPoint() <em>Branch Point</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getBranchPoint()
   * @generated
   * @ordered
   */
  protected static final CDOBranchPointRef BRANCH_POINT_EDEFAULT = (CDOBranchPointRef)EtypesFactory.eINSTANCE
      .createFromString(EtypesPackage.eINSTANCE.getBranchPointRef(), "");

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected DropImpl()
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
    return LMPackage.Literals.DROP;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public DropType getType()
  {
    return (DropType)eDynamicGet(LMPackage.DROP__TYPE, LMPackage.Literals.DROP__TYPE, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public DropType basicGetType()
  {
    return (DropType)eDynamicGet(LMPackage.DROP__TYPE, LMPackage.Literals.DROP__TYPE, false, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setType(DropType newType)
  {
    eDynamicSet(LMPackage.DROP__TYPE, LMPackage.Literals.DROP__TYPE, newType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getLabel()
  {
    return (String)eDynamicGet(LMPackage.DROP__LABEL, LMPackage.Literals.DROP__LABEL, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setLabel(String newLabel)
  {
    eDynamicSet(LMPackage.DROP__LABEL, LMPackage.Literals.DROP__LABEL, newLabel);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public CDOBranchPointRef getBranchPoint()
  {
    return (CDOBranchPointRef)eDynamicGet(LMPackage.DROP__BRANCH_POINT, LMPackage.Literals.DROP__BRANCH_POINT, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setBranchPoint(CDOBranchPointRef newBranchPoint)
  {
    eDynamicSet(LMPackage.DROP__BRANCH_POINT, LMPackage.Literals.DROP__BRANCH_POINT, newBranchPoint);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public boolean isRelease()
  {
    DropType type = getType();
    if (type != null)
    {
      return type.isRelease();
    }

    return false;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public EList<Stream> getBasedStreams()
  {
    EList<Stream> result = new BasicEList<>();

    for (Stream stream : getModule().getStreams())
    {
      if (stream.getBase() == this)
      {
        result.add(stream);
      }
    }

    return result;
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
    case LMPackage.DROP__TYPE:
      if (resolve)
      {
        return getType();
      }
      return basicGetType();
    case LMPackage.DROP__LABEL:
      return getLabel();
    case LMPackage.DROP__BRANCH_POINT:
      return getBranchPoint();
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
    case LMPackage.DROP__TYPE:
      setType((DropType)newValue);
      return;
    case LMPackage.DROP__LABEL:
      setLabel((String)newValue);
      return;
    case LMPackage.DROP__BRANCH_POINT:
      setBranchPoint((CDOBranchPointRef)newValue);
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
    case LMPackage.DROP__TYPE:
      setType((DropType)null);
      return;
    case LMPackage.DROP__LABEL:
      setLabel(LABEL_EDEFAULT);
      return;
    case LMPackage.DROP__BRANCH_POINT:
      setBranchPoint(BRANCH_POINT_EDEFAULT);
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
    case LMPackage.DROP__TYPE:
      return basicGetType() != null;
    case LMPackage.DROP__LABEL:
      return LABEL_EDEFAULT == null ? getLabel() != null : !LABEL_EDEFAULT.equals(getLabel());
    case LMPackage.DROP__BRANCH_POINT:
      return BRANCH_POINT_EDEFAULT == null ? getBranchPoint() != null : !BRANCH_POINT_EDEFAULT.equals(getBranchPoint());
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException
  {
    switch (operationID)
    {
    case LMPackage.DROP___IS_RELEASE:
      return isRelease();
    case LMPackage.DROP___GET_BASED_STREAMS:
      return getBasedStreams();
    }
    return super.eInvoke(operationID, arguments);
  }

  @Override
  public String getName()
  {
    return getLabel();
  }

  @Override
  public String getTypeName()
  {
    DropType type = getType();
    if (type != null)
    {
      return type.getName();
    }

    return super.getTypeName();
  }

} // DropImpl
