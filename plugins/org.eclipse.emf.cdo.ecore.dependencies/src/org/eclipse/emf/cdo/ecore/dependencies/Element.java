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

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Element</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.Element#getModel <em>Model</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.Element#isExists <em>Exists</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.Element#getOutgoingLinks <em>Outgoing Links</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.Element#getIncomingLinks <em>Incoming Links</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.ecore.dependencies.Element#getBrokenLinks <em>Broken Links</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.ecore.dependencies.DependenciesPackage#getElement()
 * @model
 * @generated
 */
public interface Element extends Addressable
{
  /**
   * Returns the value of the '<em><b>Model</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.ecore.dependencies.Model#getElements <em>Elements</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Model</em>' container reference.
   * @see #setModel(Model)
   * @see org.eclipse.emf.cdo.ecore.dependencies.DependenciesPackage#getElement_Model()
   * @see org.eclipse.emf.cdo.ecore.dependencies.Model#getElements
   * @model opposite="elements" resolveProxies="false" transient="false"
   * @generated
   */
  Model getModel();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.ecore.dependencies.Element#getModel <em>Model</em>}' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Model</em>' container reference.
   * @see #getModel()
   * @generated
   */
  void setModel(Model value);

  /**
   * Returns the value of the '<em><b>Exists</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Exists</em>' attribute.
   * @see #setExists(boolean)
   * @see org.eclipse.emf.cdo.ecore.dependencies.DependenciesPackage#getElement_Exists()
   * @model
   * @generated
   */
  boolean isExists();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.ecore.dependencies.Element#isExists <em>Exists</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Exists</em>' attribute.
   * @see #isExists()
   * @generated
   */
  void setExists(boolean value);

  /**
   * Returns the value of the '<em><b>Outgoing Links</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.ecore.dependencies.Link}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.ecore.dependencies.Link#getSource <em>Source</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Outgoing Links</em>' containment reference list.
   * @see org.eclipse.emf.cdo.ecore.dependencies.DependenciesPackage#getElement_OutgoingLinks()
   * @see org.eclipse.emf.cdo.ecore.dependencies.Link#getSource
   * @model opposite="source" containment="true"
   * @generated
   */
  EList<Link> getOutgoingLinks();

  /**
   * Returns the value of the '<em><b>Incoming Links</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.ecore.dependencies.Link}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.ecore.dependencies.Link#getTarget <em>Target</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Incoming Links</em>' reference list.
   * @see org.eclipse.emf.cdo.ecore.dependencies.DependenciesPackage#getElement_IncomingLinks()
   * @see org.eclipse.emf.cdo.ecore.dependencies.Link#getTarget
   * @model opposite="target" resolveProxies="false"
   * @generated
   */
  EList<Link> getIncomingLinks();

  /**
   * Returns the value of the '<em><b>Broken Links</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.ecore.dependencies.Link}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Broken Links</em>' reference list.
   * @see org.eclipse.emf.cdo.ecore.dependencies.DependenciesPackage#getElement_BrokenLinks()
   * @model resolveProxies="false" transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  EList<Link> getBrokenLinks();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  boolean hasBrokenLinks();

} // Element
