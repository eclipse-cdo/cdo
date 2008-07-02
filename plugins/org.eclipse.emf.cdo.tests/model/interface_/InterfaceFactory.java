/**
 * <copyright>
 * </copyright>
 *
 * $Id: InterfaceFactory.java,v 1.1 2008-07-02 14:09:49 estepper Exp $
 */
package interface_;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each non-abstract class of
 * the model. <!-- end-user-doc -->
 * 
 * @see interface_.InterfacePackage
 * @generated
 */
public interface InterfaceFactory extends EFactory
{
  /**
   * The singleton instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  InterfaceFactory eINSTANCE = interface_.impl.InterfaceFactoryImpl.init();

  /**
   * Returns the package supported by this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the package supported by this factory.
   * @generated
   */
  InterfacePackage getInterfacePackage();

} // InterfaceFactory
