/*
 * Copyright (c) 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.doc.article.impl;

import org.eclipse.emf.cdo.releng.doc.article.ArticlePackage;
import org.eclipse.emf.cdo.releng.doc.article.ExtensionPoint;
import org.eclipse.emf.cdo.releng.doc.article.Plugin;
import org.eclipse.emf.cdo.releng.doc.article.Schemadoc;
import org.eclipse.emf.cdo.releng.doc.article.StructuralElement;
import org.eclipse.emf.cdo.releng.doc.article.impl.DocumentationImpl.TocWriter;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

import com.sun.javadoc.PackageDoc;

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
  protected void generateTocEntry(TocWriter writer) throws IOException
  {
    boolean exists = false;

    for (Plugin plugin : getDocumentation().getPlugins())
    {
      EList<ExtensionPoint> extensionPoints = plugin.getExtensionPoints();
      if (!extensionPoints.isEmpty())
      {
        if (!exists)
        {
          writer.writeGroupStart(getTitle(), getTocHref(), null);
          exists = true;
        }

        String href = getHref(extensionPoints.get(0));
        writer.writeGroupStart(plugin.getLabel(), href, "plugin");

        for (ExtensionPoint extensionPoint : extensionPoints)
        {
          href = getHref(extensionPoint);
          writer.writeSingle(extensionPoint.getName(), href, "extpoint");
        }

        writer.writeGroupEnd();
      }
    }

    if (exists)
    {
      writer.writeGroupEnd();
    }
  }

  private String getHref(ExtensionPoint extensionPoint)
  {
    String plugin = extensionPoint.getPlugin().getName().replace('.', '_');
    return "schemadoc/" + plugin + "_" + extensionPoint.getName() + ".html";
  }
} // SchemadocImpl
