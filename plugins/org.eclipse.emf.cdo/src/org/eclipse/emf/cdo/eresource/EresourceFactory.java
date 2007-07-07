/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.eresource;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a
 * create method for each non-abstract class of the model. <!-- end-user-doc -->
 * 
 * @see org.eclipse.emf.cdo.eresource.EresourcePackage
 * @generated
 */
public interface EresourceFactory extends EFactory
{
  /**
   * The singleton instance of the factory. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   */
  EresourceFactory eINSTANCE = org.eclipse.emf.cdo.eresource.impl.EresourceFactoryImpl.init();

  /**
   * Returns a new object of class '<em>CDO Resource</em>'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>CDO Resource</em>'.
   * @generated
   */
  CDOResource createCDOResource();

  /**
   * Returns the package supported by this factory. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @return the package supported by this factory.
   * @generated
   */
  EresourcePackage getEresourcePackage();

} // EresourceFactory
