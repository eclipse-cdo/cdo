/**
 * <copyright>
 * </copyright>
 *
 * $Id: EtypesPackage.java,v 1.3 2010-11-12 17:37:25 estepper Exp $
 */
package org.eclipse.emf.cdo.etypes;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;

/**
 * <!-- begin-user-doc --> The <b>Package</b> for the model. It contains accessors for the meta objects to represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * 
 * @since 4.0 <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.etypes.EtypesFactory
 * @model kind="package"
 * @generated
 */
public interface EtypesPackage extends EPackage
{
  /**
   * The package name. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNAME = "etypes"; //$NON-NLS-1$

  /**
   * The package namespace URI. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/emf/CDO/Etypes/4.0.0"; //$NON-NLS-1$

  /**
   * The package namespace name. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNS_PREFIX = "etypes"; //$NON-NLS-1$

  /**
   * The singleton instance of the package. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  EtypesPackage eINSTANCE = org.eclipse.emf.cdo.etypes.impl.EtypesPackageImpl.init();

  /**
   * The meta object id for the '<em>Blob</em>' data type. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.common.model.lob.CDOBlob
   * @see org.eclipse.emf.cdo.etypes.impl.EtypesPackageImpl#getBlob()
   * @generated
   */
  int BLOB = 0;

  /**
   * The meta object id for the '<em>Clob</em>' data type. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.common.model.lob.CDOClob
   * @see org.eclipse.emf.cdo.etypes.impl.EtypesPackageImpl#getClob()
   * @generated
   */
  int CLOB = 1;

  /**
   * Returns the meta object for data type '{@link org.eclipse.emf.cdo.common.model.lob.CDOBlob <em>Blob</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for data type '<em>Blob</em>'.
   * @see org.eclipse.emf.cdo.common.model.lob.CDOBlob
   * @model instanceClass="org.eclipse.emf.cdo.etypes.CDOBlob"
   * @generated
   */
  EDataType getBlob();

  /**
   * Returns the meta object for data type '{@link org.eclipse.emf.cdo.common.model.lob.CDOClob <em>Clob</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for data type '<em>Clob</em>'.
   * @see org.eclipse.emf.cdo.common.model.lob.CDOClob
   * @model instanceClass="org.eclipse.emf.cdo.etypes.CDOClob"
   * @generated
   */
  EDataType getClob();

  /**
   * Returns the factory that creates the instances of the model. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the factory that creates the instances of the model.
   * @generated
   */
  EtypesFactory getEtypesFactory();

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
     * The meta object literal for the '<em>Blob</em>' data type. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.common.model.lob.CDOBlob
     * @see org.eclipse.emf.cdo.etypes.impl.EtypesPackageImpl#getBlob()
     * @generated
     */
    EDataType BLOB = eINSTANCE.getBlob();

    /**
     * The meta object literal for the '<em>Clob</em>' data type. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.common.model.lob.CDOClob
     * @see org.eclipse.emf.cdo.etypes.impl.EtypesPackageImpl#getClob()
     * @generated
     */
    EDataType CLOB = eINSTANCE.getClob();

  }

} // EtypesPackage
