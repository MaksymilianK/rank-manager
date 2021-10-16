package com.github.maksymiliank.rankmanager;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Rank {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private final int id;
    private final List<String> rankPermissions;
    private final List<String> rankNegatedPermissions;

    private int level;
    private String name;
    private String displayName;
    private String chatFormat;
    private Set<String> effectivePermissions = Set.of();

    private Rank(int id, List<String> rankPermissions, List<String> rankNegatedPermissions, int level, String name,
                String displayName, String chatFormat) {
        this.id = id;
        this.rankPermissions = rankPermissions;
        this.rankNegatedPermissions = rankNegatedPermissions;
        this.level = level;
        this.name = name;
        this.displayName = displayName;
        this.chatFormat = chatFormat;
    }

    public boolean hasPermission(String permission) {
        lock.readLock().lock();
        try {
            return effectivePermissions.contains(permission);
        } finally {
            lock.readLock().unlock();
        }
    }

    public int getId() {
        return id;
    }

    public List<String> getRankPermissions() {
        lock.readLock().lock();
        try {
            return List.copyOf(rankPermissions);
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<String> getRankNegatedPermissions() {
        lock.readLock().lock();
        try {
            return List.copyOf(rankNegatedPermissions);
        } finally {
            lock.readLock().unlock();
        }
    }

    public int getLevel() {
        lock.readLock().lock();
        try {
            return level;
        } finally {
            lock.readLock().unlock();
        }
    }

    public String getName() {
        lock.readLock().lock();
        try {
            return name;
        } finally {
            lock.readLock().unlock();
        }
    }

    public String getDisplayName() {
        lock.readLock().lock();
        try {
            return displayName;
        } finally {
            lock.readLock().unlock();
        }
    }

    public String getChatFormat() {
        lock.readLock().lock();
        try {
            return chatFormat;
        } finally {
            lock.readLock().unlock();
        }
    }

    public Set<String> getEffectivePermissions() {
        lock.readLock().lock();
        try {
            return Set.copyOf(effectivePermissions);
        } finally {
            lock.readLock().unlock();
        }
    }

    void modify(RankModification rankModification) {
        lock.writeLock().lock();
        try {
            if (rankModification.getLevel().isPresent()) {
                level = rankModification.getLevel().get();
            }

            if (rankModification.getName().isPresent()) {
                name = rankModification.getName().get();
            }

            if (rankModification.getDisplayName().isPresent()) {
                displayName = rankModification.getDisplayName().get();
            }

            if (rankModification.getChatFormat().isPresent()) {
                chatFormat = rankModification.getChatFormat().get();
            }

            rankPermissions.removeAll(rankModification.getRemovedPermissions());
            rankPermissions.addAll(rankModification.getAddedPermissions());

            rankNegatedPermissions.removeAll(rankModification.getRemovedNegatedPermissions());
            rankNegatedPermissions.addAll(rankModification.getAddedNegatedPermissions());
        } finally {
            lock.writeLock().unlock();
        }
    }

    void resetEffectivePermissions(Collection<String> effectivePermissions) {
        lock.writeLock().lock();
        try {
            this.effectivePermissions = Set.copyOf(effectivePermissions);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private static final class BuilderException extends RuntimeException {

        private BuilderException() {
            super("Cannot create RankModification class - one of fields is missing");
        }
    }

    public static final class Builder {

        private final List<String> rankPermissions = new ArrayList<>();
        private final List<String> rankNegatedPermissions = new ArrayList<>();

        private Integer id;
        private Integer level;
        private String name;
        private String displayName;
        private String chatFormat;

        private Builder() {}

        public Builder rankPermissions(Collection<String> rankPermissions) {
            this.rankPermissions.addAll(rankPermissions);
            return this;
        }

        public Builder rankNegatedPermissions(Collection<String> rankNegatedPermissions) {
            this.rankNegatedPermissions.addAll(rankNegatedPermissions);
            return this;
        }

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder level(int level) {
            this.level = level;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder chatFormat(String chatFormat) {
            this.chatFormat = chatFormat;
            return this;
        }

        public Rank build() {
            if (id == null || level == null || name == null || displayName == null || chatFormat == null) {
                throw new BuilderException();
            }

            return new Rank(id, rankPermissions, rankNegatedPermissions, level, name, displayName, chatFormat);
        }
    }
}
