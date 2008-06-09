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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc --> The <b>Package</b> for the model. It contains accessors for the meta objects to represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * 
 * @see org.eclipse.emf.cdo.eresource.EresourceFactory
 * @model kind="package"
 * @generated
 */
public interface EresourcePackage extends EPackage
{
  /**
   * The package name. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNAME = "eresource";

  /**
   * The package namespace URI. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/emf/CDO/resource/1.0.0";

  /**
   * The package namespace name. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNS_PREFIX = "eresource";

  /**
   * The singleton instance of the package. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  EresourcePackage eINSTANCE = org.eclipse.emf.cdo.eresource.impl.EresourcePackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl <em>CDO Resource</em>}'
   * class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl
   * @see org.eclipse.emf.cdo.eresource.impl.EresourcePackageImpl#getCDOResource()
   * @generated
   */
  int CDO_RESOURCE = 0;

  /**
   * The feature id for the '<em><b>Resource Set</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CDO_RESOURCE__RESOURCE_SET = 0;

  /**
   * The feature id for the '<em><b>URI</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CDO_RESOURCE__URI = 1;

  /**
   * The feature id for the '<em><b>Contents</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CDO_RESOURCE__CONTENTS = 2;

  /**
   * The feature id for the '<em><b>Modified</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CDO_RESOURCE__MODIFIED = 3;

  /**
   * The feature id for the '<em><b>Loaded</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CDO_RESOURCE__LOADED = 4;

  /**
   * The feature id for the '<em><b>Tracking Modification</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int CDO_RESOURCE__TRACKING_MODIFICATION = 5;

  /**
   * The feature id for the '<em><b>Errors</b></em>' attribute list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CDO_RESOURCE__ERRORS = 6;

  /**
   * The feature id for the '<em><b>Warnings</b></em>' attribute list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CDO_RESOURCE__WARNINGS = 7;

  /**
   * The feature id for the '<em><b>Time Stamp</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CDO_RESOURCE__TIME_STAMP = 8;

  /**
   * The feature id for the '<em><b>Path</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CDO_RESOURCE__PATH = 9;

  /**
   * The number of structural features of the '<em>CDO Resource</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int CDO_RESOURCE_FEATURE_COUNT = 10;

  /**
   * The meta object id for the '<em>Resource Set</em>' data type. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.ecore.resource.ResourceSet
   * @see org.eclipse.emf.cdo.eresource.impl.EresourcePackageImpl#getResourceSet()
   * @generated
   */
  int RESOURCE_SET = 1;

  /**
   * The meta object id for the '<em>URI</em>' data type. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.common.util.URI
   * @see org.eclipse.emf.cdo.eresource.impl.EresourcePackageImpl#getURI()
   * @generated
   */
  int URI = 2;

  /**
   * The meta object id for the '<em>Diagnostic</em>' data type. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.ecore.resource.Resource.Diagnostic
   * @see org.eclipse.emf.cdo.eresource.impl.EresourcePackageImpl#getDiagnostic()
   * @generated
   */
  int DIAGNOSTIC = 3;

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.eresource.CDOResource <em>CDO Resource</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>CDO Resource</em>'.
   * @see org.eclipse.emf.cdo.eresource.CDOResource
   * @generated
   */
  EClass getCDOResource();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.eresource.CDOResource#getResourceSet
   * <em>Resource Set</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Resource Set</em>'.
   * @see org.eclipse.emf.cdo.eresource.CDOResource#getResourceSet()
   * @see #getCDOResource()
   * @generated
   */
  EAttribute getCDOResource_ResourceSet();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.eresource.CDOResource#getURI <em>URI</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>URI</em>'.
   * @see org.eclipse.emf.cdo.eresource.CDOResource#getURI()
   * @see #getCDOResource()
   * @generated
   */
  EAttribute getCDOResource_URI();

  /**
   * Returns the meta object for the containment reference list '
   * {@link org.eclipse.emf.cdo.eresource.CDOResource#getContents <em>Contents</em>}'. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @return the meta object for the containment reference list '<em>Contents</em>'.
   * @see org.eclipse.emf.cdo.eresource.CDOResource#getContents()
   * @see #getCDOResource()
   * @generated
   */
  EReference getCDOResource_Contents();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.eresource.CDOResource#isModified
   * <em>Modified</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Modified</em>'.
   * @see org.eclipse.emf.cdo.eresource.CDOResource#isModified()
   * @see #getCDOResource()
   * @generated
   */
  EAttribute getCDOResource_Modified();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.eresource.CDOResource#isLoaded
   * <em>Loaded</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Loaded</em>'.
   * @see org.eclipse.emf.cdo.eresource.CDOResource#isLoaded()
   * @see #getCDOResource()
   * @generated
   */
  EAttribute getCDOResource_Loaded();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.eresource.CDOResource#isTrackingModification
   * <em>Tracking Modification</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Tracking Modification</em>'.
   * @see org.eclipse.emf.cdo.eresource.CDOResource#isTrackingModification()
   * @see #getCDOResource()
   * @generated
   */
  EAttribute getCDOResource_TrackingModification();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.emf.cdo.eresource.CDOResource#getErrors
   * <em>Errors</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute list '<em>Errors</em>'.
   * @see org.eclipse.emf.cdo.eresource.CDOResource#getErrors()
   * @see #getCDOResource()
   * @generated
   */
  EAttribute getCDOResource_Errors();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.emf.cdo.eresource.CDOResource#getWarnings
   * <em>Warnings</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute list '<em>Warnings</em>'.
   * @see org.eclipse.emf.cdo.eresource.CDOResource#getWarnings()
   * @see #getCDOResource()
   * @generated
   */
  EAttribute getCDOResource_Warnings();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.eresource.CDOResource#getTimeStamp
   * <em>Time Stamp</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Time Stamp</em>'.
   * @see org.eclipse.emf.cdo.eresource.CDOResource#getTimeStamp()
   * @see #getCDOResource()
   * @generated
   */
  EAttribute getCDOResource_TimeStamp();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.eresource.CDOResource#getPath <em>Path</em>}
   * '. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Path</em>'.
   * @see org.eclipse.emf.cdo.eresource.CDOResource#getPath()
   * @see #getCDOResource()
   * @generated
   */
  EAttribute getCDOResource_Path();

