/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.releng.doc.article.impl;

import org.eclipse.emf.cdo.releng.doc.article.ArticlePackage;
import org.eclipse.emf.cdo.releng.doc.article.JavaPackage;
import org.eclipse.emf.cdo.releng.doc.article.Javadoc;
import org.eclipse.emf.cdo.releng.doc.article.Plugin;
import org.eclipse.emf.cdo.releng.doc.article.StructuralElement;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

import com.sun.javadoc.PackageDoc;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Javadoc</b></em>'. <!-- end-user-doc -->
 * <p>
 * </p>
 * 
 * @generated
 */
public class JavadocImpl extends CategoryImpl implements Javadoc
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected JavadocImpl()
  {
    super();
  }

  JavadocImpl(StructuralElement parent, PackageDoc packageDoc)
  {
    super(parent, packageDoc);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return ArticlePackage.Literals.JAVADOC;
  }

  @Override
  public void generate() throws IOException
  {
    // Do nothing
  }

  @Override
  protected void generateTocEntry(BufferedWriter writer, String prefix) throws IOException
  {
    writer.write(prefix + "<topic label=\"" + getTitle() + "\" href=\"javadoc/overview-summary.html\">\n");

    for (Plugin plugin : getDocumentation().getPlugins())
    {
      EList<JavaPackage> packages = plugin.getPackages();
      String href = getHref(packages.get(0));
      writer.write(prefix + "\t<topic label=\"" + plugin.getLabel() + "\" href=\"" + href + "\">\n");

      for (JavaPackage javaPackage : packages)
      {
        href = getHref(javaPackage);
        writer.write(prefix + "\t\t<topic label=\"" + javaPackage.getName() + "\" href=\"" + href + "\" />\n");
      }

      writer.write(prefix + "\t</topic>\n");
    }

    writer.write(prefix + "</topic>\n");
  }

  private String getHref(JavaPackage javaPackage)
  {
    return "javadoc/" + javaPackage.getName().replace('.', '/') + "/package-summary.html";
  }
} // JavadocImpl
