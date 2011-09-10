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
package org.eclipse.emf.cdo.releng.doc.article.util;

import com.sun.javadoc.Doc;
import com.sun.javadoc.SeeTag;

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class SnippetTag extends TextTag
{
  private static Constructor<?> snippet;

  private static Method write;

  private final Doc snippetDoc;

  private final boolean includeSignature;

  public SnippetTag(SeeTag delegate, Doc snippetDoc, boolean includeSignature)
  {
    super(delegate, null);
    this.snippetDoc = snippetDoc;
    this.includeSignature = includeSignature;
  }

  @Override
  public SeeTag getDelegate()
  {
    return (SeeTag)super.getDelegate();
  }

  @Override
  protected String resolve()
  {
    CharArrayWriter result = new CharArrayWriter();

    PrintWriter out = new PrintWriter(result);
    out.write("\n\n");
    writeExampleSnippet(out);
    out.flush();

    return result.toString();
  }

  private void writeExampleSnippet(PrintWriter out)
  {
    Map<String, Object> options = new HashMap<String, Object>();
    options.put("includeSignature", includeSignature);
    options.put("imagePath", "../../../../../../../../org.eclipse.emf.cdo.releng.doc/resources/");

    try
    {
      Object instance = snippet.newInstance(snippetDoc, options);
      write.invoke(instance, out);
    }
    catch (Throwable ex)
    {
      ex.printStackTrace();
    }
  }

  static
  {
    try
    {
      Class<?> c = Class.forName("CodeSnippet");
      snippet = c.getConstructor(Doc.class, Map.class);
      write = c.getMethod("write", PrintWriter.class);
    }
    catch (Throwable ex)
    {
      ex.printStackTrace();
    }
  }
}
