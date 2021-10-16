package com.github.maksymiliank.rankmanager;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class RankManager {

    public static final int DEFAULT_RANK_ID = -2;

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private final Map<String, Rank> playersRanks = new HashMap<>();
    private final List<Rank> ranksByLevel = new ArrayList<>();
    private final Map<Integer, Rank> ranksById = new HashMap<>();

    public RankManager(List<Rank> ranks) {
        if (ranks.stream().noneMatch(r -> r.getId() == DEFAULT_RANK_ID)) {
            throw new IllegalArgumentException("Ranks list does not contain a default rank");
        }

        var ranksTemp = new ArrayList<>(ranks);
        ranksTemp.sort(Comparator.comparingInt(Rank::getLevel));

        ranksByLevel.addAll(ranksTemp);
        ranksTemp.forEach(r -> ranksById.put(r.getId(), r));

        recalculatePermissions();
    }

    public void addRank(Rank rank) {
        lock.writeLock().lock();
        try {
            if (ranksById.get(rank.getId()) != null) {
                throw new IllegalArgumentException("Rank with that id already exists");
            } else if (ranksByLevel.stream().anyMatch(r -> r.getLevel() == rank.getLevel())) {
                throw new IllegalArgumentException("Rank with that level already exists");
            }

            ranksByLevel.add(findRankIndex(rank), rank);
            ranksById.put(rank.getId(), rank);

            recalculatePermissions();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void removeRank(int rankId) {
        lock.writeLock().lock();
        try {
            if (ranksById.get(rankId) == null) {
                throw new IllegalArgumentException("Rank with that id does not exist");
            } else if (rankId == DEFAULT_RANK_ID) {
                throw new IllegalArgumentException("Cannot remove a default rank");
            }

            ranksByLevel.removeIf(r -> r.getId() == rankId);
            ranksById.remove(rankId);

            var playersToChangeRank = playersRanks.entrySet().stream()
                    .filter(e -> e.getValue().getId() == rankId)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            playersToChangeRank.forEach(p -> playersRanks.put(p, ranksById.get(DEFAULT_RANK_ID)));

            recalculatePermissions();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void modifyRank(int id, RankModification rankModification) {
        lock.writeLock().lock();
        try {
            var rank = ranksById.get(id);

            if (rankModification.getLevel().isPresent()) {
                int newLevel = rankModification.getLevel().get();
                if (newLevel != rank.getLevel() && ranksByLevel.stream().anyMatch(r -> r.getLevel() == newLevel)) {
                    throw new IllegalArgumentException("Rank with the new level already exists");
                }
            }

            rank.modify(rankModification);

            ranksByLevel.removeIf(r -> r.getId() == id);
            ranksByLevel.add(findRankIndex(rank), rank);

            recalculatePermissions();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void setPlayer(String player, int rankId) {
        lock.writeLock().lock();
        try {
            if (getRankById(rankId).isEmpty()) {
                throw new IllegalArgumentException("Rank with that is does not exist");
            }

            playersRanks.put(player, ranksById.get(rankId));
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void removePlayer(String player) {
        lock.writeLock().lock();
        try {
            playersRanks.remove(player);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Optional<Rank> getRankByPlayer(String player) {
        lock.readLock().lock();
        try {
            return Optional.ofNullable(playersRanks.get(player));
        } finally {
            lock.readLock().unlock();
        }
    }

    public Optional<Rank> getRankById(int id) {
        lock.readLock().lock();
        try {
            return Optional.ofNullable(ranksById.get(id));
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Rank> getRanks() {
        lock.readLock().lock();
        try {
            return List.copyOf(ranksByLevel);
        } finally {
            lock.readLock().unlock();
        }
    }

    private void recalculatePermissions() {
        var inherited = new HashSet<String>();
        ranksByLevel.forEach(r -> {
            inherited.addAll(r.getRankPermissions());
            r.getRankNegatedPermissions().forEach(inherited::remove);

            r.resetEffectivePermissions(inherited);
        });
    }

    private int findRankIndex(Rank rank) {
        return (int) ranksByLevel.stream()
                .filter(r -> r.getLevel() < rank.getLevel())
                .count();
    }
}
