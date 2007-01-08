/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package testmodel1;


import org.eclipse.emf.ecore.EFactory;


/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see testmodel1.TestModel1Package
 * @generated
 */
public interface TestModel1Factory extends EFactory
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  TestModel1Factory eINSTANCE = testmodel1.impl.TestModel1FactoryImpl.init();

  /**
   * Returns a new object of class '<em>Tree Node</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Tree Node</em>'.
   * @generated
   */
  TreeNode createTreeNode();

  /**
   * Returns a new object of class '<em>Extended Node</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Extended Node</em>'.
   * @generated
   */
  ExtendedNode createExtendedNode();

  /**
   * Returns a new object of class '<em>Empty Node</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Empty Node</em>'.
   * @generated
   */
  EmptyNode createEmptyNode();

  /**
   * Returns a new object of class '<em>Empty Ref Node</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Empty Ref Node</em>'.
   * @generated
   */
  EmptyRefNode createEmptyRefNode();

  /**
   * Returns a new object of class '<em>Root</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Root</em>'.
   * @generated
   */
  Root createRoot();

  /**
   * Returns a new object of class '<em>Author</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Author</em>'.
   * @generated
   */
  Author createAuthor();

  /**
   * Returns a new object of class '<em>Book</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Book</em>'.
   * @generated
   */
  Book createBook();

  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  TestModel1Package getTestModel1Package();

} //TestModel1Factory
