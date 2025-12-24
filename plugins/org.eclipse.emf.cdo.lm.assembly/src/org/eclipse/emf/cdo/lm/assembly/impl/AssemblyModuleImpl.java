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
package org.eclipse.emf.cdo.lm.assembly.impl;

import org.eclipse.emf.cdo.common.branch.CDOBranchPointRef;
import org.eclipse.emf.cdo.etypes.Annotation;
import org.eclipse.emf.cdo.etypes.impl.ModelElementImpl;
import org.eclipse.emf.cdo.lm.assembly.Assembly;
import org.eclipse.emf.cdo.lm.assembly.AssemblyModule;
import org.eclipse.emf.cdo.lm.assembly.AssemblyPackage;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.equinox.p2.metadata.Version;

import java.util.Collection;

/**
 * <!-- begin-user-doc --> An implementation of the model object
 * '<em><b>Module</b></em>'.
 * @noextend This class is not intended to be subclassed by clients.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.assembly.impl.AssemblyModuleImpl#getAssembly <em>Assembly</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.assembly.impl.AssemblyModuleImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.assembly.impl.AssemblyModuleImpl#getVersion <em>Version</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.assembly.impl.AssemblyModuleImpl#getBranchPoint <em>Branch Point</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.assembly.impl.AssemblyModuleImpl#isRoot <em>Root</em>}</li>
 * </ul>
 *
 * @generated
 */
public class AssemblyModuleImpl extends ModelElementImpl implements AssemblyModule
{
  /**
   * The default value of the '{@link #getName() <em>Name</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getName()
   * @generated
   * @ordered
   */
  protected static final String NAME_EDEFAULT = null;

  /**
   * The default value of the '{@link #getVersion() <em>Version</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getVersion()
   * @generated
   * @ordered
   */
  protected static final Version VERSION_EDEFAULT = null;

  /**
   * The default value of the '{@link #getBranchPoint() <em>Branch Point</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getBranchPoint()
   * @generated
   * @ordered
   */
  protected static final CDOBranchPointRef BRANCH_POINT_EDEFAULT = null;

