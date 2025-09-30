package org.eclipse.emf.cdo.doc.programmers.client;

import org.eclipse.emf.cdo.common.lob.CDOBlob;
import org.eclipse.emf.cdo.eresource.CDOBinaryResource;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.io.IOUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Large Objects
 * <p>
 * This chapter describes how to work with large objects in CDO client applications. Large objects, such as images,
 * documents, or binary files, require special handling to ensure efficient storage, retrieval, and transfer within the
 * CDO repository.
 * <p>
 * CDO provides mechanisms for streaming large objects, managing memory usage, and integrating binary and textual data
 * with EMF models. Learn best practices for loading, saving, and querying large objects, as well as strategies for
 * optimizing performance and avoiding common pitfalls.
 * <p>
 * Topics include:
 * <ul>
 *   <li>Storing and retrieving binary and text resources</li>
 *   <li>Streaming APIs for large data</li>
 *   <li>Performance considerations</li>
 *   <li>Integration with EMF ResourceSet</li>
 * </ul>
 */
public class Doc07_LargeObjects
{
  /**
   * Example: Store a binary file as a CDO binary resource
   * Stores a binary file in the repository using a CDOTransaction.
   * @param transaction the CDOTransaction
   * @param resourcePath the path for the new binary resource
   * @param file the File to store
   * @snip
   */
  public void storeBinaryResource(CDOTransaction transaction, String resourcePath, File file) throws Exception
  {
    CDOBinaryResource binaryResource = transaction.createBinaryResource(resourcePath);

    try (FileInputStream fis = new FileInputStream(file))
    {
      binaryResource.setContents(new CDOBlob(fis));
      transaction.commit();
      System.out.println("Stored binary resource at: " + resourcePath);
    }
  }

  /**
   * Example: Retrieve and save a binary resource to a file
   * Loads a binary resource from the repository and saves it to a local file.
   * @param view the CDOView
   * @param resourcePath the path of the binary resource
   * @param file the File to save to
   * @snip
   */
  public void retrieveBinaryResource(CDOView view, String resourcePath, File file) throws IOException
  {
    CDOBinaryResource binaryResource = view.getBinaryResource(resourcePath);

    try (FileOutputStream fos = new FileOutputStream(file))
    {
      CDOBlob blob = binaryResource.getContents();
      InputStream contents = blob.getContents();
      IOUtil.copy(contents, fos);
      System.out.println("Saved binary resource from: " + resourcePath + " to file: " + file.getAbsolutePath());
    }
  }
}
