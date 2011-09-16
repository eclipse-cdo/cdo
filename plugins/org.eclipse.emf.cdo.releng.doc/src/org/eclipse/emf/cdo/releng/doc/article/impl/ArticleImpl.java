/*
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.releng.doc.article.impl;

import org.eclipse.emf.cdo.releng.doc.article.Article;
import org.eclipse.emf.cdo.releng.doc.article.ArticlePackage;
import org.eclipse.emf.cdo.releng.doc.article.StructuralElement;

import org.eclipse.emf.ecore.EClass;

import com.sun.javadoc.ClassDoc;

import java.io.IOException;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Article</b></em>'. <!-- end-user-doc -->
 * <p>
 * </p>
 * 
 * @generated
 */
public class ArticleImpl extends ChapterImpl implements Article
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected ArticleImpl()
  {
    super();
  }

  ArticleImpl(StructuralElement parent, ClassDoc classDoc)
  {
    super(parent, classDoc);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return ArticlePackage.Literals.ARTICLE;
  }

  @Override
  protected String getKind()
  {
    return "Article";
  }

  @Override
  public void generate() throws IOException
  {
    generate(getOutputFile());
  }

} // ArticleImpl
