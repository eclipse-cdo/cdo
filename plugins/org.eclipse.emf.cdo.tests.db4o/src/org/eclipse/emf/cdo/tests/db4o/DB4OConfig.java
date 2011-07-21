package org.eclipse.emf.cdo.tests.db4o;

import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.internal.db4o.DB4OStore;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;

import org.eclipse.net4j.util.io.TMPUtil;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;

/**
 * @author Victor Roldan Betancort
 */
public class DB4OConfig extends RepositoryConfig
{
  private static final Random RANDOM = new Random(System.currentTimeMillis());

  private static final long serialVersionUID = 1L;

  private boolean mem;

  private transient boolean optimizing = true;

  public DB4OConfig(boolean mem)
  {
    super("DB4O", false, false, IDGenerationLocation.STORE);
    this.mem = mem;
  }

  public boolean isMem()
  {
    return mem;
  }

  public IStore createStore(String repoName)
  {
    if (mem)
    {
      if (!isRestarting())
      {
        MEMDB4OStore.clearContainer();
      }

      return new MEMDB4OStore();
    }

    File tempFolder = TMPUtil.getTempFolder();
    File file = new File(tempFolder, "cdodb_" + repoName + ".db4o");
    if (file.exists() && !isRestarting())
    {
      file.delete();
    }

    int port = 0;
    boolean ok = false;
    do
    {
      ServerSocket sock = null;

      try
      {
        port = 1024 + RANDOM.nextInt(65536 - 1024);
        sock = new ServerSocket(port);
        ok = true;
      }
      catch (IOException e)
      {
      }
      finally
      {
        try
        {
          if (sock != null)
          {
            sock.close();
          }
        }
        catch (IOException e)
        {
        }
      }
    } while (!ok);

    return new DB4OStore(file.getPath(), port);
  }

  @Override
  protected boolean isOptimizing()
  {
    // Do NOT replace this with a hardcoded value!
    return optimizing;
  }
}
