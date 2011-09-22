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

import org.eclipse.emf.ecore.EClass;

import com.sun.javadoc.ClassDoc;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

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
    super(parent, makePath(classDoc), classDoc);
    ((ArticleImpl)getArticle()).registerChapter(this);
  }

  private static String makePath(ClassDoc classDoc)
  {
    return classDoc.simpleTypeName() + (classDoc.containingClass() == null ? ".html" : "");
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
  protected String getKind()
  {
    return "Chapter";
  }

  @Override
  protected String createFullPath()
  {
    if (this instanceof Article)
    {
      return super.createFullPath();
    }

    return getArticle().getFullPath() + "#" + getName();
  }

  @Override
  protected void generateTocEntry(TocWriter writer) throws IOException
  {
    if (this instanceof Article)
    {
      super.generateTocEntry(writer);
    }
  }

  @Override
  public void generate(PrintWriter out) throws IOException
  {
    if (this instanceof Article)
    {
      generateHeader(out);
      super.generate(out);
      generateFooter(out);
    }
    else
    {
      String anchor = "<a name=\"" + getName() + "\"/>";

      int level = getChapterNumbers().length + 1;
      out.write("<h" + level + ">");
      out.write(anchor + getTitleWithNumber());
      out.write("</h" + level + ">\n");

      super.generate(out);
    }
  }

  public String getName()
  {
    return getDoc().simpleTypeName();
  }

  public String getTitleWithNumber()
  {
    return getChapterNumber() + "&nbsp;&nbsp;" + getTitle();
  }

  public String getChapterNumber()
  {
    int[] chapterNumber = getChapterNumbers();

    StringBuilder builder = new StringBuilder();
    for (int number : chapterNumber)
    {
      if (builder.length() != 0)
      {
        builder.append(".");
      }

      builder.append(number);
    }

    return builder.toString();
  }

  public int[] getChapterNumbers()
  {
    List<Integer> levelNumbers = new ArrayList<Integer>();
    getLevelNumbers(this, levelNumbers);

    int[] result = new int[levelNumbers.size()];
    for (int i = 0; i < result.length; i++)
    {
      result[i] = levelNumbers.get(i);

    }

    return result;
  }

  private static void getLevelNumbers(ChapterImpl chapter, List<Integer> levelNumbers)
  {
    StructuralElement parent = chapter.getParent();
    if (!(parent instanceof Article))
    {
      getLevelNumbers((ChapterImpl)parent, levelNumbers);
    }

    int number = parent.getSortedChildren().indexOf(chapter) + 1;
    levelNumbers.add(number);
  }
} // ChapterImpl
