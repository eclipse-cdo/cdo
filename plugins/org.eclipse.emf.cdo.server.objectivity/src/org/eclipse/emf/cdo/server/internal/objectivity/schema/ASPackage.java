/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Ibrahim Sallam - code refactoring for CDO 3.0
 */
package org.eclipse.emf.cdo.server.internal.objectivity.schema;

/**
 * @author Simon McDuff
 */
public class ASPackage extends ooObj
{
  private long range1;

  private long range2;

  private String uri = null;

  private String ecoreString = null;

  public ASPackage(String uri)
  {
    this.uri = uri;
  }

  public void setEcore(String eCore)
  {
    markModified();
    ecoreString = eCore;
  }

  public String getEcore()
  {
    fetch();
    return ecoreString;
  }

  public String getURI()
  {
    fetch();
    return uri;
  }

  public long getRange1()
  {
    fetch();
    return range1;
  }

  public void setRange1(long range1)
  {
    markModified();
    this.range1 = range1;
  }

  public long getRange2()
  {
    fetch();
    return range2;
  }

  public void setRange2(long range2)
  {
    markModified();
    this.range2 = range2;
  }

}
