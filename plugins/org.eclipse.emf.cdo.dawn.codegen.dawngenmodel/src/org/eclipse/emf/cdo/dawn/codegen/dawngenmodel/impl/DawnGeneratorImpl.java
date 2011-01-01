/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.impl;

import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnEMFGenerator;
import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnGMFGenerator;
import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnGenerator;
import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawngenmodelPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Dawn Generator</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.impl.DawnGeneratorImpl#getEmfFragmentgenerator <em>Emf
 * Fragmentgenerator</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.impl.DawnGeneratorImpl#getGmfFragmentgenerator <em>Gmf
 * Fragmentgenerator</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class DawnGeneratorImpl extends EObjectImpl implements DawnGenerator
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public static final String copyright = "Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n\r\nContributors:\r\n   Martin Fluegge - initial API and implementation";

  /**
   * The cached value of the '{@link #getEmfFragmentgenerator() <em>Emf Fragmentgenerator</em>}' containment reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getEmfFragmentgenerator()
   * @generated
   * @ordered
   */
  protected DawnEMFGenerator emfFragmentgenerator;

  /**
   * The cached value of the '{@link #getGmfFragmentgenerator() <em>Gmf Fragmentgenerator</em>}' containment reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getGmfFragmentgenerator()
   * @generated
   * @ordered
   */
  protected DawnGMFGenerator gmfFragmentgenerator;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected DawnGeneratorImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return DawngenmodelPackage.Literals.DAWN_GENERATOR;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public DawnEMFGenerator getEmfFragmentgenerator()
  {
    return emfFragmentgenerator;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public NotificationChain basicSetEmfFragmentgenerator(DawnEMFGenerator newEmfFragmentgenerator, NotificationChain msgs)
  {
    DawnEMFGenerator oldEmfFragmentgenerator = emfFragmentgenerator;
    emfFragmentgenerator = newEmfFragmentgenerator;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
          DawngenmodelPackage.DAWN_GENERATOR__EMF_FRAGMENTGENERATOR, oldEmfFragmentgenerator, newEmfFragmentgenerator);
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setEmfFragmentgenerator(DawnEMFGenerator newEmfFragmentgenerator)
  {
    if (newEmfFragmentgenerator != emfFragmentgenerator)
    {
      NotificationChain msgs = null;
      if (emfFragmentgenerator != null)
      {
        msgs = ((InternalEObject)emfFragmentgenerator).eInverseRemove(this, EOPPOSITE_FEATURE_BASE
            - DawngenmodelPackage.DAWN_GENERATOR__EMF_FRAGMENTGENERATOR, null, msgs);
      }
      if (newEmfFragmentgenerator != null)
      {
        msgs = ((InternalEObject)newEmfFragmentgenerator).eInverseAdd(this, EOPPOSITE_FEATURE_BASE
            - DawngenmodelPackage.DAWN_GENERATOR__EMF_FRAGMENTGENERATOR, null, msgs);
      }
      msgs = basicSetEmfFragmentgenerator(newEmfFragmentgenerator, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, DawngenmodelPackage.DAWN_GENERATOR__EMF_FRAGMENTGENERATOR,
          newEmfFragmentgenerator, newEmfFragmentgenerator));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public DawnGMFGenerator getGmfFragmentgenerator()
  {
    return gmfFragmentgenerator;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public NotificationChain basicSetGmfFragmentgenerator(DawnGMFGenerator newGmfFragmentgenerator, NotificationChain msgs)
  {
    DawnGMFGenerator oldGmfFragmentgenerator = gmfFragmentgenerator;
    gmfFragmentgenerator = newGmfFragmentgenerator;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
          DawngenmodelPackage.DAWN_GENERATOR__GMF_FRAGMENTGENERATOR, oldGmfFragmentgenerator, newGmfFragmentgenerator);
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setGmfFragmentgenerator(DawnGMFGenerator newGmfFragmentgenerator)
  {
    if (newGmfFragmentgenerator != gmfFragmentgenerator)
    {
      NotificationChain msgs = null;
      if (gmfFragmentgenerator != null)
      {
        msgs = ((InternalEObject)gmfFragmentgenerator).eInverseRemove(this, EOPPOSITE_FEATURE_BASE
            - DawngenmodelPackage.DAWN_GENERATOR__GMF_FRAGMENTGENERATOR, null, msgs);
      }
      if (newGmfFragmentgenerator != null)
      {
        msgs = ((InternalEObject)newGmfFragmentgenerator).eInverseAdd(this, EOPPOSITE_FEATURE_BASE
            - DawngenmodelPackage.DAWN_GENERATOR__GMF_FRAGMENTGENERATOR, null, msgs);
      }
      msgs = basicSetGmfFragmentgenerator(newGmfFragmentgenerator, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, DawngenmodelPackage.DAWN_GENERATOR__GMF_FRAGMENTGENERATOR,
          newGmfFragmentgenerator, newGmfFragmentgenerator));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case DawngenmodelPackage.DAWN_GENERATOR__EMF_FRAGMENTGENERATOR:
      return basicSetEmfFragmentgenerator(null, msgs);
    case DawngenmodelPackage.DAWN_GENERATOR__GMF_FRAGMENTGENERATOR:
      return basicSetGmfFragmentgenerator(null, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case DawngenmodelPackage.DAWN_GENERATOR__EMF_FRAGMENTGENERATOR:
      return getEmfFragmentgenerator();
    case DawngenmodelPackage.DAWN_GENERATOR__GMF_FRAGMENTGENERATOR:
      return getGmfFragmentgenerator();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case DawngenmodelPackage.DAWN_GENERATOR__EMF_FRAGMENTGENERATOR:
      setEmfFragmentgenerator((DawnEMFGenerator)newValue);
      return;
    case DawngenmodelPackage.DAWN_GENERATOR__GMF_FRAGMENTGENERATOR:
      setGmfFragmentgenerator((DawnGMFGenerator)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case DawngenmodelPackage.DAWN_GENERATOR__EMF_FRAGMENTGENERATOR:
      setEmfFragmentgenerator((DawnEMFGenerator)null);
      return;
    case DawngenmodelPackage.DAWN_GENERATOR__GMF_FRAGMENTGENERATOR:
      setGmfFragmentgenerator((DawnGMFGenerator)null);
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case DawngenmodelPackage.DAWN_GENERATOR__EMF_FRAGMENTGENERATOR:
      return emfFragmentgenerator != null;
    case DawngenmodelPackage.DAWN_GENERATOR__GMF_FRAGMENTGENERATOR:
      return gmfFragmentgenerator != null;
    }
    return super.eIsSet(featureID);
  }

} // DawnGeneratorImpl
