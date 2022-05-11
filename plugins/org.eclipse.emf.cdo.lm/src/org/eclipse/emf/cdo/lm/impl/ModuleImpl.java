/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.impl;

import org.eclipse.emf.cdo.etypes.impl.ModelElementImpl;
import org.eclipse.emf.cdo.lm.Baseline;
import org.eclipse.emf.cdo.lm.LMPackage;
import org.eclipse.emf.cdo.lm.ModuleType;
import org.eclipse.emf.cdo.lm.Stream;

import org.eclipse.net4j.util.StringUtil;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * <!-- begin-user-doc --> An implementation of the model object
 * '<em><b>Module</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.ModuleImpl#getSystem <em>System</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.ModuleImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.ModuleImpl#getType <em>Type</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.ModuleImpl#getStreams <em>Streams</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ModuleImpl extends ModelElementImpl implements org.eclipse.emf.cdo.lm.Module
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected ModuleImpl()
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
    return LMPackage.Literals.MODULE;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public org.eclipse.emf.cdo.lm.System getSystem()
  {
    return (org.eclipse.emf.cdo.lm.System)eDynamicGet(LMPackage.MODULE__SYSTEM, LMPackage.Literals.MODULE__SYSTEM, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetSystem(org.eclipse.emf.cdo.lm.System newSystem, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newSystem, LMPackage.MODULE__SYSTEM, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setSystem(org.eclipse.emf.cdo.lm.System newSystem)
  {
    eDynamicSet(LMPackage.MODULE__SYSTEM, LMPackage.Literals.MODULE__SYSTEM, newSystem);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getName()
  {
    return (String)eDynamicGet(LMPackage.MODULE__NAME, LMPackage.Literals.MODULE__NAME, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setName(String newName)
  {
    eDynamicSet(LMPackage.MODULE__NAME, LMPackage.Literals.MODULE__NAME, newName);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public EList<Stream> getStreams()
  {
    return (EList<Stream>)eDynamicGet(LMPackage.MODULE__STREAMS, LMPackage.Literals.MODULE__STREAMS, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ModuleType getType()
  {
    return (ModuleType)eDynamicGet(LMPackage.MODULE__TYPE, LMPackage.Literals.MODULE__TYPE, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ModuleType basicGetType()
  {
    return (ModuleType)eDynamicGet(LMPackage.MODULE__TYPE, LMPackage.Literals.MODULE__TYPE, false, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setType(ModuleType newType)
  {
    eDynamicSet(LMPackage.MODULE__TYPE, LMPackage.Literals.MODULE__TYPE, newType);
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
    case LMPackage.MODULE__SYSTEM:
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      return basicSetSystem((org.eclipse.emf.cdo.lm.System)otherEnd, msgs);
    case LMPackage.MODULE__STREAMS:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getStreams()).basicAdd(otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
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
    case LMPackage.MODULE__SYSTEM:
      return basicSetSystem(null, msgs);
    case LMPackage.MODULE__STREAMS:
      return ((InternalEList<?>)getStreams()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
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
    case LMPackage.MODULE__SYSTEM:
      return eInternalContainer().eInverseRemove(this, LMPackage.SYSTEM__MODULES, org.eclipse.emf.cdo.lm.System.class, msgs);
    }
    return super.eBasicRemoveFromContainerFeature(msgs);
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
    case LMPackage.MODULE__SYSTEM:
      return getSystem();
    case LMPackage.MODULE__NAME:
      return getName();
    case LMPackage.MODULE__TYPE:
      if (resolve)
      {
        return getType();
      }
      return basicGetType();
    case LMPackage.MODULE__STREAMS:
      return getStreams();
    }
    return super.eGet(featureID, resolve, coreType);
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
    case LMPackage.MODULE__SYSTEM:
      setSystem((org.eclipse.emf.cdo.lm.System)newValue);
      return;
    case LMPackage.MODULE__NAME:
      setName((String)newValue);
      return;
    case LMPackage.MODULE__TYPE:
      setType((ModuleType)newValue);
      return;
    case LMPackage.MODULE__STREAMS:
      getStreams().clear();
      getStreams().addAll((Collection<? extends Stream>)newValue);
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
    case LMPackage.MODULE__SYSTEM:
      setSystem((org.eclipse.emf.cdo.lm.System)null);
      return;
    case LMPackage.MODULE__NAME:
      setName(NAME_EDEFAULT);
      return;
    case LMPackage.MODULE__TYPE:
      setType((ModuleType)null);
      return;
    case LMPackage.MODULE__STREAMS:
      getStreams().clear();
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
    case LMPackage.MODULE__SYSTEM:
      return getSystem() != null;
    case LMPackage.MODULE__NAME:
      return NAME_EDEFAULT == null ? getName() != null : !NAME_EDEFAULT.equals(getName());
    case LMPackage.MODULE__TYPE:
      return basicGetType() != null;
    case LMPackage.MODULE__STREAMS:
      return !getStreams().isEmpty();
    }
    return super.eIsSet(featureID);
  }

  @Override
  public Stream getStream(int majorVersion, int minorVersion)
  {
    for (Stream stream : getStreams())
    {
      if (stream.getMajorVersion() == majorVersion && stream.getMinorVersion() == minorVersion)
      {
        return stream;
      }
    }

    return null;
  }

  @Override
  public Stream getStream(String codeName)
  {
    for (Stream stream : getStreams())
    {
      if (Objects.equals(stream.getCodeName(), codeName))
      {
        return stream;
      }
    }

    return null;
  }

  @Override
  @SuppressWarnings("resource")
  public java.util.stream.Stream<Baseline> getAllBaselines()
  {
    java.util.stream.Stream<Baseline> result = null;

    for (Stream stream : getStreams())
    {
      java.util.stream.Stream<Baseline> element = stream.getContents().stream();
      if (result == null)
      {
        result = element;
      }
      else
      {
        result = java.util.stream.Stream.concat(result, element);
      }
    }

    return result;
  }

  @Override
  public void forEachBaseline(Consumer<Baseline> consumer)
  {
    for (Stream stream : getStreams())
    {
      stream.forEachBaseline(consumer);
    }
  }

  public static String name(Object o)
  {
    if (o instanceof org.eclipse.emf.cdo.lm.Module)
    {
      return StringUtil.safe(((org.eclipse.emf.cdo.lm.Module)o).getName());
    }

    return StringUtil.EMPTY;
  }

} // ModuleImpl
