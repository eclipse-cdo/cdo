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

import org.eclipse.emf.cdo.releng.setup.ContextVariableTask;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.SetupTaskContext;
import org.eclipse.emf.cdo.releng.setup.Trigger;

import org.eclipse.net4j.util.StringUtil;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.IValueVariable;
import org.eclipse.core.variables.VariablesPlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Context Variable Task</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.ContextVariableTaskImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.ContextVariableTaskImpl#getValue <em>Value</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.ContextVariableTaskImpl#isStringSubstitution <em>String Substitution</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ContextVariableTaskImpl extends SetupTaskImpl implements ContextVariableTask
{
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
   * The default value of the '{@link #getValue() <em>Value</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getValue()
   * @generated
   * @ordered
   */
  protected static final String VALUE_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getValue() <em>Value</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getValue()
   * @generated
   * @ordered
   */
  protected String value = VALUE_EDEFAULT;

  /**
   * The default value of the '{@link #isStringSubstitution() <em>String Substitution</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isStringSubstitution()
   * @generated
   * @ordered
   */
  protected static final boolean STRING_SUBSTITUTION_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isStringSubstitution() <em>String Substitution</em>}' attribute.
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @see #isStringSubstitution()
   * @generated
   * @ordered
   */
  protected boolean stringSubstitution = STRING_SUBSTITUTION_EDEFAULT;

  private IValueVariable cachedVariable;

  private String expandedValue;

  /**
   * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
   * @generated
   */
  protected ContextVariableTaskImpl()
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
    return SetupPackage.Literals.CONTEXT_VARIABLE_TASK;
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
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.CONTEXT_VARIABLE_TASK__NAME, oldName, name));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getValue()
  {
    return value;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setValue(String newValue)
  {
    String oldValue = value;
    value = newValue;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.CONTEXT_VARIABLE_TASK__VALUE, oldValue, value));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isStringSubstitution()
  {
    return stringSubstitution;
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  public void setStringSubstitution(boolean newStringSubstitution)
  {
    boolean oldStringSubstitution = stringSubstitution;
    stringSubstitution = newStringSubstitution;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.CONTEXT_VARIABLE_TASK__STRING_SUBSTITUTION,
          oldStringSubstitution, stringSubstitution));
    }
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
    case SetupPackage.CONTEXT_VARIABLE_TASK__NAME:
      return getName();
    case SetupPackage.CONTEXT_VARIABLE_TASK__VALUE:
      return getValue();
    case SetupPackage.CONTEXT_VARIABLE_TASK__STRING_SUBSTITUTION:
      return isStringSubstitution();
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
    case SetupPackage.CONTEXT_VARIABLE_TASK__NAME:
      setName((String)newValue);
      return;
    case SetupPackage.CONTEXT_VARIABLE_TASK__VALUE:
      setValue((String)newValue);
      return;
    case SetupPackage.CONTEXT_VARIABLE_TASK__STRING_SUBSTITUTION:
      setStringSubstitution((Boolean)newValue);
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
    case SetupPackage.CONTEXT_VARIABLE_TASK__NAME:
      setName(NAME_EDEFAULT);
      return;
    case SetupPackage.CONTEXT_VARIABLE_TASK__VALUE:
      setValue(VALUE_EDEFAULT);
      return;
    case SetupPackage.CONTEXT_VARIABLE_TASK__STRING_SUBSTITUTION:
      setStringSubstitution(STRING_SUBSTITUTION_EDEFAULT);
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
    case SetupPackage.CONTEXT_VARIABLE_TASK__NAME:
      return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
    case SetupPackage.CONTEXT_VARIABLE_TASK__VALUE:
      return VALUE_EDEFAULT == null ? value != null : !VALUE_EDEFAULT.equals(value);
    case SetupPackage.CONTEXT_VARIABLE_TASK__STRING_SUBSTITUTION:
      return stringSubstitution != STRING_SUBSTITUTION_EDEFAULT;
    }
    return super.eIsSet(featureID);
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
    result.append(", value: ");
    result.append(value);
    result.append(", stringSubstitution: ");
    result.append(stringSubstitution);
    result.append(')');
    return result.toString();
  }

  public boolean isNeeded(SetupTaskContext context) throws Exception
  {
    if (!isStringSubstitution() || context.getTrigger() == Trigger.BOOTSTRAP)
    {
      String expandedValue = context.expandString(getValue());
      context.put(getName(), expandedValue);
      return false;
    }

    IStringVariableManager manager = VariablesPlugin.getDefault().getStringVariableManager();
    IValueVariable variable = manager.getValueVariable(getName());

    cachedVariable = variable;
    expandedValue = context.expandString(getValue());

    if (variable == null)
    {
      return true;
    }

    if (!expandedValue.equals(variable.getValue()))
    {
      return true;
    }

    if (!StringUtil.safe(getDocumentation()).equals(StringUtil.safe(variable.getDescription())))
    {
      return true;
    }

    return false;
  }

  public void perform(SetupTaskContext context) throws Exception
  {
    context.log("Setting string substitution variable " + getName() + " = " + expandedValue);

    IValueVariable variable = cachedVariable;
    if (variable == null)
    {
      IStringVariableManager manager = VariablesPlugin.getDefault().getStringVariableManager();
      variable = manager.newValueVariable(getName(), null);
      manager.addVariables(new IValueVariable[] { variable });
    }

    variable.setDescription(getDocumentation());
    variable.setValue(expandedValue);
  }

} // ContextVariableTaskImpl
