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
package org.eclipse.emf.cdo.releng.preferences.impl;

import org.eclipse.emf.cdo.releng.preferences.PreferenceItem;
import org.eclipse.emf.cdo.releng.preferences.PreferenceNode;
import org.eclipse.emf.cdo.releng.preferences.PreferencesPackage;
import org.eclipse.emf.cdo.releng.preferences.Property;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import java.lang.reflect.InvocationTargetException;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Preference Item</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.preferences.impl.PreferenceItemImpl#getRoot <em>Root</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.preferences.impl.PreferenceItemImpl#getScope <em>Scope</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.preferences.impl.PreferenceItemImpl#getAbsolutePath <em>Absolute Path</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.preferences.impl.PreferenceItemImpl#getScopeRelativePath <em>Scope Relative Path</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.preferences.impl.PreferenceItemImpl#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class PreferenceItemImpl extends MinimalEObjectImpl.Container implements PreferenceItem
{
  /**
   * The default value of the '{@link #getAbsolutePath() <em>Absolute Path</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAbsolutePath()
   * @generated
   * @ordered
   */
  protected static final String ABSOLUTE_PATH_EDEFAULT = null;

  /**
   * The default value of the '{@link #getScopeRelativePath() <em>Scope Relative Path</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getScopeRelativePath()
   * @generated
   * @ordered
   */
  protected static final String SCOPE_RELATIVE_PATH_EDEFAULT = null;

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
  protected PreferenceItemImpl()
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
    return PreferencesPackage.Literals.PREFERENCE_ITEM;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public PreferenceNode getRoot()
  {
    PreferenceNode parent = getParent();
    if (parent == null)
    {
      if (this instanceof PreferenceNode)
      {
        return (PreferenceNode)this;
      }

      return null;
    }

    return parent.getRoot();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String getAbsolutePath()
  {
    PreferenceNode parent = getParent();
    if (parent == null)
    {
      return getName();
    }

    return parent.getAbsolutePath() + "/" + getName();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String getScopeRelativePath()
  {
    IPath path = new Path(getAbsolutePath()).removeFirstSegments(1).makeRelative();
    return path.toString();
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
      eNotify(new ENotificationImpl(this, Notification.SET, PreferencesPackage.PREFERENCE_ITEM__NAME, oldName, name));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public PreferenceNode getScope()
  {
    PreferenceNode parent = getParent();
    if (parent != null)
    {
      PreferenceNode parent2 = parent.getParent();
      if (parent2 == null)
      {
        return parent;
      }

      return parent.getScope();
    }

    return null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public abstract PreferenceNode getParent();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public PreferenceItem getItem(String path)
  {
    String name;
    int pos = path.indexOf('/');
    if (pos == -1)
    {
      name = path;
      path = null;
    }
    else
    {
      name = path.substring(0, pos);
      path = path.substring(pos + 1);
    }

    if (this instanceof PreferenceNode)
    {
      PreferenceNode preferenceNode = (PreferenceNode)this;

      // TODO Check whether for the same name a node AND a property exists
      for (PreferenceNode node : preferenceNode.getChildren())
      {
        if (name.equals(node.getName()))
        {
          if (path == null)
          {
            return node;
          }

          return node.getNode(path);
        }
      }

      for (Property property : preferenceNode.getProperties())
      {
        if (name.equals(property.getName()))
        {
          if (path == null)
          {
            return property;
          }
        }
      }
    }

    return null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public PreferenceItem getInScope(String scopeName)
  {
    PreferenceNode root = getRoot();
    if (root != null)
    {
      PreferenceNode scope = root.getNode(scopeName);
      if (scope != null)
      {
        return scope.getItem(getScopeRelativePath());
      }
    }

    return null;
  }

  private static final String[] SCOPE_NAMES = { "project", "instance", "default", "configuration", "bundle_defaults" };

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public PreferenceItem getInScope()
  {
    PreferenceNode scope = getScope();
    if (scope != null)
    {
      PreferenceNode root = scope.getRoot();
      String ownScopeName = scope.getName();
      String scopeRelativePath = getScopeRelativePath();
      if (SCOPE_NAMES[0].equals(ownScopeName))
      {
        scopeRelativePath = new Path(scopeRelativePath).removeFirstSegments(1).toString();
      }

      for (int i = 0; i < SCOPE_NAMES.length; i++)
      {
        String scopeName = SCOPE_NAMES[i];
        if (ownScopeName != null)
        {
          if (scopeName.equals(ownScopeName))
          {
            ownScopeName = null;
          }
        }
        else
        {
          PreferenceNode defaultScope = root.getNode(scopeName);
          if (defaultScope != null)
          {
            PreferenceItem item = defaultScope.getItem(scopeRelativePath);
            if (item != null)
            {
              return item;
            }
          }
        }
      }
    }

    return null;
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
    case PreferencesPackage.PREFERENCE_ITEM__ROOT:
      return getRoot();
    case PreferencesPackage.PREFERENCE_ITEM__SCOPE:
      return getScope();
    case PreferencesPackage.PREFERENCE_ITEM__ABSOLUTE_PATH:
      return getAbsolutePath();
    case PreferencesPackage.PREFERENCE_ITEM__SCOPE_RELATIVE_PATH:
      return getScopeRelativePath();
    case PreferencesPackage.PREFERENCE_ITEM__NAME:
      return getName();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case PreferencesPackage.PREFERENCE_ITEM__NAME:
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
    case PreferencesPackage.PREFERENCE_ITEM__NAME:
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
    case PreferencesPackage.PREFERENCE_ITEM__ROOT:
      return getRoot() != null;
    case PreferencesPackage.PREFERENCE_ITEM__SCOPE:
      return getScope() != null;
    case PreferencesPackage.PREFERENCE_ITEM__ABSOLUTE_PATH:
      return ABSOLUTE_PATH_EDEFAULT == null ? getAbsolutePath() != null : !ABSOLUTE_PATH_EDEFAULT
          .equals(getAbsolutePath());
    case PreferencesPackage.PREFERENCE_ITEM__SCOPE_RELATIVE_PATH:
      return SCOPE_RELATIVE_PATH_EDEFAULT == null ? getScopeRelativePath() != null : !SCOPE_RELATIVE_PATH_EDEFAULT
          .equals(getScopeRelativePath());
    case PreferencesPackage.PREFERENCE_ITEM__NAME:
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
  public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException
  {
    switch (operationID)
    {
    case PreferencesPackage.PREFERENCE_ITEM___GET_PARENT:
      return getParent();
    case PreferencesPackage.PREFERENCE_ITEM___GET_ITEM__STRING:
      return getItem((String)arguments.get(0));
    case PreferencesPackage.PREFERENCE_ITEM___GET_IN_SCOPE__STRING:
      return getInScope((String)arguments.get(0));
    case PreferencesPackage.PREFERENCE_ITEM___GET_IN_SCOPE:
      return getInScope();
    }
    return super.eInvoke(operationID, arguments);
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
    result.append(" (name: ");
    result.append(name);
    result.append(')');
    return result.toString();
  }

} // PreferenceItemImpl
