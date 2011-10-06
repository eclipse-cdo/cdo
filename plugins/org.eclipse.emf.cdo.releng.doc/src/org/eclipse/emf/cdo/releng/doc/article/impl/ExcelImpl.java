/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.releng.doc.article.impl;

import org.eclipse.emf.cdo.releng.doc.article.ArticlePackage;
import org.eclipse.emf.cdo.releng.doc.article.Excel;
import org.eclipse.emf.cdo.releng.doc.article.StructuralElement;

import org.eclipse.emf.ecore.EClass;

import com.sun.javadoc.Tag;

import de.escnet.ExcelTable;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

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
