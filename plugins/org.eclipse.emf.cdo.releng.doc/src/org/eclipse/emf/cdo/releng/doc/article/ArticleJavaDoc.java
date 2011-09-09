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
public class ArticleJavaDoc extends JavaDoc
{
  private final List<String> javaDocPaths = new ArrayList<String>();

  public ArticleJavaDoc()
  {
    super(ArticleJavaDoc.class);
  }

  public final ArticleJavaDoc javaDocPath(String javaDocPath)
  {
    javaDocPaths.add(javaDocPath);
    return this;
  }

  public static boolean start(RootDoc root)
  {
    long startTime = System.currentTimeMillis();
    String base = getOption(root, OPTION_BASE_FOLDER);
    String output = getOption(root, OPTION_OUTPUT_PATH);

    try
    {
      Documentation documentation = Documentation.create(root, base, output);
      documentation.accept(new DocumentationGenerator());
    }
    catch (RuntimeException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new RuntimeException(ex);
    }

    long duration = (System.currentTimeMillis() - startTime) / 1000;
    System.out.println("Finished: " + duration + " seconds");
    return true;
  }

  public static void main(String[] args)
  {
    ArticleJavaDoc tutorial = new ArticleJavaDoc();

    tutorial.classPath("org.eclipse.net4j.jvm/bin");
    tutorial.classPath("org.eclipse.net4j.tcp/bin");
    tutorial.classPath("org.eclipse.net4j.http/bin");
    tutorial.classPath("org.eclipse.net4j.http.server/bin");
    tutorial.classPath("org.eclipse.net4j/bin");
    tutorial.classPath("org.eclipse.net4j.util/bin");

    tutorial.sourcePath("org.eclipse.emf.cdo.doc.tutorial/src");

    tutorial.subPackage("connectors");
    tutorial.subPackage("sessions");

    tutorial.start("/develop/ws/cdo/plugins", "org.eclipse.emf.cdo.doc.tutorial/output");
  }
}
