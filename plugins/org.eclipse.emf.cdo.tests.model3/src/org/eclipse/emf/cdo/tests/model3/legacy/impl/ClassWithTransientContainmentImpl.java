/*
 * Copyright (c) 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model3.legacy.impl;

import org.eclipse.emf.cdo.tests.model3.ClassWithTransientContainment;
import org.eclipse.emf.cdo.tests.model3.legacy.Model3Package;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Class With Transient Containment</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.ClassWithTransientContainmentImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.ClassWithTransientContainmentImpl#getTransientChild <em>Transient Child</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.ClassWithTransientContainmentImpl#getTransientChildren <em>Transient Children</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.ClassWithTransientContainmentImpl#getPersistentChild <em>Persistent Child</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.ClassWithTransientContainmentImpl#getPersistentChildren <em>Persistent Children</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ClassWithTransientContainmentImpl extends EObjectImpl implements ClassWithTransientContainment
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
   * The cached value of the '{@link #getTransientChild() <em>Transient Child</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTransientChild()
   * @generated
   * @ordered
   */
  protected ClassWithTransientContainment transientChild;

  /**
   * The cached value of the '{@link #getTransientChildren() <em>Transient Children</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTransientChildren()
   * @generated
   * @ordered
   */
  protected EList<ClassWithTransientContainment> transientChildren;

  /**
   * The cached value of the '{@link #getPersistentChild() <em>Persistent Child</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPersistentChild()
   * @generated
   * @ordered
   */
  protected ClassWithTransientContainment persistentChild;

  /**
   * The cached value of the '{@link #getPersistentChildren() <em>Persistent Children</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPersistentChildren()
   * @generated
   * @ordered
   */
  protected EList<ClassWithTransientContainment> persistentChildren;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ClassWithTransientContainmentImpl()
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
    return Model3Package.eINSTANCE.getClassWithTransientContainment();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setName(String newName)
  {
    String oldName = name;
    name = newName;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model3Package.CLASS_WITH_TRANSIENT_CONTAINMENT__NAME, oldName, name));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ClassWithTransientContainment getTransientChild()
  {
    return transientChild;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetTransientChild(ClassWithTransientContainment newTransientChild, NotificationChain msgs)
  {
    ClassWithTransientContainment oldTransientChild = transientChild;
    transientChild = newTransientChild;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, Model3Package.CLASS_WITH_TRANSIENT_CONTAINMENT__TRANSIENT_CHILD,
          oldTransientChild, newTransientChild);
      if (msgs == null)
      {
        msgs = notification;
      }
      else
      {
        msgs.add(notification);
      }
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setTransientChild(ClassWithTransientContainment newTransientChild)
  {
    if (newTransientChild != transientChild)
    {
      NotificationChain msgs = null;
      if (transientChild != null)
      {
        msgs = ((InternalEObject)transientChild).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - Model3Package.CLASS_WITH_TRANSIENT_CONTAINMENT__TRANSIENT_CHILD,
            null, msgs);
      }
      if (newTransientChild != null)
      {
        msgs = ((InternalEObject)newTransientChild).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - Model3Package.CLASS_WITH_TRANSIENT_CONTAINMENT__TRANSIENT_CHILD,
            null, msgs);
      }
      msgs = basicSetTransientChild(newTransientChild, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(
          new ENotificationImpl(this, Notification.SET, Model3Package.CLASS_WITH_TRANSIENT_CONTAINMENT__TRANSIENT_CHILD, newTransientChild, newTransientChild));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<ClassWithTransientContainment> getTransientChildren()
  {
    if (transientChildren == null)
    {
      transientChildren = new EObjectContainmentEList<>(ClassWithTransientContainment.class, this,
          Model3Package.CLASS_WITH_TRANSIENT_CONTAINMENT__TRANSIENT_CHILDREN);
    }
    return transientChildren;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ClassWithTransientContainment getPersistentChild()
  {
    return persistentChild;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetPersistentChild(ClassWithTransientContainment newPersistentChild, NotificationChain msgs)
  {
    ClassWithTransientContainment oldPersistentChild = persistentChild;
    persistentChild = newPersistentChild;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, Model3Package.CLASS_WITH_TRANSIENT_CONTAINMENT__PERSISTENT_CHILD,
          oldPersistentChild, newPersistentChild);
      if (msgs == null)
      {
        msgs = notification;
      }
      else
      {
        msgs.add(notification);
      }
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setPersistentChild(ClassWithTransientContainment newPersistentChild)
  {
    if (newPersistentChild != persistentChild)
    {
      NotificationChain msgs = null;
      if (persistentChild != null)
      {
        msgs = ((InternalEObject)persistentChild).eInverseRemove(this,
            EOPPOSITE_FEATURE_BASE - Model3Package.CLASS_WITH_TRANSIENT_CONTAINMENT__PERSISTENT_CHILD, null, msgs);
      }
      if (newPersistentChild != null)
      {
        msgs = ((InternalEObject)newPersistentChild).eInverseAdd(this,
            EOPPOSITE_FEATURE_BASE - Model3Package.CLASS_WITH_TRANSIENT_CONTAINMENT__PERSISTENT_CHILD, null, msgs);
      }
      msgs = basicSetPersistentChild(newPersistentChild, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model3Package.CLASS_WITH_TRANSIENT_CONTAINMENT__PERSISTENT_CHILD, newPersistentChild,
          newPersistentChild));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<ClassWithTransientContainment> getPersistentChildren()
  {
    if (persistentChildren == null)
    {
      persistentChildren = new EObjectContainmentEList<>(ClassWithTransientContainment.class, this,
          Model3Package.CLASS_WITH_TRANSIENT_CONTAINMENT__PERSISTENT_CHILDREN);
    }
    return persistentChildren;
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
    case Model3Package.CLASS_WITH_TRANSIENT_CONTAINMENT__TRANSIENT_CHILD:
      return basicSetTransientChild(null, msgs);
    case Model3Package.CLASS_WITH_TRANSIENT_CONTAINMENT__TRANSIENT_CHILDREN:
      return ((InternalEList<?>)getTransientChildren()).basicRemove(otherEnd, msgs);
    case Model3Package.CLASS_WITH_TRANSIENT_CONTAINMENT__PERSISTENT_CHILD:
      return basicSetPersistentChild(null, msgs);
    case Model3Package.CLASS_WITH_TRANSIENT_CONTAINMENT__PERSISTENT_CHILDREN:
      return ((InternalEList<?>)getPersistentChildren()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
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
    case Model3Package.CLASS_WITH_TRANSIENT_CONTAINMENT__NAME:
      return getName();
    case Model3Package.CLASS_WITH_TRANSIENT_CONTAINMENT__TRANSIENT_CHILD:
      return getTransientChild();
    case Model3Package.CLASS_WITH_TRANSIENT_CONTAINMENT__TRANSIENT_CHILDREN:
      return getTransientChildren();
    case Model3Package.CLASS_WITH_TRANSIENT_CONTAINMENT__PERSISTENT_CHILD:
      return getPersistentChild();
    case Model3Package.CLASS_WITH_TRANSIENT_CONTAINMENT__PERSISTENT_CHILDREN:
      return getPersistentChildren();
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
    case Model3Package.CLASS_WITH_TRANSIENT_CONTAINMENT__NAME:
      setName((String)newValue);
      return;
    case Model3Package.CLASS_WITH_TRANSIENT_CONTAINMENT__TRANSIENT_CHILD:
      setTransientChild((ClassWithTransientContainment)newValue);
      return;
    case Model3Package.CLASS_WITH_TRANSIENT_CONTAINMENT__TRANSIENT_CHILDREN:
      getTransientChildren().clear();
      getTransientChildren().addAll((Collection<? extends ClassWithTransientContainment>)newValue);
      return;
    case Model3Package.CLASS_WITH_TRANSIENT_CONTAINMENT__PERSISTENT_CHILD:
      setPersistentChild((ClassWithTransientContainment)newValue);
      return;
    case Model3Package.CLASS_WITH_TRANSIENT_CONTAINMENT__PERSISTENT_CHILDREN:
      getPersistentChildren().clear();
      getPersistentChildren().addAll((Collection<? extends ClassWithTransientContainment>)newValue);
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
    case Model3Package.CLASS_WITH_TRANSIENT_CONTAINMENT__NAME:
      setName(NAME_EDEFAULT);
      return;
    case Model3Package.CLASS_WITH_TRANSIENT_CONTAINMENT__TRANSIENT_CHILD:
      setTransientChild((ClassWithTransientContainment)null);
      return;
    case Model3Package.CLASS_WITH_TRANSIENT_CONTAINMENT__TRANSIENT_CHILDREN:
      getTransientChildren().clear();
      return;
    case Model3Package.CLASS_WITH_TRANSIENT_CONTAINMENT__PERSISTENT_CHILD:
      setPersistentChild((ClassWithTransientContainment)null);
      return;
    case Model3Package.CLASS_WITH_TRANSIENT_CONTAINMENT__PERSISTENT_CHILDREN:
      getPersistentChildren().clear();
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
    case Model3Package.CLASS_WITH_TRANSIENT_CONTAINMENT__NAME:
      return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
    case Model3Package.CLASS_WITH_TRANSIENT_CONTAINMENT__TRANSIENT_CHILD:
      return transientChild != null;
    case Model3Package.CLASS_WITH_TRANSIENT_CONTAINMENT__TRANSIENT_CHILDREN:
      return transientChildren != null && !transientChildren.isEmpty();
    case Model3Package.CLASS_WITH_TRANSIENT_CONTAINMENT__PERSISTENT_CHILD:
      return persistentChild != null;
    case Model3Package.CLASS_WITH_TRANSIENT_CONTAINMENT__PERSISTENT_CHILDREN:
      return persistentChildren != null && !persistentChildren.isEmpty();
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

    StringBuilder result = new StringBuilder(super.toString());
    result.append(" (name: ");
    result.append(name);
    result.append(')');
    return result.toString();
  }

} // ClassWithTransientContainmentImpl
