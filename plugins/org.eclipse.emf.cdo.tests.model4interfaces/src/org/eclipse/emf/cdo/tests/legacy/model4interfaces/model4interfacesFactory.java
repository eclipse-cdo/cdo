/**
 * <copyright>
 * </copyright>
 *
 * $Id: model4interfacesFactory.java,v 1.2 2008-09-18 12:56:27 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.legacy.model4interfaces;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each non-abstract class of
 * the model. <!-- end-user-doc -->
 * 
 * @see org.eclipse.emf.cdo.tests.legacy.model4interfaces.model4interfacesPackage
 * @generated NOT
 */
public interface model4interfacesFactory extends org.eclipse.emf.cdo.tests.model4interfaces.model4interfacesFactory
{
  /**
   * The singleton instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  model4interfacesFactory eINSTANCE = org.eclipse.emf.cdo.tests.legacy.model4interfaces.impl.model4interfacesFactoryImpl
      .init();

  /**
   * Returns the package supported by this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the package supported by this factory.
   * @generated
   */
  model4interfacesPackage getmodel4interfacesPackage();

} // model4interfacesFactory
