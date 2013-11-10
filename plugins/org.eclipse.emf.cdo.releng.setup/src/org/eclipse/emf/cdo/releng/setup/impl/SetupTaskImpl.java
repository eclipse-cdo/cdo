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
import org.eclipse.emf.cdo.releng.setup.ConfigurableItem;
import org.eclipse.emf.cdo.releng.setup.Configuration;
import org.eclipse.emf.cdo.releng.setup.Preferences;
import org.eclipse.emf.cdo.releng.setup.Project;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.SetupTask;
import org.eclipse.emf.cdo.releng.setup.SetupTaskScope;
import org.eclipse.emf.cdo.releng.setup.Trigger;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Task</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.SetupTaskImpl#getRequirements <em>Requirements</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.SetupTaskImpl#getRestrictions <em>Restrictions</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.SetupTaskImpl#isDisabled <em>Disabled</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.SetupTaskImpl#getScope <em>Scope</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.SetupTaskImpl#getExcludedTriggers <em>Excluded Triggers</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.SetupTaskImpl#getDocumentation <em>Documentation</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class SetupTaskImpl extends MinimalEObjectImpl.Container implements SetupTask
{
  /**
   * The cached value of the '{@link #getRequirements() <em>Requirements</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRequirements()
   * @generated
   * @ordered
   */
  protected EList<SetupTask> requirements;

  /**
   * The cached value of the '{@link #getRestrictions() <em>Restrictions</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRestrictions()
   * @generated
   * @ordered
   */
  protected EList<ConfigurableItem> restrictions;

  /**
   * The default value of the '{@link #isDisabled() <em>Disabled</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isDisabled()
   * @generated
   * @ordered
   */
  protected static final boolean DISABLED_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isDisabled() <em>Disabled</em>}' attribute.
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @see #isDisabled()
   * @generated
   * @ordered
   */
  protected boolean disabled = DISABLED_EDEFAULT;

  /**
   * The default value of the '{@link #getScope() <em>Scope</em>}' attribute.
   * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
   * @see #getScope()
   * @generated
   * @ordered
   */
  protected static final SetupTaskScope SCOPE_EDEFAULT = SetupTaskScope.NONE;

  /**
   * The cached value of the '{@link #getExcludedTriggers() <em>Excluded Triggers</em>}' attribute.
   * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
   * @see #getExcludedTriggers()
   * @generated
   * @ordered
   */
  protected Set<Trigger> excludedTriggers;

  /**
   * The default value of the '{@link #getDocumentation() <em>Documentation</em>}' attribute.
   * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
   * @see #getDocumentation()
   * @generated
   * @ordered
   */
  protected static final String DOCUMENTATION_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getDocumentation() <em>Documentation</em>}' attribute.
   * <!-- begin-user-doc -->
    	 * <!-- end-user-doc -->
   * @see #getDocumentation()
   * @generated
   * @ordered
   */
  protected String documentation = DOCUMENTATION_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
   * @generated
   */
  protected SetupTaskImpl()
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
    return SetupPackage.Literals.SETUP_TASK;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<SetupTask> getRequirements()
  {
    if (requirements == null)
    {
      requirements = new EObjectResolvingEList<SetupTask>(SetupTask.class, this, SetupPackage.SETUP_TASK__REQUIREMENTS);
    }
    return requirements;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<ConfigurableItem> getRestrictions()
  {
    if (restrictions == null)
    {
      restrictions = new EObjectResolvingEList<ConfigurableItem>(ConfigurableItem.class, this,
          SetupPackage.SETUP_TASK__RESTRICTIONS);
    }
    return restrictions;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public SetupTaskScope getScope()
  {
    return getScope(this);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Set<Trigger> getExcludedTriggers()
  {
    return excludedTriggers == null ? Collections.<Trigger> emptySet() : excludedTriggers;
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  public void setExcludedTriggersGen(Set<Trigger> newExcludedTriggers)
  {
    Set<Trigger> oldExcludedTriggers = excludedTriggers;
    excludedTriggers = newExcludedTriggers;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.SETUP_TASK__EXCLUDED_TRIGGERS,
          oldExcludedTriggers, excludedTriggers));
    }
  }

  public void setExcludedTriggers(Set<Trigger> newExcludedTriggers)
  {
    setExcludedTriggersGen(newExcludedTriggers == null || newExcludedTriggers.isEmpty() ? null : newExcludedTriggers);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getDocumentation()
  {
    return documentation;
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  public void setDocumentation(String newDocumentation)
  {
    String oldDocumentation = documentation;
    documentation = newDocumentation;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.SETUP_TASK__DOCUMENTATION, oldDocumentation,
          documentation));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isDisabled()
  {
    return disabled;
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  public void setDisabled(boolean newDisabled)
  {
    boolean oldDisabled = disabled;
    disabled = newDisabled;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.SETUP_TASK__DISABLED, oldDisabled, disabled));
    }
  }

  private SetupTaskScope getScope(EObject object)
  {
    if (object instanceof Configuration)
    {
      return SetupTaskScope.ECLIPSE;
    }

    if (object instanceof Project)
    {
      return SetupTaskScope.PROJECT;
    }

    if (object instanceof Branch)
    {
      return SetupTaskScope.BRANCH;
    }

    if (object instanceof Preferences)
    {
      return SetupTaskScope.USER;
    }

    EObject container = object.eContainer();
    if (container == null)
    {
      return SetupTaskScope.NONE;
    }

    return getScope(container);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean requires(SetupTask setupTask)
  {
    Set<SetupTask> visited = new HashSet<SetupTask>();
    return requires(setupTask, visited);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Set<Trigger> getValidTriggers()
  {
    return Trigger.ALL_TRIGGERS;
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated NOT
   */
  public final Set<Trigger> getTriggers()
  {
    Set<Trigger> excludedTriggers = getExcludedTriggers();
    if (excludedTriggers == null || excludedTriggers.isEmpty())
    {
      return getValidTriggers();
    }

    Set<Trigger> result = new HashSet<Trigger>(getValidTriggers());
    result.removeAll(excludedTriggers);
    return Trigger.intern(result);
  }

  private boolean requires(SetupTask setupTask, Set<SetupTask> visited)
  {
    if (visited.add(this))
    {
      if (setupTask == this)
      {
        return true;
      }

      for (SetupTask requirement : getRequirements())
      {
        if (((SetupTaskImpl)requirement).requires(setupTask, visited))
        {
          return true;
        }
      }
    }

    return false;
  }

  protected final Object createToken(String value)
  {
    return new TypedStringToken(eClass(), value);
  }

  /**
   * Subclasses may override to indicate that this task overrides another task with the same token.
   * 
   * @see #createToken(String)
   */
  public Object getOverrideToken()
  {
    return this;
  }

  /**
   * Subclasses may override to reset this task to its initial state.
   */
  public void dispose()
  {
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
    case SetupPackage.SETUP_TASK__REQUIREMENTS:
      return getRequirements();
    case SetupPackage.SETUP_TASK__RESTRICTIONS:
      return getRestrictions();
    case SetupPackage.SETUP_TASK__DISABLED:
      return isDisabled();
    case SetupPackage.SETUP_TASK__SCOPE:
      return getScope();
    case SetupPackage.SETUP_TASK__EXCLUDED_TRIGGERS:
      return getExcludedTriggers();
    case SetupPackage.SETUP_TASK__DOCUMENTATION:
      return getDocumentation();
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
    case SetupPackage.SETUP_TASK__REQUIREMENTS:
      getRequirements().clear();
      getRequirements().addAll((Collection<? extends SetupTask>)newValue);
      return;
    case SetupPackage.SETUP_TASK__RESTRICTIONS:
      getRestrictions().clear();
      getRestrictions().addAll((Collection<? extends ConfigurableItem>)newValue);
      return;
    case SetupPackage.SETUP_TASK__DISABLED:
      setDisabled((Boolean)newValue);
      return;
    case SetupPackage.SETUP_TASK__EXCLUDED_TRIGGERS:
      setExcludedTriggers((Set<Trigger>)newValue);
      return;
    case SetupPackage.SETUP_TASK__DOCUMENTATION:
      setDocumentation((String)newValue);
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
    case SetupPackage.SETUP_TASK__REQUIREMENTS:
      getRequirements().clear();
      return;
    case SetupPackage.SETUP_TASK__RESTRICTIONS:
      getRestrictions().clear();
      return;
    case SetupPackage.SETUP_TASK__DISABLED:
      setDisabled(DISABLED_EDEFAULT);
      return;
    case SetupPackage.SETUP_TASK__EXCLUDED_TRIGGERS:
      setExcludedTriggers((Set<Trigger>)null);
      return;
    case SetupPackage.SETUP_TASK__DOCUMENTATION:
      setDocumentation(DOCUMENTATION_EDEFAULT);
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
    case SetupPackage.SETUP_TASK__REQUIREMENTS:
      return requirements != null && !requirements.isEmpty();
    case SetupPackage.SETUP_TASK__RESTRICTIONS:
      return restrictions != null && !restrictions.isEmpty();
    case SetupPackage.SETUP_TASK__DISABLED:
      return disabled != DISABLED_EDEFAULT;
    case SetupPackage.SETUP_TASK__SCOPE:
      return getScope() != SCOPE_EDEFAULT;
    case SetupPackage.SETUP_TASK__EXCLUDED_TRIGGERS:
      return excludedTriggers != null;
    case SetupPackage.SETUP_TASK__DOCUMENTATION:
      return DOCUMENTATION_EDEFAULT == null ? documentation != null : !DOCUMENTATION_EDEFAULT.equals(documentation);
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
    result.append(" (disabled: ");
    result.append(disabled);
    result.append(", excludedTriggers: ");
    result.append(excludedTriggers);
    result.append(", documentation: ");
    result.append(documentation);
    result.append(')');
    return result.toString();
  }

  /**
     * @author Eike Stepper
     */
  protected static final class TypedStringToken
  {
    private final Object type;

    private final String value;

    public TypedStringToken(Object type, String value)
    {
      this.type = type;
      this.value = value;
    }

    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + (type == null ? 0 : type.hashCode());
      result = prime * result + (value == null ? 0 : value.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj)
    {
      if (this == obj)
      {
        return true;
      }

      if (obj == null)
      {
        return false;
      }

      if (getClass() != obj.getClass())
      {
        return false;
      }

      TypedStringToken other = (TypedStringToken)obj;
      if (type == null)
      {
        if (other.type != null)
        {
          return false;
        }
      }
      else if (!type.equals(other.type))
      {
        return false;
      }

      if (value == null)
      {
        if (other.value != null)
        {
          return false;
        }
      }
      else if (!value.equals(other.value))
      {
        return false;
      }

      return true;
    }
  }

} // SetupTaskImpl
