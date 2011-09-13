/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.releng.doc.article;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Embedding</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.Embedding#getElement <em>Element</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getEmbedding()
 * @model
 * @generated
 */
public interface Embedding extends BodyElement
{
  /**
   * Returns the value of the '<em><b>Element</b></em>' reference. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Element</em>' reference isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Element</em>' reference.
   * @see #setElement(EmbeddableElement)
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getEmbedding_Element()
   * @model resolveProxies="false" required="true"
   * @generated
   */
  EmbeddableElement getElement();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.doc.article.Embedding#getElement <em>Element</em>}'
   * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Element</em>' reference.
   * @see #getElement()
   * @generated
   */
  void setElement(EmbeddableElement value);

} // Embedding
