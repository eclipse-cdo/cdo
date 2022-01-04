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
package org.eclipse.emf.cdo.ecore.dependencies;

import org.eclipse.net4j.util.StringUtil;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

import java.util.Comparator;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Addressable</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.Addressable#getUri <em>Uri</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.ecore.dependencies.DependenciesPackage#getAddressable()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface Addressable extends EObject
{
  public static final Comparator<Addressable> ALPHABETICAL_COMPARATOR = Comparator.comparing(Addressable::getAlphaKey);

  /**
   * Returns the value of the '<em><b>Uri</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Uri</em>' attribute.
   * @see #setUri(URI)
   * @see org.eclipse.emf.cdo.ecore.dependencies.DependenciesPackage#getAddressable_Uri()
   * @model dataType="org.eclipse.emf.cdo.ecore.dependencies.URI" required="true"
   * @generated
   */
  URI getUri();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.ecore.dependencies.Addressable#getUri <em>Uri</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Uri</em>' attribute.
   * @see #getUri()
   * @generated
   */
  void setUri(URI value);

  public static String getAlphaKey(Addressable addressable)
  {
    URI uri = addressable.getUri();
    return uri == null ? StringUtil.EMPTY : uri.toString();
  }
} // Addressable
