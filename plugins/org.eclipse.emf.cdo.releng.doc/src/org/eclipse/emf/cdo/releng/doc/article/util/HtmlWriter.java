/*
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 * @author Eike Stepper
 */
public class HtmlWriter extends PrintWriter
{
  public HtmlWriter(File file, String csn) throws FileNotFoundException, UnsupportedEncodingException
  {
    super(file, csn);
  }

  public HtmlWriter(File file) throws FileNotFoundException
  {
    super(file);
  }

  public HtmlWriter(OutputStream out, boolean autoFlush)
  {
    super(out, autoFlush);
  }

  public HtmlWriter(OutputStream out)
  {
    super(out);
  }

  public HtmlWriter(String fileName, String csn) throws FileNotFoundException, UnsupportedEncodingException
  {
    super(fileName, csn);
  }

  public HtmlWriter(String fileName) throws FileNotFoundException
  {
    super(fileName);
  }

  public HtmlWriter(Writer out, boolean autoFlush)
  {
    super(out, autoFlush);
  }

  public HtmlWriter(Writer out)
  {
    super(out);
  }

  public void writeHeading(int level, String label)
  {
    String h = Integer.toString(level);

    write("<h");
    write(h);
    write(">");
    write(label);
    write("</h");
    write(h);
    write(">\n");
  }

  public void writeHRef(String href, String label)
  {
    write("<a href=\"");
    write(href);
    write("\">");
    write(label);
    write("</a>");
  }
}
