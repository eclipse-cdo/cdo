/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.releng.doc.article.impl;

import org.eclipse.emf.cdo.releng.doc.article.Article;
import org.eclipse.emf.cdo.releng.doc.article.ArticlePackage;
import org.eclipse.emf.cdo.releng.doc.article.BodyElement;
import org.eclipse.emf.cdo.releng.doc.article.Chapter;
import org.eclipse.emf.cdo.releng.doc.article.StructuralElement;
import org.eclipse.emf.cdo.releng.doc.article.util.HtmlWriter;

import org.eclipse.emf.ecore.EClass;

import com.sun.javadoc.ClassDoc;

import java.io.IOException;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Chapter</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.impl.ChapterImpl#getArticle <em>Article</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class ChapterImpl extends BodyImpl implements Chapter
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected ChapterImpl()
  {
    super();
  }

  ChapterImpl(StructuralElement parent, ClassDoc classDoc)
  {
    super(parent, classDoc.containingClass() == null ? classDoc.simpleTypeName() + ".html" : "#"
        + classDoc.simpleTypeName(), classDoc);
    getDocumentation().getContext().register(getId(), this);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return ArticlePackage.Literals.CHAPTER;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  public Article getArticle()
  {
    if (this instanceof Article)
    {
      return (Article)this;
    }

    return ((Chapter)getParent()).getArticle();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case ArticlePackage.CHAPTER__ARTICLE:
      return getArticle();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case ArticlePackage.CHAPTER__ARTICLE:
      return getArticle() != null;
    }
    return super.eIsSet(featureID);
  }

  @Override
  public ClassDoc getDoc()
  {
    return (ClassDoc)super.getDoc();
  }

  @Override
  public void generate(HtmlWriter out) throws IOException
  {
    ClassDoc classDoc = getDoc();

    out.writeHeading(1, getTitle());
    out.write("<a name=\"");
    out.write(classDoc.typeName());
    out.write("\"/>");

    for (BodyElement element : getElements())
    {
      out.write(element.getHtml());
    }

    out.write("\n\n");
    super.generate(out);
  }

} // ChapterImpl
