/**
 * <copyright>
 * </copyright>
 *
 * $Id: DerivedFactory.java,v 1.1.2.3 2008-07-16 16:34:52 estepper Exp $
 */
package derived;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each non-abstract class of
 * the model. <!-- end-user-doc -->
 * 
 * @see derived.DerivedPackage
 * @generated
 */
public interface DerivedFactory extends EFactory
{
  /**
   * The singleton instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  DerivedFactory eINSTANCE = derived.impl.DerivedFactoryImpl.init();

  /**
   * Returns a new object of class '<em>Class</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Class</em>'.
   * @generated
   */
  DerivedClass createDerivedClass();

  /**
   * Returns the package supported by this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the package supported by this factory.
   * @generated
   */
  DerivedPackage getDerivedPackage();

} // DerivedFactory
