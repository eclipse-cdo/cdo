package org.eclipse.emf.cdo.internal.server.store;

import org.eclipse.emf.cdo.protocol.CDOID;

import org.eclipse.net4j.util.transaction.ITransaction;
import org.eclipse.net4j.util.transaction.ITransactionalOperation;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class RegisterResourceOperation<T extends ITransaction> implements ITransactionalOperation<T, Object>
{
  private CDOID id;

  private String path;

  private Map<CDOID, String> idToPathMap = new HashMap();

  private Map<String, CDOID> pathToIDMap = new HashMap();

  public RegisterResourceOperation(CDOID id, String path, Map<CDOID, String> idToPathMap, Map<String, CDOID> pathToIDMap)
  {
    this.id = id;
    this.path = path;
    this.idToPathMap = idToPathMap;
    this.pathToIDMap = pathToIDMap;
  }

  public Object prepare(T transaction) throws Exception
  {
    update(transaction, id, path);
    idToPathMap.put(id, path);
    pathToIDMap.put(path, id);
    return null;
  }

  public void onCommit(T transaction)
  {
  }

  public void onRollback(T transaction)
  {
    idToPathMap.remove(id);
    pathToIDMap.remove(path);
  }

  protected abstract void update(T transaction, CDOID id, String path);
}