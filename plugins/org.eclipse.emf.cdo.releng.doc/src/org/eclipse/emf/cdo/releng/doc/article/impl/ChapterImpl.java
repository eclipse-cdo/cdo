/**
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
import org.eclipse.emf.cdo.releng.doc.article.util.HtmlWriter;

import org.eclipse.emf.ecore.EClass;

import com.sun.javadoc.ClassDoc;

import java.io.IOException;
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
    getDocumentation().getContext().register(getId(), this);
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

    return getArticle().getFullPath() + "#" + getPath();
  }

  @Override
  public void generate(HtmlWriter out) throws IOException
  {
    String title = getTitle();
    if (this instanceof Article)
    {
      out.writeHeading(1, title);
    }
    else
    {
      int[] chapterNumber = getChapterNumber();

      StringBuilder builder = new StringBuilder();
      for (int number : chapterNumber)
      {
        if (builder.length() != 0)
        {
          builder.append(".");
        }

        builder.append(number);
      }

      builder.append(" ");
      builder.append(title);
      String result = builder.toString();
      out.writeHeading(chapterNumber.length + 1, result);
    }

    out.write("<a name=\"");
    out.write(getPath());
    out.write("\"/>");

    super.generate(out);
  }

  public int[] getChapterNumber()
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

    int number = parent.getChildren().indexOf(chapter) + 1;
    levelNumbers.add(number);
  }
} // ChapterImpl
