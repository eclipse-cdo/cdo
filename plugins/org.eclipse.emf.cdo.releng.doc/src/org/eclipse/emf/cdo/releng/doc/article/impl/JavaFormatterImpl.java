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
import org.eclipse.emf.cdo.releng.doc.article.JavaFormatter;

import org.eclipse.emf.ecore.EClass;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.SeeTag;

import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Java Formatter</b></em>'. <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class JavaFormatterImpl extends FormatterImpl implements JavaFormatter
{
  private static Constructor<?> snippet;

  private static Method getHtml;

  static
  {
    try
    {
      Class<?> c = Class.forName("de.escnet.CodeSnippet");
      snippet = c.getConstructor(Doc.class, Map.class);
      getHtml = c.getMethod("getHtml");
    }
    catch (Throwable ex)
    {
      ex.printStackTrace();
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected JavaFormatterImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return ArticlePackage.Literals.JAVA_FORMATTER;
  }

  public String getDefaultTitle(SeeTag embedderTag)
  {
    return embedderTag.text() + ".java";
  }

  public String getTopLeftEditorIcon(String imagePath)
  {
    return imagePath + "editor-top-left-java.png";
  }

  public String getSnippetHtml(PrintWriter out, String id, String title)
  {
    Map<String, Object> options = new HashMap<String, Object>();
    options.put("id", id);
    options.put("title", title);
    options.put("includeSignature", getDoc() instanceof ClassDoc);

    try
    {
      Object instance = snippet.newInstance(getDoc(), options);
      return (String)getHtml.invoke(instance);
    }
    catch (Error ex)
    {
      throw ex;
    }
    catch (RuntimeException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new RuntimeException(ex);
    }
  }

  public String getCalloutMarker()
  {
    return "<font color=\"#3f7f5f\">/*&nbsp;callout&nbsp;*/</font>";
  }

} // JavaFormatterImpl
