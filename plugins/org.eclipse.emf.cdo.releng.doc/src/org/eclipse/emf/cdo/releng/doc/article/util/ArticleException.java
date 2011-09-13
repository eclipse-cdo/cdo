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

/**
 * @author Eike Stepper
 */
public class ArticleException extends RuntimeException
{
  private static final long serialVersionUID = 1L;

  public ArticleException()
  {
  }

  public ArticleException(String message)
  {
    super(message);
  }

  public ArticleException(Throwable cause)
  {
    super(cause);
  }

  public ArticleException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
