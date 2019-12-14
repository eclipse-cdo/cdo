/*
 * Copyright (c) 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model6.impl;

import org.eclipse.emf.cdo.tests.model6.BaseObject;
import org.eclipse.emf.cdo.tests.model6.G;
import org.eclipse.emf.cdo.tests.model6.Model6Package;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.ArrayList;
import java.util.List;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>G</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.GImpl#getDummy <em>Dummy</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.GImpl#getReference <em>Reference</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.GImpl#getList <em>List</em>}</li>
 * </ul>
 *
 * @generated
 */
public class GImpl extends CDOObjectImpl implements G
{
  /**
   * @ADDED
   */
  private List<Notification> notifications = new ArrayList<>();

  /**
   * @ADDED
   */
  private boolean listModified = false;

  /**
   * @ADDED
   */
  private boolean referenceModified = false;

  /**
   * @ADDED
   */
  private boolean attributeModified = false;

  /**
   * @ADDED
   */
  {
    eAdapters().add(new AdapterImpl()
    {
      @Override
      public void notifyChanged(Notification msg)
      {
        notifications.add(msg);

        EStructuralFeature feature = (EStructuralFeature)msg.getFeature();
        if (feature != null)
        {
          String featureName = feature.getName();
          if ("dummy".equals(featureName))
          {
            attributeModified = true;
          }
          else if ("reference".equals(featureName))
          {
            referenceModified = true;
          }
          else if ("list".equals(featureName))
          {
            listModified = true;
          }
        }
      }
    });
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected GImpl()
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
    return Model6Package.Literals.G;
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
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getDummy()
  {
    return (String)eGet(Model6Package.Literals.G__DUMMY, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setDummy(String newDummy)
  {
    eSet(Model6Package.Literals.G__DUMMY, newDummy);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public BaseObject getReference()
  {
    return (BaseObject)eGet(Model6Package.Literals.G__REFERENCE, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setReference(BaseObject newReference)
  {
    eSet(Model6Package.Literals.G__REFERENCE, newReference);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<BaseObject> getList()
  {
    return (EList<BaseObject>)eGet(Model6Package.Literals.G__LIST, true);
  }

  /**
   * @ADDED
   */
  @Override
  public List<Notification> getNotifications()
  {
    return notifications;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public boolean isAttributeModified()
  {
    return attributeModified;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public boolean isReferenceModified()
  {
    return referenceModified;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public boolean isListModified()
  {
    return listModified;
  }

} // GImpl
