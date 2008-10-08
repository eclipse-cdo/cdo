/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.eresource;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>CDO Resource</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.eresource.CDOResource#getResourceSet <em>Resource Set</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.eresource.CDOResource#getURI <em>URI</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.eresource.CDOResource#getContents <em>Contents</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.eresource.CDOResource#isModified <em>Modified</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.eresource.CDOResource#isLoaded <em>Loaded</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.eresource.CDOResource#isTrackingModification <em>Tracking Modification</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.eresource.CDOResource#getErrors <em>Errors</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.eresource.CDOResource#getWarnings <em>Warnings</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.eresource.CDOResource#getTimeStamp <em>Time Stamp</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.eresource.CDOResource#getPath <em>Path</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.eresource.EresourcePackage#getCDOResource()
 * @model
 * @extends EresourceObject
 * @generated
 */
public interface CDOResource extends EresourceObject
{
  /**
   * Returns the value of the '<em><b>Resource Set</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Resource Set</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Resource Set</em>' attribute.
   * @see #setResourceSet(ResourceSet)
   * @see org.eclipse.emf.cdo.eresource.EresourcePackage#getCDOResource_ResourceSet()
   * @model dataType="org.eclipse.emf.cdo.eresource.ResourceSet" transient="true"
   * @generated
   */
  ResourceSet getResourceSet();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.eresource.CDOResource#getResourceSet <em>Resource Set</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Resource Set</em>' attribute.
   * @see #getResourceSet()
   * @generated
   */
  void setResourceSet(ResourceSet value);

  /**
   * Returns the value of the '<em><b>URI</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>URI</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>URI</em>' attribute.
   * @see #setURI(URI)
   * @see org.eclipse.emf.cdo.eresource.EresourcePackage#getCDOResource_URI()
   * @model dataType="org.eclipse.emf.cdo.eresource.URI" transient="true"
   * @generated
   */
  URI getURI();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.eresource.CDOResource#getURI <em>URI</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>URI</em>' attribute.
   * @see #getURI()
   * @generated
   */
  void setURI(URI value);

  /**
   * Returns the value of the '<em><b>Contents</b></em>' containment reference list. The list contents are of type
   * {@link org.eclipse.emf.ecore.EObject}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Contents</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Contents</em>' containment reference list.
   * @see org.eclipse.emf.cdo.eresource.EresourcePackage#getCDOResource_Contents()
   * @model containment="true"
   * @generated
   */
  EList<EObject> getContents();

  /**
   * Returns the value of the '<em><b>Modified</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Modified</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Modified</em>' attribute.
   * @see #setModified(boolean)
   * @see org.eclipse.emf.cdo.eresource.EresourcePackage#getCDOResource_Modified()
   * @model transient="true"
   * @generated
   */
  boolean isModified();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.eresource.CDOResource#isModified <em>Modified</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Modified</em>' attribute.
   * @see #isModified()
   * @generated
   */
  void setModified(boolean value);

  /**
   * Returns the value of the '<em><b>Loaded</b></em>' attribute. The default value is <code>"true"</code>. <!--
   * begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Loaded</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Loaded</em>' attribute.
   * @see #setLoaded(boolean)
   * @see org.eclipse.emf.cdo.eresource.EresourcePackage#getCDOResource_Loaded()
   * @model default="true" transient="true"
   * @generated
   */
  boolean isLoaded();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.eresource.CDOResource#isLoaded <em>Loaded</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Loaded</em>' attribute.
   * @see #isLoaded()
   * @generated
   */
  void setLoaded(boolean value);

  /**
   * Returns the value of the '<em><b>Tracking Modification</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Tracking Modification</em>' attribute isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Tracking Modification</em>' attribute.
   * @see #setTrackingModification(boolean)
   * @see org.eclipse.emf.cdo.eresource.EresourcePackage#getCDOResource_TrackingModification()
   * @model transient="true"
   * @generated
   */
  boolean isTrackingModification();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.eresource.CDOResource#isTrackingModification
   * <em>Tracking Modification</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Tracking Modification</em>' attribute.
   * @see #isTrackingModification()
   * @generated
   */
  void setTrackingModification(boolean value);

  /**
   * Returns the value of the '<em><b>Errors</b></em>' attribute list. The list contents are of type
   * {@link org.eclipse.emf.ecore.resource.Resource.Diagnostic}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Errors</em>' attribute list isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Errors</em>' attribute list.
   * @see org.eclipse.emf.cdo.eresource.EresourcePackage#getCDOResource_Errors()
   * @model dataType="org.eclipse.emf.cdo.eresource.Diagnostic" transient="true"
   * @generated
   */
  EList<Diagnostic> getErrors();

  /**
   * Returns the value of the '<em><b>Warnings</b></em>' attribute list. The list contents are of type
   * {@link org.eclipse.emf.ecore.resource.Resource.Diagnostic}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Warnings</em>' attribute list isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Warnings</em>' attribute list.
   * @see org.eclipse.emf.cdo.eresource.EresourcePackage#getCDOResource_Warnings()
   * @model dataType="org.eclipse.emf.cdo.eresource.Diagnostic" transient="true"
   * @generated
   */
  EList<Diagnostic> getWarnings();

  /**
   * Returns the value of the '<em><b>Time Stamp</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Time Stamp</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Time Stamp</em>' attribute.
   * @see #setTimeStamp(long)
   * @see org.eclipse.emf.cdo.eresource.EresourcePackage#getCDOResource_TimeStamp()
   * @model transient="true"
   * @generated
   */
  long getTimeStamp();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.eresource.CDOResource#getTimeStamp <em>Time Stamp</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Time Stamp</em>' attribute.
   * @see #getTimeStamp()
   * @generated
   */
  void setTimeStamp(long value);

  /**
   * Returns the value of the '<em><b>Path</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Path</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Path</em>' attribute.
   * @see #setPath(String)
   * @see org.eclipse.emf.cdo.eresource.EresourcePackage#getCDOResource_Path()
   * @model
   * @generated
   */
  String getPath();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.eresource.CDOResource#getPath <em>Path</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Path</em>' attribute.
   * @see #getPath()
   * @generated
   */
  void setPath(String value);

} // CDOResource
