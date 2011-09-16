/*
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.releng.doc.article.impl;

import org.eclipse.emf.cdo.releng.doc.article.ArticlePackage;
import org.eclipse.emf.cdo.releng.doc.article.ExtensionPoint;
import org.eclipse.emf.cdo.releng.doc.article.Plugin;
import org.eclipse.emf.cdo.releng.doc.article.Schemadoc;
import org.eclipse.emf.cdo.releng.doc.article.StructuralElement;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

import com.sun.javadoc.PackageDoc;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Schemadoc</b></em>'. <!-- end-user-doc -->
 * <p>
 * </p>
 * 
 * @generated
 */
public class SchemadocImpl extends CategoryImpl implements Schemadoc
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected SchemadocImpl()
  {
    super();
  }

  SchemadocImpl(StructuralElement parent, PackageDoc packageDoc)
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
    return ArticlePackage.Literals.SCHEMADOC;
  }

  @Override
  protected void generateTocEntry(BufferedWriter writer, String prefix) throws IOException
  {
    boolean exists = false;

    for (Plugin plugin : getDocumentation().getPlugins())
    {
      EList<ExtensionPoint> extensionPoints = plugin.getExtensionPoints();
      if (!extensionPoints.isEmpty())
      {
        if (!exists)
        {
          writer.write(prefix + "<topic label=\"" + getTitle() + "\" href=\"" + getTocHref() + "\">\n");
          exists = true;
        }

        String href = getHref(extensionPoints.get(0));
        writer.write(prefix + "\t<topic label=\"" + plugin.getLabel() + "\" href=\"" + href + "\">\n");

        for (ExtensionPoint extensionPoint : extensionPoints)
        {
          href = getHref(extensionPoint);
          writer.write(prefix + "\t\t<topic label=\"" + extensionPoint.getName() + "\" href=\"" + href + "\" />\n");
        }

        writer.write(prefix + "\t</topic>\n");
      }
    }

    if (exists)
    {
      writer.write(prefix + "</topic>\n");
    }
  }

  private String getHref(ExtensionPoint extensionPoint)
  {
    String plugin = extensionPoint.getPlugin().getName().replace('.', '_');
    return "schemadoc/" + plugin + "_" + extensionPoint.getName() + ".html";
  }
} // SchemadocImpl
