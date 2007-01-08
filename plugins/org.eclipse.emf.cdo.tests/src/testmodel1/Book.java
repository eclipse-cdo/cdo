/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package testmodel1;


import org.eclipse.emf.cdo.client.CDOPersistent;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Book</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link testmodel1.Book#getAuthor <em>Author</em>}</li>
 *   <li>{@link testmodel1.Book#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @see testmodel1.TestModel1Package#getBook()
 * @model
 * @generated
 */
public interface Book extends CDOPersistent
{
  /**
   * Returns the value of the '<em><b>Author</b></em>' reference.
   * It is bidirectional and its opposite is '{@link testmodel1.Author#getBooks <em>Books</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Author</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Author</em>' reference.
   * @see #setAuthor(Author)
   * @see testmodel1.TestModel1Package#getBook_Author()
   * @see testmodel1.Author#getBooks
   * @model opposite="books"
   * @generated
   */
  Author getAuthor();

  /**
   * Sets the value of the '{@link testmodel1.Book#getAuthor <em>Author</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Author</em>' reference.
   * @see #getAuthor()
   * @generated
   */
  void setAuthor(Author value);

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
   * @see testmodel1.TestModel1Package#getBook_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link testmodel1.Book#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

} // Book