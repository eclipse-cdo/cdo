/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    
 *  Initial Publication:
 *    Eclipse Magazin - http://www.eclipse-magazin.de
 */
package org.gastro.inventory;

import org.eclipse.emf.cdo.CDOObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Product</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.gastro.inventory.Product#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.gastro.inventory.InventoryPackage#getProduct()
 * @model abstract="true"
 * @extends CDOObject
 * @generated
 */
public interface Product extends CDOObject
{
  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.gastro.inventory.InventoryPackage#getProduct_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.gastro.inventory.Product#getName <em>Name</em>}' attribute. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @model kind="operation"
   * @generated
   */
  float getCost();

  /**
   * TODO Should be modeled as EOperation!
   * 
   * @ADDED
   */
  public Department getDepartment();
} // Product
