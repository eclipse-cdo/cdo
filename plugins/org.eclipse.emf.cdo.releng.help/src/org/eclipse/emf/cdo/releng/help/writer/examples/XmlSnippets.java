/*
 * Copyright (c) 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.help.writer.examples;

/**
 * Embedding XML Snippets
 * <p>
 * {@toc}
 */
public class XmlSnippets
{
  /**
   * XML Example with a Callout
   * <p>
   * {@link #cdoServerXml()}
   */
  public class XmlExample
  {
    /**
     * @snippet xml ../../../../../../../../../../org.eclipse.emf.cdo.server.product/config/cdo-server.xml
     * @callout The mapping strategy "horizontal" delegates to a mapping strategy chosen to match the repository
     *          properties <i>supportingAudits</i> and <i>supportingBranches</i>.
     */
    public void cdoServerXml()
    {
    }
  }

  /**
   * XMI Example
   * <p>
   * {@link #companyEcore()}
   */
  public class XmiExample
  {
    /**
     * @snippet xml ../../../../../../../../../../org.eclipse.emf.cdo.examples.company/model/company.ecore
     */
    public void companyEcore()
    {
    }
  }

  // /**
  // * HTML Example
  // * <p>
  // * {@link #aboutHtml()}
  // */
  // public class HtmlExample
  // {
  // /**
  // * @snippet xml ../../../../../../../../../about.html
  // */
  // public void aboutHtml()
  // {
  // }
  // }
}
