/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.impl;

import org.eclipse.emf.cdo.releng.setup.Branch;
import org.eclipse.emf.cdo.releng.setup.Configuration;
import org.eclipse.emf.cdo.releng.setup.Project;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.TopLevelElement;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Project</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.ProjectImpl#getToolVersion <em>Tool Version</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.ProjectImpl#getConfiguration <em>Configuration</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.ProjectImpl#getBranches <em>Branches</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.ProjectImpl#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ProjectImpl extends ConfigurableItemImpl implements Project
{
  /**
   * The default value of the '{@link #getToolVersion() <em>Tool Version</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getToolVersion()
   * @generated
   * @ordered
   */
  protected static final int TOOL_VERSION_EDEFAULT = 0;

  /**
   * The cached value of the '{@link #getToolVersion() <em>Tool Version</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getToolVersion()
   * @generated
   * @ordered
   */
  protected int toolVersion = TOOL_VERSION_EDEFAULT;

  /**
   * The cached value of the '{@link #getBranches() <em>Branches</em>}' containment reference list.
   * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
   * @see #getBranches()
   * @generated
   * @ordered
   */
  protected EList<Branch> branches;

  /**
   * The default value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected static final String NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected String name = NAME_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
   * @generated
   */
  protected ProjectImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return SetupPackage.Literals.PROJECT;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public int getToolVersion()
  {
    return toolVersion;
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  public void setToolVersion(int newToolVersion)
  {
    int oldToolVersion = toolVersion;
    toolVersion = newToolVersion;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.PROJECT__TOOL_VERSION, oldToolVersion,
          toolVersion));
    }
  }

  /**
   * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
   * @generated
   */
  public Configuration getConfiguration()
  {
    if (eContainerFeatureID() != SetupPackage.PROJECT__CONFIGURATION)
    {
      return null;
    }
    return (Configuration)eContainer();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Configuration basicGetConfiguration()
  {
    if (eContainerFeatureID() != SetupPackage.PROJECT__CONFIGURATION)
    {
      return null;
    }
    return (Configuration)eInternalContainer();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetConfiguration(Configuration newConfiguration, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newConfiguration, SetupPackage.PROJECT__CONFIGURATION, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setConfiguration(Configuration newConfiguration)
  {
    if (newConfiguration != eInternalContainer() || eContainerFeatureID() != SetupPackage.PROJECT__CONFIGURATION
        && newConfiguration != null)
    {
      if (EcoreUtil.isAncestor(this, newConfiguration))
      {
        throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
      }
      NotificationChain msgs = null;
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      if (newConfiguration != null)
      {
        msgs = ((InternalEObject)newConfiguration).eInverseAdd(this, SetupPackage.CONFIGURATION__PROJECTS,
            Configuration.class, msgs);
      }
      msgs = basicSetConfiguration(newConfiguration, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.PROJECT__CONFIGURATION, newConfiguration,
          newConfiguration));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Branch> getBranches()
  {
    if (branches == null)
    {
      branches = new EObjectContainmentWithInverseEList.Resolving<Branch>(Branch.class, this,
          SetupPackage.PROJECT__BRANCHES, SetupPackage.BRANCH__PROJECT);
    }
    return branches;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setName(String newName)
  {
    String oldName = name;
    name = newName;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.PROJECT__NAME, oldName, name));
    }
  }

  /**
   * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case SetupPackage.PROJECT__CONFIGURATION:
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      return basicSetConfiguration((Configuration)otherEnd, msgs);
    case SetupPackage.PROJECT__BRANCHES:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getBranches()).basicAdd(otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case SetupPackage.PROJECT__CONFIGURATION:
      return basicSetConfiguration(null, msgs);
    case SetupPackage.PROJECT__BRANCHES:
      return ((InternalEList<?>)getBranches()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs)
  {
    switch (eContainerFeatureID())
    {
    case SetupPackage.PROJECT__CONFIGURATION:
      return eInternalContainer().eInverseRemove(this, SetupPackage.CONFIGURATION__PROJECTS, Configuration.class, msgs);
    }
    return super.eBasicRemoveFromContainerFeature(msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case SetupPackage.PROJECT__TOOL_VERSION:
      return getToolVersion();
    case SetupPackage.PROJECT__CONFIGURATION:
      if (resolve)
      {
        return getConfiguration();
      }
      return basicGetConfiguration();
    case SetupPackage.PROJECT__BRANCHES:
      return getBranches();
    case SetupPackage.PROJECT__NAME:
      return getName();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case SetupPackage.PROJECT__TOOL_VERSION:
      setToolVersion((Integer)newValue);
      return;
    case SetupPackage.PROJECT__CONFIGURATION:
      setConfiguration((Configuration)newValue);
      return;
    case SetupPackage.PROJECT__BRANCHES:
      getBranches().clear();
      getBranches().addAll((Collection<? extends Branch>)newValue);
      return;
    case SetupPackage.PROJECT__NAME:
      setName((String)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case SetupPackage.PROJECT__TOOL_VERSION:
      setToolVersion(TOOL_VERSION_EDEFAULT);
      return;
    case SetupPackage.PROJECT__CONFIGURATION:
      setConfiguration((Configuration)null);
      return;
    case SetupPackage.PROJECT__BRANCHES:
      getBranches().clear();
      return;
    case SetupPackage.PROJECT__NAME:
      setName(NAME_EDEFAULT);
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case SetupPackage.PROJECT__TOOL_VERSION:
      return toolVersion != TOOL_VERSION_EDEFAULT;
    case SetupPackage.PROJECT__CONFIGURATION:
      return basicGetConfiguration() != null;
    case SetupPackage.PROJECT__BRANCHES:
      return branches != null && !branches.isEmpty();
    case SetupPackage.PROJECT__NAME:
      return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass)
  {
    if (baseClass == TopLevelElement.class)
    {
      switch (derivedFeatureID)
      {
      case SetupPackage.PROJECT__TOOL_VERSION:
        return SetupPackage.TOP_LEVEL_ELEMENT__TOOL_VERSION;
      default:
        return -1;
      }
    }
    return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass)
  {
    if (baseClass == TopLevelElement.class)
    {
      switch (baseFeatureID)
      {
      case SetupPackage.TOP_LEVEL_ELEMENT__TOOL_VERSION:
        return SetupPackage.PROJECT__TOOL_VERSION;
      default:
        return -1;
      }
    }
    return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
  }

  /**
   * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
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
    result.append(" (toolVersion: ");
    result.append(toolVersion);
    result.append(", name: ");
    result.append(name);
    result.append(')');
    return result.toString();
  }

} // ProjectImpl