  /**
   * Returns the meta object for data type '{@link org.eclipse.emf.ecore.resource.ResourceSet <em>Resource Set</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for data type '<em>Resource Set</em>'.
   * @see org.eclipse.emf.ecore.resource.ResourceSet
   * @model instanceClass="org.eclipse.emf.ecore.resource.ResourceSet" serializeable="false"
   * @generated
   */
  EDataType getResourceSet();

  /**
   * Returns the meta object for data type '{@link org.eclipse.emf.common.util.URI <em>URI</em>}'. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @return the meta object for data type '<em>URI</em>'.
   * @see org.eclipse.emf.common.util.URI
   * @model instanceClass="org.eclipse.emf.common.util.URI"
   * @generated
   */
  EDataType getURI();

  /**
   * Returns the meta object for data type '{@link org.eclipse.emf.ecore.resource.Resource.Diagnostic
   * <em>Diagnostic</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for data type '<em>Diagnostic</em>'.
   * @see org.eclipse.emf.ecore.resource.Resource.Diagnostic
   * @model instanceClass="org.eclipse.emf.ecore.resource.Resource.Diagnostic" serializeable="false"
   * @generated
   */
  EDataType getDiagnostic();

  /**
   * Returns the factory that creates the instances of the model. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the factory that creates the instances of the model.
   * @generated
   */
  EresourceFactory getEresourceFactory();

  /**
   * <!-- begin-user-doc --> Defines literals for the meta objects that represent
   * <ul>
   * <li>each class,</li>
   * <li>each feature of each class,</li>
   * <li>each enum,</li>
   * <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   * 
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl <em>CDO Resource</em>}
     * ' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl
     * @see org.eclipse.emf.cdo.eresource.impl.EresourcePackageImpl#getCDOResource()
     * @generated
     */
    EClass CDO_RESOURCE = eINSTANCE.getCDOResource();

    /**
     * The meta object literal for the '<em><b>Resource Set</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute CDO_RESOURCE__RESOURCE_SET = eINSTANCE.getCDOResource_ResourceSet();

    /**
     * The meta object literal for the '<em><b>URI</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute CDO_RESOURCE__URI = eINSTANCE.getCDOResource_URI();

    /**
     * The meta object literal for the '<em><b>Contents</b></em>' containment reference list feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference CDO_RESOURCE__CONTENTS = eINSTANCE.getCDOResource_Contents();

    /**
     * The meta object literal for the '<em><b>Modified</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute CDO_RESOURCE__MODIFIED = eINSTANCE.getCDOResource_Modified();

    /**
     * The meta object literal for the '<em><b>Loaded</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute CDO_RESOURCE__LOADED = eINSTANCE.getCDOResource_Loaded();

    /**
     * The meta object literal for the '<em><b>Tracking Modification</b></em>' attribute feature. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute CDO_RESOURCE__TRACKING_MODIFICATION = eINSTANCE.getCDOResource_TrackingModification();

    /**
     * The meta object literal for the '<em><b>Errors</b></em>' attribute list feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute CDO_RESOURCE__ERRORS = eINSTANCE.getCDOResource_Errors();

    /**
     * The meta object literal for the '<em><b>Warnings</b></em>' attribute list feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute CDO_RESOURCE__WARNINGS = eINSTANCE.getCDOResource_Warnings();

    /**
     * The meta object literal for the '<em><b>Time Stamp</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute CDO_RESOURCE__TIME_STAMP = eINSTANCE.getCDOResource_TimeStamp();

    /**
     * The meta object literal for the '<em><b>Path</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute CDO_RESOURCE__PATH = eINSTANCE.getCDOResource_Path();

    /**
     * The meta object literal for the '<em>Resource Set</em>' data type. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.ecore.resource.ResourceSet
     * @see org.eclipse.emf.cdo.eresource.impl.EresourcePackageImpl#getResourceSet()
     * @generated
     */
    EDataType RESOURCE_SET = eINSTANCE.getResourceSet();

    /**
     * The meta object literal for the '<em>URI</em>' data type. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.common.util.URI
     * @see org.eclipse.emf.cdo.eresource.impl.EresourcePackageImpl#getURI()
     * @generated
     */
    EDataType URI = eINSTANCE.getURI();

    /**
     * The meta object literal for the '<em>Diagnostic</em>' data type. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.ecore.resource.Resource.Diagnostic
     * @see org.eclipse.emf.cdo.eresource.impl.EresourcePackageImpl#getDiagnostic()
     * @generated
     */
    EDataType DIAGNOSTIC = eINSTANCE.getDiagnostic();

  }

} // EresourcePackage
