/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.releng.doc.article.Image;
import org.eclipse.emf.cdo.releng.doc.article.StructuralElement;
import org.eclipse.emf.cdo.releng.doc.article.util.ArticleUtil;

import org.eclipse.emf.ecore.EClass;

import com.sun.javadoc.Tag;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Image</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.impl.ImageImpl#getFile <em>File</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class ImageImpl extends BodyElementImpl implements Image
{
  /**
   * The default value of the '{@link #getFile() <em>File</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @see #getFile()
   * @generated
   * @ordered
   */
  protected static final File FILE_EDEFAULT = null;

  private File file;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected ImageImpl()
  {
    super();
  }

  public ImageImpl(Tag tag, File file)
  {
    super(tag);
    this.file = file;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return ArticlePackage.Literals.IMAGE;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  public File getFile()
  {
    return file;
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
    case ArticlePackage.IMAGE__FILE:
      return getFile();
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
    case ArticlePackage.IMAGE__FILE:
      return FILE_EDEFAULT == null ? getFile() != null : !FILE_EDEFAULT.equals(getFile());
    }
    return super.eIsSet(featureID);
  }

  public BodyElement copy()
  {
    return new ImageImpl(getTag(), file);
  }

  public void generate(PrintWriter out, StructuralElement linkSource) throws IOException
  {
    File source = linkSource.getDoc().position().file().getParentFile();
    String link = ArticleUtil.createLink(source, file);

    out.write("<p align=\"center\"><img src=\"");
    out.write(link);
    out.write("\"></p>\n");
  }

} // ImageImpl
