/*
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.releng.doc.article.impl;

import org.eclipse.emf.cdo.releng.doc.article.ArticlePackage;
import org.eclipse.emf.cdo.releng.doc.article.BodyElement;
import org.eclipse.emf.cdo.releng.doc.article.Category;
import org.eclipse.emf.cdo.releng.doc.article.StructuralElement;
import org.eclipse.emf.cdo.releng.doc.article.util.ArticleUtil;
import org.eclipse.emf.cdo.releng.doc.article.util.HtmlWriter;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

import com.sun.javadoc.PackageDoc;

import java.io.File;
import java.io.IOException;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Category</b></em>'. <!-- end-user-doc -->
 * <p>
 * </p>
 * 
 * @generated
 */
public class CategoryImpl extends BodyImpl implements Category
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected CategoryImpl()
  {
    super();
  }

  CategoryImpl(StructuralElement parent, PackageDoc packageDoc)
  {
    super(parent, ArticleUtil.getSimplePackageName(packageDoc), packageDoc);
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
    return ArticlePackage.Literals.CATEGORY;
  }

  @Override
  protected String getKind()
  {
    return "Category";
  }

  @Override
  public PackageDoc getDoc()
  {
    return (PackageDoc)super.getDoc();
  }

  @Override
  public void generate() throws IOException
  {
    File targetFolder = getOutputFile();
    File sourceFolder = getDoc().position().file().getParentFile();
    for (File file : sourceFolder.listFiles())
    {
      if (file.isFile())
      {
        String name = file.getName();
        if (!name.endsWith(".java") && !name.equals("package-info.java"))
        {
          File targetFile = new File(targetFolder, name);
          ArticleUtil.copyFile(file, targetFile);
        }
      }
    }

    super.generate();
    generate(getTocTarget());
  }

  @Override
  public void generate(HtmlWriter out) throws IOException
  {
    String title = getTitle();
    out.write("<center>\n");
    out.writeHeading(1, title);
    out.write("</center>\n");

    EList<BodyElement> elements = getElements();
    BodyElementContainerImpl.generate(out, this, elements);
  }

  @Override
  protected File getTocTarget()
  {
    return new File(super.getTocTarget(), "summary.html");
  }
} // CategoryImpl
