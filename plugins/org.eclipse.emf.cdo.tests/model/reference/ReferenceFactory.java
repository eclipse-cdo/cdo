/**
 * <copyright>
 * </copyright>
 *
 * $Id: ReferenceFactory.java,v 1.1.2.3 2008-07-16 16:34:52 estepper Exp $
 */
package reference;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each non-abstract class of
 * the model. <!-- end-user-doc -->
 * 
 * @see reference.ReferencePackage
 * @generated
 */
public interface ReferenceFactory extends EFactory
{
  /**
   * The singleton instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  ReferenceFactory eINSTANCE = reference.impl.ReferenceFactoryImpl.init();

  /**
   * Returns a new object of class '<em>Reference</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Reference</em>'.
   * @generated
   */
  Reference createReference();

  /**
   * Returns the package supported by this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the package supported by this factory.
   * @generated
   */
  ReferencePackage getReferencePackage();

} // ReferenceFactory
