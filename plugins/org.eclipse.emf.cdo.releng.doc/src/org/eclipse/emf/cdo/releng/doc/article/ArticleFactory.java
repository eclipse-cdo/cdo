/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.releng.doc.article;

import org.eclipse.emf.ecore.EFactory;

import com.sun.javadoc.RootDoc;

import java.io.File;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each non-abstract class of
 * the model. <!-- end-user-doc -->
 * 
 * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage
 * @generated
 */
public interface ArticleFactory extends EFactory
{
  /**
   * The singleton instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  ArticleFactory eINSTANCE = org.eclipse.emf.cdo.releng.doc.article.impl.ArticleFactoryImpl.init();

  /**
   * Returns a new object of class '<em>Documentation</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Documentation</em>'.
   * @generated
   */
  Documentation createDocumentation();

  /**
   * Returns a new object of class '<em>Context</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Context</em>'.
   * @generated
   */
  Context createContext();

  Context createContext(RootDoc root, File baseFolder, String project);

  /**
   * Returns a new object of class '<em>Category</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Category</em>'.
   * @generated
   */
  Category createCategory();

  /**
   * Returns a new object of class '<em>Article</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Article</em>'.
   * @generated
   */
  Article createArticle();

  /**
   * Returns a new object of class '<em>Chapter</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Chapter</em>'.
   * @generated
   */
  Chapter createChapter();

  /**
   * Returns a new object of class '<em>Snippet</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Snippet</em>'.
   * @generated
   */
  Snippet createSnippet();

  /**
   * Returns a new object of class '<em>Diagram</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Diagram</em>'.
   * @generated
   */
  Diagram createDiagram();

  /**
   * Returns a new object of class '<em>Factory</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Factory</em>'.
   * @generated
   */
  Factory createFactory();

  /**
   * Returns a new object of class '<em>Java Element</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Java Element</em>'.
   * @generated
   */
  JavaElement createJavaElement();

  /**
   * Returns a new object of class '<em>Callout</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Callout</em>'.
   * @generated
   */
  Callout createCallout();

  /**
   * Returns a new object of class '<em>External Target</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>External Target</em>'.
   * @generated
   */
  ExternalTarget createExternalTarget();

  /**
   * Returns a new object of class '<em>Text</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Text</em>'.
   * @generated
   */
  Text createText();

  /**
   * Returns a new object of class '<em>Link</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Link</em>'.
   * @generated
   */
  Link createLink();

  /**
   * Returns a new object of class '<em>Embedding</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Embedding</em>'.
   * @generated
   */
  Embedding createEmbedding();

  /**
   * Returns a new object of class '<em>Source Code</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Source Code</em>'.
   * @generated
   */
  SourceCode createSourceCode();

  /**
   * Returns the package supported by this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the package supported by this factory.
   * @generated
   */
  ArticlePackage getArticlePackage();

} // ArticleFactory
