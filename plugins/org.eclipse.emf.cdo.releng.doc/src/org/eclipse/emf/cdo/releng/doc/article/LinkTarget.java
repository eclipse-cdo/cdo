/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.releng.doc.article;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Link Target</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.LinkTarget#getLabel <em>Label</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getLinkTarget()
 * @model abstract="true"
 * @generated
 */
public interface LinkTarget extends Identifiable
{
  /**
   * Returns the value of the '<em><b>Label</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Label</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Label</em>' attribute.
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getLinkTarget_Label()
   * @model required="true" transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  String getLabel();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @model required="true" sourceRequired="true"
   * @generated
   */
  String linkFrom(StructuralElement source);

} // LinkTarget
