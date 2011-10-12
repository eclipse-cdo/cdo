/*
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.releng.doc.article;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Snippet</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.Snippet#getCallouts <em>Callouts</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.Snippet#getFormatter <em>Formatter</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getSnippet()
 * @model
 * @generated
 */
public interface Snippet extends EmbeddableElement
{
  /**
   * Returns the value of the '<em><b>Callouts</b></em>' containment reference list. The list contents are of type
   * {@link org.eclipse.emf.cdo.releng.doc.article.Callout}. It is bidirectional and its opposite is '
   * {@link org.eclipse.emf.cdo.releng.doc.article.Callout#getSnippet <em>Snippet</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Callouts</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Callouts</em>' containment reference list.
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getSnippet_Callouts()
   * @see org.eclipse.emf.cdo.releng.doc.article.Callout#getSnippet
   * @model opposite="snippet" containment="true"
   * @generated
   */
  EList<Callout> getCallouts();

  /**
   * Returns the value of the '<em><b>Formatter</b></em>' containment reference. It is bidirectional and its opposite is
   * '{@link org.eclipse.emf.cdo.releng.doc.article.Formatter#getSnippet <em>Snippet</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Formatter</em>' containment reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Formatter</em>' containment reference.
   * @see #setFormatter(Formatter)
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getSnippet_Formatter()
   * @see org.eclipse.emf.cdo.releng.doc.article.Formatter#getSnippet
   * @model opposite="snippet" containment="true" required="true"
   * @generated
   */
  Formatter getFormatter();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.doc.article.Snippet#getFormatter <em>Formatter</em>}'
   * containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Formatter</em>' containment reference.
   * @see #getFormatter()
   * @generated
   */
  void setFormatter(Formatter value);

} // Snippet
