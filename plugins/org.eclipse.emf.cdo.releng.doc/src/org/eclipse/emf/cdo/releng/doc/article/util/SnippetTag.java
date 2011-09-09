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

import com.sun.javadoc.SeeTag;
import com.sun.javadoc.SourcePosition;

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;

/**
 * @author Eike Stepper
 */
public class SnippetTag extends TextTag
{
  private final SourcePosition position;

  private final boolean includeSignature;

  public SnippetTag(SeeTag delegate, SourcePosition position, boolean includeSignature)
  {
    super(delegate, null);
    this.position = position;
    this.includeSignature = includeSignature;
  }

  @Override
  public SeeTag getDelegate()
  {
    return (SeeTag)super.getDelegate();
  }

  public final SourcePosition getPosition()
  {
    return position;
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
    out.write("\n");
    out.flush();

    return result.toString();
  }

  private void writeExampleSnippet(PrintWriter out)
  {
    Class<?>[] parameters = { PrintWriter.class, Boolean.class, SourcePosition.class };

    try
    {
      Class<?> snippets = Class.forName("Snippets");
      Method writeExampleSnippet = snippets.getMethod("writeExampleSnippet", parameters);
      writeExampleSnippet.invoke(null, new Object[] { out, includeSignature, position });
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
}
