/**
 * <copyright>
 * </copyright>
 *
 * $Id: MetaRef.java,v 1.2 2008-09-18 12:57:20 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model3;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

/**
 * <!-- begin-user-<!-- begin-user-doc --> A representation of the model object '/b></em>'. <!-- end-'. <!--
 * end-user-doc --> e
 * <p>
 * The following features are supported: >{@link or <li>se.emf.cdo.tests.model3.MetaRef#getEPackageRef <em>EPackage Ref
 * </em>}</li> </ul>
 * </p>
 * 
 * @see org.ec se.emf.cdo.tests.model3.Model3Package#getMetaRef()
 * @model
 * @generated
 */
public interface MetaRef extends EObject
{
  /**
   * Returns the value of the '<em><b>EPackage Ref</b></em>' reference. ' reference. <!-- begin-user-doc --> he meaning
   * of the '<em>EPackage Ref</em>' reference isn'' reference isn't clear, there really should be more of a description
   * here... </p> doc -->
   * 
   * @return the val e of the '<em>EPackage Ref</em>' reference.
   * @see #setEPackageRef(EPackage)
   * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getMetaRef_EPackageRef()
   * @model
   * @generated
   */
  EPackage getEPackageRef();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model3.MetaRef#getEPackageRef <em>EPackage Ref</em>}'
   * reference. ' reference. <!-- begin-user-doc --> <!-- end-user-doc --> h new v value the new value of the '</em>'
   * reference.
   * 
   * @see #getEPackageRef()
   * @generated
   */
  void setEPackageRef(EPackage value);

} // MetaRef
