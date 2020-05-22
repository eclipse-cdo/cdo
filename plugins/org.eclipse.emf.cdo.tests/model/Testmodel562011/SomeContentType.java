/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package Testmodel562011;

import org.eclipse.emf.cdo.CDOObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Some Content Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link Testmodel562011.SomeContentType#getSomeElem <em>Some Elem</em>}</li>
 * </ul>
 *
 * @see Testmodel562011.Testmodel562011Package#getSomeContentType()
 * @model extendedMetaData="name='SomeContent_._type' kind='elementOnly'"
 * @extends CDOObject
 * @generated
 */
public interface SomeContentType extends CDOObject
{
  /**
   * Returns the value of the '<em><b>Some Elem</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Some Elem</em>' attribute.
   * @see #setSomeElem(String)
   * @see Testmodel562011.Testmodel562011Package#getSomeContentType_SomeElem()
   * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
   *        extendedMetaData="kind='element' name='SomeElem' namespace='##targetNamespace'"
   * @generated
   */
  String getSomeElem();

  /**
   * Sets the value of the '{@link Testmodel562011.SomeContentType#getSomeElem <em>Some Elem</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Some Elem</em>' attribute.
   * @see #getSomeElem()
   * @generated
   */
  void setSomeElem(String value);

} // SomeContentType
