package org.eclipse.emf.cdo.server.internal.security;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.spi.security.PermissionCache;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public final class DefaultPermissionCacheCreator implements PermissionCache.Creator
{
  private final Map<Key, Byte> entries;

  public DefaultPermissionCacheCreator(final int capacity)
  {
    if (capacity <= 0)
    {
      throw new IllegalArgumentException("Permission cache capacity must be positive: " + capacity);
    }

    entries = new LinkedHashMap<>(16, 0.75F, true)
    {
      private static final long serialVersionUID = 1L;

      @Override
      protected boolean removeEldestEntry(Map.Entry<Key, Byte> eldest)
      {
        return size() > capacity;
      }
    };
  }

  @Override
  public PermissionCache create(IRepository repository, String userID, CDOBranch branch)
  {
    return new DefaultPermissionCache(new Object(), entries);
  }

  /**
   * @author Eike Stepper
   */
  private static final class Key
  {
    private final Object scope;

    private final CDOID id;

    public Key(Object scope, CDOID id)
    {
      this.scope = scope;
      this.id = id;
    }

    @Override
    public int hashCode()
    {
      return System.identityHashCode(scope) * 31 + id.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
      if (!(obj instanceof Key))
      {
        return false;
      }

      Key other = (Key)obj;
      return scope == other.scope && id.equals(other.id);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class DefaultPermissionCache implements PermissionCache
  {
    private final Object scope;

    private final Map<Key, Byte> entries;

    public DefaultPermissionCache(Object scope, Map<Key, Byte> entries)
    {
      this.scope = scope;
      this.entries = entries;
    }

    @Override
    public CDOPermission get(CDOID resourceNodeID, boolean resourceNode)
    {
      synchronized (entries)
      {
        Byte packed = entries.get(new Key(scope, resourceNodeID));
        if (packed == null)
        {
          return null;
        }

        int value = packed >>> (resourceNode ? 2 : 0) & 3;
        return permission(value);
      }
    }

    @Override
    public void put(CDOID resourceNodeID, boolean resourceNode, CDOPermission permission)
    {
      synchronized (entries)
      {
        Key key = new Key(scope, resourceNodeID);
        byte packed = entries.getOrDefault(key, (byte)0);
        int shift = resourceNode ? 2 : 0;
        entries.put(key, (byte)(packed & ~(3 << shift) | code(permission) << shift));
      }
    }

    private static int code(CDOPermission permission)
    {
      if (permission == CDOPermission.NONE)
      {
        return 1;
      }

      if (permission == CDOPermission.READ)
      {
        return 2;
      }

      if (permission == CDOPermission.WRITE)
      {
        return 3;
      }

      throw new IllegalArgumentException("Unsupported permission: " + permission);
    }

    private static CDOPermission permission(int code)
    {
      switch (code)
      {
      case 1:
        return CDOPermission.NONE;

      case 2:
        return CDOPermission.READ;

      case 3:
        return CDOPermission.WRITE;

      default:
        return null;
      }
    }
  }
}
