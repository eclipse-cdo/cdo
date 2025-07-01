/*
 * Copyright (c) 2015, 2016, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.doc.operators;

import org.eclipse.emf.cdo.doc.operators.Doc01_ConfiguringRepositories.Element_store;
import org.eclipse.emf.cdo.doc.users.Doc02_ManagingRepositories;

/**
 * Operating a CDO Server
 * <p>
 * <b>Table of Contents</b> {@toc}
 *
 * @author Eike Stepper
 */
public class Doc00_OperatingServer
{
  /**
   * Deploying a CDO Server
   * <p>
   * The easiest way to deploy a stand-alone CDO Server is to download the latest
   * <a href="https://wiki.eclipse.org/Eclipse_Installer">Eclipse Installer</a> for the target environment:
   * <ul>
   * <li><a href="http://www.eclipse.org/downloads/download.php?file=/oomph/products/latest/eclipse-inst-win64.exe">Windows 64 Bit</a> (self-extracting exe)
   * <li><a href="http://www.eclipse.org/downloads/download.php?file=/oomph/products/latest/eclipse-inst-win32.exe">Windows 32 Bit</a> (self-extracting exe)
   * <li><a href="http://www.eclipse.org/downloads/download.php?file=/oomph/products/latest/eclipse-inst-mac64.tar.gz">Mac OS 64 Bit</a> (tar.gz)
   * <li><a href="http://www.eclipse.org/downloads/download.php?file=/oomph/products/latest/eclipse-inst-linux64.tar.gz">Linux 64 Bit</a> (tar.gz)
   * <li><a href="http://www.eclipse.org/downloads/download.php?file=/oomph/products/latest/eclipse-inst-linux32.tar.gz">Linux 32 Bit</a> (tar.gz)
   * </ul>
   * <p>
   * When the Eclipse Installer is started, select the "CDO Server" application:
   * <p align="center">{@image eclipse-installer.png}
   * <p>
   * On the second page make sure you select the product version (of the CDO Server) that matches your clients' versions:
   * <p align="center">{@image eclipse-installer2.png}
   * <p>
   * When the installation has finished the "eclipse" folder under the installation folder contains the <b>cdo-server.xml</b>
   * configuration file, which looks similar to this:
   * {@link #cdoServerXML() cdo&#8209;server.xml}
   * <p>
   * Please note that many {@link Doc02_ManagingRepositories repository configuration options} can <b>not</b> be changed anymore after the repository
   * has been started the first time.
   *
   * @see Doc01_ConfiguringRepositories
   * @see Doc02_ConfiguringAcceptors
   * @see Doc03_ManagingSecurity
   */
  public class Doc_ServerDeploy
  {
    /**
    * @snippet xml cdo-server.xml
    */
    public void cdoServerXML()
    {
    }
  }

  /**
   * Starting a CDO Server
   * <p>
   * Starting a CDO Server differs a little bit depending on the target environment:
   * <ul>
   * <li> On <b>Windows</b> double-click the <code>eclipsec.exe</code> file (mind the last "c").
   * <li> On <b>Mac OS</b> start the <code>Eclipse.app/Contents/MacOS/eclipse</code> executable from a terminal window.
   * <li> On <b>Linux</b> start the normal <code>eclipse</code> executable from a terminal window.
   * </ul>
   * <p>
   * After successful start of the server the console should look similar to this:
   * <p align="center">{@image server-start.png}
   * <p>
   * Please note that many {@link Doc02_ManagingRepositories repository configuration options} can <b>not</b> be changed anymore after the repository
   * has been started the first time.
   *
   * @see Doc01_ConfiguringRepositories
   */
  public class Doc_ServerStart
  {
  }

  /**
   * Stopping a CDO Server
   * <p>
   * To gracefully shut down a running CDO server enter the command "<code>close</code>" into the console and confirm with "<code>yes</code>":
   * <p align="center">{@image server-stop.png}
   * <p>
   * Please note that the {@link Element_store DBStore} supports automatic crash detection and recovery at startup time.
   * Other store may or may not support similar functionality.
   */
  public class Doc_ServerStop
  {
  }
}
