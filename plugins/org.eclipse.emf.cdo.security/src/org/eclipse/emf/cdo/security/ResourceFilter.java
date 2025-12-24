/*
 * Copyright (c) 2013, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.security;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Resource Filter</b></em>'.
 * @since 4.3
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.ResourceFilter#getPath <em>Path</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.ResourceFilter#getPatternStyle <em>Pattern Style</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.ResourceFilter#isFolders <em>Folders</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.ResourceFilter#isTextResources <em>Text Resources</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.ResourceFilter#isBinaryResources <em>Binary Resources</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.ResourceFilter#isModelResources <em>Model Resources</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.ResourceFilter#isModelObjects <em>Model Objects</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.ResourceFilter#isIncludeParents <em>Include Parents</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.ResourceFilter#isIncludeRoot <em>Include Root</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.security.SecurityPackage#getResourceFilter()
 * @model
 * @generated
 */
public interface ResourceFilter extends PermissionFilter
{
  /**
   * Returns the value of the '<em><b>Path</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Path</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Path</em>' attribute.
   * @see #setPath(String)
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getResourceFilter_Path()
   * @model
   * @generated
   */
  String getPath();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.security.ResourceFilter#getPath <em>Path</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Path</em>' attribute.
   * @see #getPath()
   * @generated NOT
   */
  ResourceFilter setPath(String value);

  /**
   * Returns the value of the '<em><b>Pattern Style</b></em>' attribute.
   * The default value is <code>"TREE"</code>.
   * The literals are from the enumeration {@link org.eclipse.emf.cdo.security.PatternStyle}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Pattern Style</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Pattern Style</em>' attribute.
   * @see org.eclipse.emf.cdo.security.PatternStyle
   * @see #setPatternStyle(PatternStyle)
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getResourceFilter_PatternStyle()
   * @model default="TREE"
   * @generated
   */
  PatternStyle getPatternStyle();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.security.ResourceFilter#getPatternStyle <em>Pattern Style</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Pattern Style</em>' attribute.
   * @see org.eclipse.emf.cdo.security.PatternStyle
   * @see #getPatternStyle()
   * @generated NOT
   */
  ResourceFilter setPatternStyle(PatternStyle value);

  /**
   * Returns the value of the '<em><b>Folders</b></em>' attribute.
   * The default value is <code>"true"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Folders</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Folders</em>' attribute.
   * @see #setFolders(boolean)
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getResourceFilter_Folders()
   * @model default="true"
   * @generated
   */
  boolean isFolders();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.security.ResourceFilter#isFolders <em>Folders</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Folders</em>' attribute.
   * @see #isFolders()
   * @generated NOT
   */
  ResourceFilter setFolders(boolean value);

  /**
   * Returns the value of the '<em><b>Model Resources</b></em>' attribute.
   * The default value is <code>"true"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Model Resources</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Model Resources</em>' attribute.
   * @see #setModelResources(boolean)
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getResourceFilter_ModelResources()
   * @model default="true"
   * @generated
   */
  boolean isModelResources();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.security.ResourceFilter#isModelResources <em>Model Resources</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Model Resources</em>' attribute.
   * @see #isModelResources()
   * @generated NOT
   */
  ResourceFilter setModelResources(boolean value);

  /**
   * Returns the value of the '<em><b>Model Objects</b></em>' attribute.
   * The default value is <code>"true"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Model Objects</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Model Objects</em>' attribute.
   * @see #setModelObjects(boolean)
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getResourceFilter_ModelObjects()
   * @model default="true"
   * @generated
   */
  boolean isModelObjects();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.security.ResourceFilter#isModelObjects <em>Model Objects</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Model Objects</em>' attribute.
   * @see #isModelObjects()
   * @generated NOT
   */
  ResourceFilter setModelObjects(boolean value);

  /**
   * Returns the value of the '<em><b>Include Parents</b></em>' attribute.
   * The default value is <code>"true"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Include Parents</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Include Parents</em>' attribute.
   * @see #setIncludeParents(boolean)
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getResourceFilter_IncludeParents()
   * @model default="true"
   * @generated
   */
  boolean isIncludeParents();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.security.ResourceFilter#isIncludeParents <em>Include Parents</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Include Parents</em>' attribute.
   * @see #isIncludeParents()
   * @generated NOT
   */
  ResourceFilter setIncludeParents(boolean value);

  /**
   * Returns the value of the '<em><b>Include Root</b></em>' attribute.
   * The default value is <code>"true"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Include Root</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Include Root</em>' attribute.
   * @see #setIncludeRoot(boolean)
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getResourceFilter_IncludeRoot()
   * @model default="true"
   * @generated
   */
  boolean isIncludeRoot();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.security.ResourceFilter#isIncludeRoot <em>Include Root</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Include Root</em>' attribute.
   * @see #isIncludeRoot()
   * @generated NOT
   */
  ResourceFilter setIncludeRoot(boolean value);

  /**
   * Returns the value of the '<em><b>Text Resources</b></em>' attribute.
   * The default value is <code>"true"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Text Resources</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Text Resources</em>' attribute.
   * @see #setTextResources(boolean)
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getResourceFilter_TextResources()
   * @model default="true"
   * @generated
   */
  boolean isTextResources();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.security.ResourceFilter#isTextResources <em>Text Resources</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Text Resources</em>' attribute.
   * @see #isTextResources()
   * @generated NOT
   */
  ResourceFilter setTextResources(boolean value);

  /**
   * Returns the value of the '<em><b>Binary Resources</b></em>' attribute.
   * The default value is <code>"true"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Binary Resources</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Binary Resources</em>' attribute.
   * @see #setBinaryResources(boolean)
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getResourceFilter_BinaryResources()
   * @model default="true"
   * @generated
   */
  boolean isBinaryResources();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.security.ResourceFilter#isBinaryResources <em>Binary Resources</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Binary Resources</em>' attribute.
   * @see #isBinaryResources()
   * @generated NOT
   */
  ResourceFilter setBinaryResources(boolean value);

} // ResourceFilter
