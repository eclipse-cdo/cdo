package org.eclipse.emf.cdo.doc.programmers;

/**
 * Introduction
 * <p>
 * CDO is a model repository and distributed shared model framework for EMF models. It enables
 * collaborative editing of EMF models in a distributed environment, providing features such as
 * transactions, versioning, and branching.
 * <p>
 * This document provides an introduction to CDO programming, covering the following topics:
 *
 * @author Eike Stepper
 * @number 1
 */
public class Doc01_Introduction
{
  /**
   * Preparing the Workspace
   * <p>
   * Before you start working with CDO, you need to set up your development environment. This documentation
   * assumes that you have a basic understanding of EMF and Java development with Eclipse and that you have an
   * Eclipse IDE installed and configured for your applications.
   * <p>
   * The following prerequisites must be installed into the
   * <a href="https://help.eclipse.org/latest/index.jsp?topic=%2Forg.eclipse.pde.doc.user%2Fconcepts%2Ftarget.htm">target platform</a>
   * of your workspace:
   * <ul>
   * <li>The CDO SDK and the Net4j SDK are both available from the <a href="https://download.eclipse.org/modeling/emf/cdo/updates/downloads.html">CDO Downloads</a> page.
   *      There you find "Update Site" and  "Floating Update Site" buttons, which link to p2 repositories that contain the respective SDKs.
   * <li>The EMF SDK is available from the <a href="https://download.eclipse.org/modeling/emf/emf/updates/">EMF Updates</a> page.
   *      <a href="https://download.eclipse.org/modeling/emf/emf/builds"></a>contains the necessary libraries and tools for working with EMF models.
   * <li>Depending on what subset of CDO you want to use, you may need additional dependencies, such as database drivers or networking libraries.
   * </ul>
   * <p>
   * Make sure to include all necessary dependencies in your project's build path to avoid any runtime issues.
   * <p>
   * Note: If you are using an IDE other than Eclipse, you will need to manually download and include the required libraries in your project's build path.
   * At the bottom of the CDO Downloads page, you find a link to <a href="https://download.eclipse.org/modeling/emf/cdo/updates">All Promoted Builds</a>,
   * where you can download <code>emf-cdo-{qualifier}-Dropins.zip</code> archives containing all necessary libraries.
   * <p>
   * Note also that CDO and Net4j, as well as EMF, attach great importance to API and binary compatibility. Therefore, you can
   * usually mix and match different versions of these libraries. However, it is recommended to use compatible versions to avoid any potential issues.
   * On the CDO Downloads page mentioned above, you find version information about CDO and Net4j, as well as links to the corresponding EMF
   * and Eclipse releases.
   */
  public class PreparingWorkspace
  {
  }
}
