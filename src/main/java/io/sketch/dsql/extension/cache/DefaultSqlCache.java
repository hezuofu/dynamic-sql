package io.sketch.dsql.extension.cache;

import io.sketch.dsql.core.node.SqlNode;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DefaultSqlCache implements SqlCache {

    private final Map<String, SqlNode> cache;
    private final int maxSize;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public DefaultSqlCache(int maxSize) {
        this.maxSize = maxSize;
        this.cache = new LinkedHashMap<String, SqlNode>(maxSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, SqlNode> eldest) {
                return size() > maxSize;
            }
        };
    }

    public DefaultSqlCache() {
        this(1000);
    }

    @Override
    public SqlNode get(String key) {
        lock.readLock().lock();
        try {
            return cache.get(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void put(String key, SqlNode value) {
        lock.writeLock().lock();
        try {
            cache.put(key, value);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void remove(String key) {
        lock.writeLock().lock();
        try {
            cache.remove(key);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean contains(String key) {
        lock.readLock().lock();
        try {
            return cache.containsKey(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void clear() {
        lock.writeLock().lock();
        try {
            cache.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public int size() {
        lock.readLock().lock();
        try {
            return cache.size();
        } finally {
            lock.readLock().unlock();
        }
    }
}