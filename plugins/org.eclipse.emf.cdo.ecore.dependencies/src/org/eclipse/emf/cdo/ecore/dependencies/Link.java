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

import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Unresolved Proxy</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.Link#getSource <em>Source</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.Link#getTarget <em>Target</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.Link#getReference <em>Reference</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.Link#isBroken <em>Broken</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.ecore.dependencies.DependenciesPackage#getLink()
 * @model
 * @generated
 */
public interface Link extends Addressable
{
  /**
   * Returns the value of the '<em><b>Source</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.ecore.dependencies.Element#getOutgoingLinks <em>Outgoing Links</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Source</em>' container reference.
   * @see #setSource(Element)
   * @see org.eclipse.emf.cdo.ecore.dependencies.DependenciesPackage#getLink_Source()
   * @see org.eclipse.emf.cdo.ecore.dependencies.Element#getOutgoingLinks
   * @model opposite="outgoingLinks" resolveProxies="false" required="true" transient="false"
   * @generated
   */
  Element getSource();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.ecore.dependencies.Link#getSource <em>Source</em>}' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Source</em>' container reference.
   * @see #getSource()
   * @generated
   */
  void setSource(Element value);

  /**
   * Returns the value of the '<em><b>Target</b></em>' reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.ecore.dependencies.Element#getIncomingLinks <em>Incoming Links</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Target</em>' reference.
   * @see #setTarget(Element)
   * @see org.eclipse.emf.cdo.ecore.dependencies.DependenciesPackage#getLink_Target()
   * @see org.eclipse.emf.cdo.ecore.dependencies.Element#getIncomingLinks
   * @model opposite="incomingLinks" resolveProxies="false" required="true"
   * @generated
   */
  Element getTarget();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.ecore.dependencies.Link#getTarget <em>Target</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Target</em>' reference.
   * @see #getTarget()
   * @generated
   */
  void setTarget(Element value);

  /**
   * Returns the value of the '<em><b>Reference</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Reference</em>' reference.
   * @see #setReference(EReference)
   * @see org.eclipse.emf.cdo.ecore.dependencies.DependenciesPackage#getLink_Reference()
   * @model required="true"
   * @generated
   */
  EReference getReference();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.ecore.dependencies.Link#getReference <em>Reference</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Reference</em>' reference.
   * @see #getReference()
   * @generated
   */
  void setReference(EReference value);

  /**
   * Returns the value of the '<em><b>Broken</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Broken</em>' attribute.
   * @see org.eclipse.emf.cdo.ecore.dependencies.DependenciesPackage#getLink_Broken()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  boolean isBroken();

} // UnresolvedProxy
