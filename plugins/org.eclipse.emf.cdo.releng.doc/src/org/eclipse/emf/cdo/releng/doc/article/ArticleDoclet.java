/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.doc.article;

import org.eclipse.emf.cdo.releng.doc.article.util.JavaDoc;

import com.sun.javadoc.RootDoc;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class ArticleDoclet // extends JavaDoc
{
  private final List<String> javaDocPaths = new ArrayList<String>();

  public ArticleDoclet()
  {
    // super(ArticleDoclet.class);
  }

  public final ArticleDoclet javaDocPath(String javaDocPath)
  {
    javaDocPaths.add(javaDocPath);
    return this;
  }

  public static int optionLength(String option)
  {
    if (JavaDoc.OPTION_BASE_FOLDER.equals(option))
    {
      return 2;
    }

    if (JavaDoc.OPTION_OUTPUT_PATH.equals(option))
    {
      return 2;
    }

    // Indicate we don't know about it
    return -1;
  }

  public static boolean start(RootDoc root)
  {
    long startTime = System.currentTimeMillis();
    String base = JavaDoc.getOption(root, JavaDoc.OPTION_BASE_FOLDER);
    String output = JavaDoc.getOption(root, JavaDoc.OPTION_OUTPUT_PATH);

    try
    {
      Documentation documentation = Documentation.create(root, base, output);
      documentation.accept(new DocumentationGenerator());
    }
    catch (Error ex)
    {
      ex.printStackTrace();
      throw ex;
    }
    catch (RuntimeException ex)
    {
      ex.printStackTrace();
      throw ex;
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      throw new RuntimeException(ex);
    }

    long duration = (System.currentTimeMillis() - startTime) / 1000;
    System.out.println("Finished: " + duration + " seconds");
    return true;
  }
}
