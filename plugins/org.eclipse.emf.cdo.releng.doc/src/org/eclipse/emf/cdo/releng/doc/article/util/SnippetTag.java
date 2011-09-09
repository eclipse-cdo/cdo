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
import java.lang.reflect.Method;

/**
 * @author Eike Stepper
 */
public class SnippetTag extends TextTag
{
  private static Method writeExampleSnippet;

  private final Doc snippet;

  private final boolean includeSignature;

  public SnippetTag(SeeTag delegate, Doc snippet, boolean includeSignature)
  {
    super(delegate, null);
    this.snippet = snippet;
    this.includeSignature = includeSignature;
  }

  @Override
  public SeeTag getDelegate()
  {
    return (SeeTag)super.getDelegate();
  }

  public final Doc getSnippet()
  {
    return snippet;
  }

  public final boolean isIncludeSignature()
  {
    return includeSignature;
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
    try
    {
      writeExampleSnippet.invoke(null, new Object[] { out, includeSignature, snippet });
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
      Class<?> snippets = Class.forName("Snippets");
      Class<?>[] parameters = { PrintWriter.class, boolean.class, Doc.class };
      writeExampleSnippet = snippets.getMethod("writeExampleSnippet", parameters);
    }
    catch (Throwable ex)
    {
      ex.printStackTrace();
    }
  }
}
