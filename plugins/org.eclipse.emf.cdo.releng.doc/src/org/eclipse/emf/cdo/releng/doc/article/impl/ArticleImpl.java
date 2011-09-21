/*
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.releng.doc.article.impl;

import org.eclipse.emf.cdo.releng.doc.article.Article;
import org.eclipse.emf.cdo.releng.doc.article.ArticlePackage;
import org.eclipse.emf.cdo.releng.doc.article.Chapter;
import org.eclipse.emf.cdo.releng.doc.article.StructuralElement;
import org.eclipse.emf.cdo.releng.doc.article.impl.DocumentationImpl.TocWriter;
import org.eclipse.emf.cdo.releng.doc.article.util.ArticleUtil;

import org.eclipse.emf.ecore.EClass;

import com.sun.javadoc.ClassDoc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Article</b></em>'. <!-- end-user-doc -->
 * <p>
 * </p>
 * 
 * @generated
 */
public class ArticleImpl extends ChapterImpl implements Article
{
  private Map<String, Chapter> chapters = new HashMap<String, Chapter>();

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

  void registerChapter(Chapter chapter)
  {
    if (!(chapter instanceof Article))
    {
      String name = chapter.getName();
      if (chapters.put(name, chapter) != null)
      {
        throw new AssertionError("Ambiguous chapter name: " + ArticleUtil.makeConsoleLink(chapter.getDoc()));
      }
    }
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

  @Override
  protected void generateTocEntry(TocWriter writer) throws IOException
  {
    writer.writeSingle(getTitle(), getTocHref());
  }
} // ArticleImpl