  /**
   * The default value of the '{@link #isRoot() <em>Root</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #isRoot()
   * @generated
   * @ordered
   */
  protected static final boolean ROOT_EDEFAULT = false;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected AssemblyModuleImpl()
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
    return AssemblyPackage.Literals.ASSEMBLY_MODULE;
  }

  @Override
  protected boolean emfToString()
  {
    return true;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Assembly getAssembly()
  {
    return (Assembly)eDynamicGet(AssemblyPackage.ASSEMBLY_MODULE__ASSEMBLY, AssemblyPackage.Literals.ASSEMBLY_MODULE__ASSEMBLY, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetAssembly(Assembly newAssembly, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newAssembly, AssemblyPackage.ASSEMBLY_MODULE__ASSEMBLY, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAssembly(Assembly newAssembly)
  {
    eDynamicSet(AssemblyPackage.ASSEMBLY_MODULE__ASSEMBLY, AssemblyPackage.Literals.ASSEMBLY_MODULE__ASSEMBLY, newAssembly);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getName()
  {
    return (String)eDynamicGet(AssemblyPackage.ASSEMBLY_MODULE__NAME, AssemblyPackage.Literals.ASSEMBLY_MODULE__NAME, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setName(String newName)
  {
    eDynamicSet(AssemblyPackage.ASSEMBLY_MODULE__NAME, AssemblyPackage.Literals.ASSEMBLY_MODULE__NAME, newName);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Version getVersion()
  {
    return (Version)eDynamicGet(AssemblyPackage.ASSEMBLY_MODULE__VERSION, AssemblyPackage.Literals.ASSEMBLY_MODULE__VERSION, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setVersion(Version newVersion)
  {
    eDynamicSet(AssemblyPackage.ASSEMBLY_MODULE__VERSION, AssemblyPackage.Literals.ASSEMBLY_MODULE__VERSION, newVersion);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public CDOBranchPointRef getBranchPoint()
  {
    return (CDOBranchPointRef)eDynamicGet(AssemblyPackage.ASSEMBLY_MODULE__BRANCH_POINT, AssemblyPackage.Literals.ASSEMBLY_MODULE__BRANCH_POINT, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setBranchPoint(CDOBranchPointRef newBranchPoint)
  {
    eDynamicSet(AssemblyPackage.ASSEMBLY_MODULE__BRANCH_POINT, AssemblyPackage.Literals.ASSEMBLY_MODULE__BRANCH_POINT, newBranchPoint);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isRoot()
  {
    return (Boolean)eDynamicGet(AssemblyPackage.ASSEMBLY_MODULE__ROOT, AssemblyPackage.Literals.ASSEMBLY_MODULE__ROOT, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setRoot(boolean newRoot)
  {
    eDynamicSet(AssemblyPackage.ASSEMBLY_MODULE__ROOT, AssemblyPackage.Literals.ASSEMBLY_MODULE__ROOT, newRoot);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case AssemblyPackage.ASSEMBLY_MODULE__ANNOTATIONS:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getAnnotations()).basicAdd(otherEnd, msgs);
    case AssemblyPackage.ASSEMBLY_MODULE__ASSEMBLY:
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      return basicSetAssembly((Assembly)otherEnd, msgs);
    }
    return eDynamicInverseAdd(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case AssemblyPackage.ASSEMBLY_MODULE__ANNOTATIONS:
      return ((InternalEList<?>)getAnnotations()).basicRemove(otherEnd, msgs);
    case AssemblyPackage.ASSEMBLY_MODULE__ASSEMBLY:
      return basicSetAssembly(null, msgs);
    }
    return eDynamicInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs)
  {
    switch (eContainerFeatureID())
    {
    case AssemblyPackage.ASSEMBLY_MODULE__ASSEMBLY:
      return eInternalContainer().eInverseRemove(this, AssemblyPackage.ASSEMBLY__MODULES, Assembly.class, msgs);
    }
    return eDynamicBasicRemoveFromContainer(msgs);
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
    case AssemblyPackage.ASSEMBLY_MODULE__ANNOTATIONS:
      return getAnnotations();
    case AssemblyPackage.ASSEMBLY_MODULE__ASSEMBLY:
      return getAssembly();
    case AssemblyPackage.ASSEMBLY_MODULE__NAME:
      return getName();
    case AssemblyPackage.ASSEMBLY_MODULE__VERSION:
      return getVersion();
    case AssemblyPackage.ASSEMBLY_MODULE__BRANCH_POINT:
      return getBranchPoint();
    case AssemblyPackage.ASSEMBLY_MODULE__ROOT:
      return isRoot();
    }
    return eDynamicGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case AssemblyPackage.ASSEMBLY_MODULE__ANNOTATIONS:
      getAnnotations().clear();
      getAnnotations().addAll((Collection<? extends Annotation>)newValue);
      return;
    case AssemblyPackage.ASSEMBLY_MODULE__ASSEMBLY:
      setAssembly((Assembly)newValue);
      return;
    case AssemblyPackage.ASSEMBLY_MODULE__NAME:
      setName((String)newValue);
      return;
    case AssemblyPackage.ASSEMBLY_MODULE__VERSION:
      setVersion((Version)newValue);
      return;
    case AssemblyPackage.ASSEMBLY_MODULE__BRANCH_POINT:
      setBranchPoint((CDOBranchPointRef)newValue);
      return;
    case AssemblyPackage.ASSEMBLY_MODULE__ROOT:
      setRoot((Boolean)newValue);
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
    case AssemblyPackage.ASSEMBLY_MODULE__ANNOTATIONS:
      getAnnotations().clear();
      return;
    case AssemblyPackage.ASSEMBLY_MODULE__ASSEMBLY:
      setAssembly((Assembly)null);
      return;
    case AssemblyPackage.ASSEMBLY_MODULE__NAME:
      setName(NAME_EDEFAULT);
      return;
    case AssemblyPackage.ASSEMBLY_MODULE__VERSION:
      setVersion(VERSION_EDEFAULT);
      return;
    case AssemblyPackage.ASSEMBLY_MODULE__BRANCH_POINT:
      setBranchPoint(BRANCH_POINT_EDEFAULT);
      return;
    case AssemblyPackage.ASSEMBLY_MODULE__ROOT:
      setRoot(ROOT_EDEFAULT);
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
    case AssemblyPackage.ASSEMBLY_MODULE__ANNOTATIONS:
      return !getAnnotations().isEmpty();
    case AssemblyPackage.ASSEMBLY_MODULE__ASSEMBLY:
      return getAssembly() != null;
    case AssemblyPackage.ASSEMBLY_MODULE__NAME:
      return NAME_EDEFAULT == null ? getName() != null : !NAME_EDEFAULT.equals(getName());
    case AssemblyPackage.ASSEMBLY_MODULE__VERSION:
      return VERSION_EDEFAULT == null ? getVersion() != null : !VERSION_EDEFAULT.equals(getVersion());
    case AssemblyPackage.ASSEMBLY_MODULE__BRANCH_POINT:
      return BRANCH_POINT_EDEFAULT == null ? getBranchPoint() != null : !BRANCH_POINT_EDEFAULT.equals(getBranchPoint());
    case AssemblyPackage.ASSEMBLY_MODULE__ROOT:
      return isRoot() != ROOT_EDEFAULT;
    }
    return eDynamicIsSet(featureID);
  }

  @Override
  public int compareTo(AssemblyModule o)
  {
    return COMPARATOR.compare(this, o);
  }

  @Override
  public String toString()
  {
    return super.toString() + "[" + getName() + " " + getVersion() + "]";
  }

  public static int root(AssemblyModule module)
  {
    return module.isRoot() ? 1 : 0;
  }

} // AssemblyModuleImpl
