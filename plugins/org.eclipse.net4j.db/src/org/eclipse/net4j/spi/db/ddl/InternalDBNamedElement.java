/*
 * Copyright (c) 2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.spi.db.ddl;

import org.eclipse.net4j.db.ddl.IDBNamedElement;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

/**
 * @since 4.2
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalDBNamedElement extends IDBNamedElement, InternalDBElement
{
  public void setName(String name);

  public String dumpToString();

  public void dump();

  public void dump(Writer writer) throws IOException;

  /**
   * @author Eike Stepper
   * @since 4.8
   */
  public interface DumpFormat
  {
    public CharSequence getNewline();

    public CharSequence getIndent();

    /**
     * @author Eike Stepper
     */
    public static class HTML extends OutputStreamWriter implements DumpFormat
    {
      public HTML(OutputStream out, Charset cs)
      {
        super(out, cs);
      }

      public HTML(OutputStream out, CharsetEncoder enc)
      {
        super(out, enc);
      }

      public HTML(OutputStream out, String charsetName) throws UnsupportedEncodingException
      {
        super(out, charsetName);
      }

      public HTML(OutputStream out)
      {
        super(out);
      }

      public CharSequence getNewline()
      {
        return "<br/>";
      }

      public CharSequence getIndent()
      {
        return "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
      }
    }
  }
}
