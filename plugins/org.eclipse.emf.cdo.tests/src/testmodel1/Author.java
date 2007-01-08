/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package testmodel1;


import org.eclipse.emf.cdo.client.CDOPersistent;

import org.eclipse.emf.common.util.EList;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Author</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link testmodel1.Author#getBooks <em>Books</em>}</li>
 *   <li>{@link testmodel1.Author#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @see testmodel1.TestModel1Package#getAuthor()
 * @model
 * @generated
 */
public interface Author extends CDOPersistent
{
  /**
   * Returns the value of the '<em><b>Books</b></em>' reference list.
   * The list contents are of type {@link testmodel1.Book}.
   * It is bidirectional and its opposite is '{@link testmodel1.Book#getAuthor <em>Author</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Books</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Books</em>' reference list.
   * @see testmodel1.TestModel1Package#getAuthor_Books()
   * @see testmodel1.Book#getAuthor
   * @model type="testmodel1.Book" opposite="author"
   * @generated
   */
  EList getBooks();

  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see testmodel1.TestModel1Package#getAuthor_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link testmodel1.Author#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

} // Author