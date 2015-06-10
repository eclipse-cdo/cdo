/*
 * Copyright (c) 2011, 2012, 2015 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.releng.doc.article.BodyElement;
import org.eclipse.emf.cdo.releng.doc.article.Excel;
import org.eclipse.emf.cdo.releng.doc.article.StructuralElement;

import org.eclipse.emf.ecore.EClass;

import com.sun.javadoc.Tag;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import de.escnet.ExcelTable;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Excel</b></em>'. <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class ExcelImpl extends BodyElementImpl implements Excel
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected ExcelImpl()
  {
    super();
  }

  public ExcelImpl(Tag tag)
  {
    super(tag);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return ArticlePackage.Literals.EXCEL;
  }

  public BodyElement copy()
  {
    return new ExcelImpl(getTag());
  }

  public void generate(PrintWriter out, StructuralElement linkSource) throws IOException
  {
    File folder = getTag().position().file().getParentFile();
    String relativePath = getTag().text();
    String sheetName = null;

    int pos = relativePath.lastIndexOf('#');
    if (pos != -1)
    {
      sheetName = relativePath.substring(pos + 1);
      relativePath = relativePath.substring(0, pos);
    }

    String path = new File(folder, relativePath).getCanonicalPath();

    ExcelTable excelTable = new ExcelTable(path, sheetName);
    excelTable.writeHtml(out);
  }
} // ExcelImpl
