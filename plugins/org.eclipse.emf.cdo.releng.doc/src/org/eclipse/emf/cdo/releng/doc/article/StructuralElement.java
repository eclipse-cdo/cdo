/*
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.releng.doc.article;

import org.eclipse.emf.common.util.EList;

import com.sun.javadoc.Doc;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Structural Element</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.StructuralElement#getChildren <em>Children</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.StructuralElement#getParent <em>Parent</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.StructuralElement#getTitle <em>Title</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.StructuralElement#getDocumentation <em>Documentation</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getStructuralElement()
 * @model abstract="true"
 * @generated
 */
public interface StructuralElement extends LinkTarget
{
  /**
   * Returns the value of the '<em><b>Children</b></em>' containment reference list. The list contents are of type
   * {@link org.eclipse.emf.cdo.releng.doc.article.StructuralElement}. It is bidirectional and its opposite is '
   * {@link org.eclipse.emf.cdo.releng.doc.article.StructuralElement#getParent <em>Parent</em>}'. <!-- begin-user-doc
   * -->
   * <p>
   * If the meaning of the '<em>Children</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Children</em>' containment reference list.
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getStructuralElement_Children()
   * @see org.eclipse.emf.cdo.releng.doc.article.StructuralElement#getParent
   * @model opposite="parent" containment="true"
   * @generated
   */
  EList<StructuralElement> getChildren();

  /**
   * Returns the value of the '<em><b>Parent</b></em>' container reference. It is bidirectional and its opposite is '
   * {@link org.eclipse.emf.cdo.releng.doc.article.StructuralElement#getChildren <em>Children</em>}'. <!--
   * begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Parent</em>' container reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Parent</em>' container reference.
   * @see #setParent(StructuralElement)
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getStructuralElement_Parent()
   * @see org.eclipse.emf.cdo.releng.doc.article.StructuralElement#getChildren
   * @model opposite="children" resolveProxies="false" transient="false"
   * @generated
   */
  StructuralElement getParent();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.doc.article.StructuralElement#getParent <em>Parent</em>}'
   * container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Parent</em>' container reference.
   * @see #getParent()
   * @generated
   */
  void setParent(StructuralElement value);

  /**
   * Returns the value of the '<em><b>Title</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Title</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Title</em>' attribute.
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getStructuralElement_Title()
   * @model required="true" changeable="false"
   * @generated
   */
  String getTitle();

  /**
   * Returns the value of the '<em><b>Documentation</b></em>' reference. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Documentation</em>' reference isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Documentation</em>' reference.
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getStructuralElement_Documentation()
   * @model resolveProxies="false" required="true" transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  Documentation getDocumentation();

  /**
   * Returns the value of the '<em><b>Doc</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Doc</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Doc</em>' attribute.
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getStructuralElement_Doc()
   * @model dataType="org.eclipse.emf.cdo.releng.doc.article.Doc" required="true" transient="true" changeable="false"
   *        volatile="true"
   * @generated
   */
  Doc getDoc();

  String getPath();

  String getFullPath();

  String getBasePathForChildren();

  File getOutputFile();

  File getBaseFolderForChildren();

  int getDepth();

  float getNumber();

  boolean isOverview();

  List<StructuralElement> getSortedChildren();

  void generate() throws IOException;

  void generate(PrintWriter out) throws IOException;

} // StructuralElement
