package org.eclipse.emf.cdo.spi.common;

import org.eclipse.emf.cdo.common.id.CDOIDLibraryDescriptor;
import org.eclipse.emf.cdo.common.id.CDOIDLibraryProvider;

import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.OMBundle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CDOIDLibraryProviderImpl implements CDOIDLibraryProvider
{
  private Map<String, OMBundle> bundles = new HashMap<String, OMBundle>();

  public CDOIDLibraryProviderImpl()
  {
  }

  public void addLibrary(String libraryName, OMBundle bundle)
  {
    bundles.put(libraryName, bundle);
  }

  public String[] getLibraryNames()
  {
    Set<String> libraryNames = bundles.keySet();
    return libraryNames.toArray(new String[libraryNames.size()]);
  }

  public CDOIDLibraryDescriptor createDescriptor(String factoryName)
  {
    return new Descriptor(factoryName);
  }

  public InputStream getContents(String libraryName) throws IOException
  {
    File library = getLibrary(libraryName);
    return IOUtil.openInputStream(library);
  }

  public int getSize(String libraryName)
  {
    File library = getLibrary(libraryName);
    return (int)library.length();
  }

  private File getLibrary(String libraryName)
  {
    OMBundle bundle = bundles.get(libraryName);
    if (bundle == null)
    {
      throw new IllegalStateException("Unknown library: " + libraryName);
    }

    URL url = bundle.getBaseURL();
    File file = new File(url.getFile());
    if (file.exists() && file.isDirectory())
    {
      file = new File(file, libraryName);
    }

    if (file.exists() && file.isFile() && file.getName().endsWith(".jar"))
    {
      return file;
    }

    throw new IllegalStateException("Not a JAR: " + file.getAbsolutePath());
  }

  /**
   * @author Eike Stepper
   */
  private final class Descriptor extends CDOIDLibraryDescriptorImpl
  {
    public Descriptor(String factoryName)
    {
      super(factoryName, CDOIDLibraryProviderImpl.this.getLibraryNames());
    }

    public Descriptor(ExtendedDataInput in) throws IOException
    {
      super(in);
    }
  }
}
