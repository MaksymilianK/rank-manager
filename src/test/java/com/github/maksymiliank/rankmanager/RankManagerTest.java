package com.github.maksymiliank.rankmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class RankManagerTest {

    private RankManager rankManager;

    @BeforeEach
    public void setUp() {
        rankManager = new RankManager(List.of(
                Rank.builder()
                        .id(1)
                        .rankPermissions(List.of("a.b.c", "a.*", "a.b", "a.b.c.d.e"))
                        .rankNegatedPermissions(List.of("a.b.c.d", "a.b.c"))
                        .level(5)
                        .name("1")
                        .displayName("d1")
                        .chatFormat("c1")
                        .build(),
                Rank.builder()
                        .id(RankManager.DEFAULT_RANK_ID)
                        .rankPermissions(List.of("a"))
                        .rankNegatedPermissions(List.of("a.b.c.d.e"))
                        .level(2)
                        .name("2")
                        .displayName("d2")
                        .chatFormat("c2")
                        .build(),
                Rank.builder()
                        .id(3)
                        .rankPermissions(List.of())
                        .rankNegatedPermissions(List.of("a.*"))
                        .level(100)
                        .name("3")
                        .displayName("d3")
                        .chatFormat("c3")
                        .build()
        ));
    }

    @Test
    public void whenConstructs_setsRanksById() {
        assertThat(rankManager.getRankById(1).get().getId()).isEqualTo(1);
        assertThat(rankManager.getRankById(RankManager.DEFAULT_RANK_ID).get().getId()).isEqualTo(RankManager.DEFAULT_RANK_ID);
        assertThat(rankManager.getRankById(3).get().getId()).isEqualTo(3);
    }

    @Test
    public void whenConstructs_ordersRanksByLevel() {
        var ranks = rankManager.getRanks();

        assertThat(ranks.get(0).getId()).isEqualTo(RankManager.DEFAULT_RANK_ID);
        assertThat(ranks.get(1).getId()).isEqualTo(1);
        assertThat(ranks.get(2).getId()).isEqualTo(3);
    }

    @Test
    public void whenConstructs_calculatesPermissions() {
        assertThat(rankManager.getRankById(RankManager.DEFAULT_RANK_ID).get().hasPermission("a")).isTrue();
        assertThat(rankManager.getRankById(RankManager.DEFAULT_RANK_ID).get().hasPermission("a.b.c.d.e")).isFalse();

        assertThat(rankManager.getRankById(1).get().hasPermission("a")).isTrue();
        assertThat(rankManager.getRankById(1).get().hasPermission("a.b.c.d.e")).isTrue();
        assertThat(rankManager.getRankById(1).get().hasPermission("a.b.c")).isFalse();
        assertThat(rankManager.getRankById(1).get().hasPermission("a.*")).isTrue();
        assertThat(rankManager.getRankById(1).get().hasPermission("a.b")).isTrue();
        assertThat(rankManager.getRankById(1).get().hasPermission("a.b.c.d")).isFalse();

        assertThat(rankManager.getRankById(3).get().hasPermission("a")).isTrue();
        assertThat(rankManager.getRankById(3).get().hasPermission("a.b.c.d.e")).isTrue();
        assertThat(rankManager.getRankById(3).get().hasPermission("a.b.c")).isFalse();
        assertThat(rankManager.getRankById(3).get().hasPermission("a.*")).isFalse();
        assertThat(rankManager.getRankById(3).get().hasPermission("a.b")).isTrue();
        assertThat(rankManager.getRankById(3).get().hasPermission("a.b.c.d")).isFalse();
    }

    @Test
    public void givenNoDefaultRank_whenConstructs_throwsException() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new RankManager(List.of(sampleRank())));
    }

    @Test
    public void whenAddsRank_setsRankById() {
        rankManager.addRank(sampleRank());

        assertThat(rankManager.getRankById(4).get().getId()).isEqualTo(4);
    }

    @Test
    public void whenAddsRank_insertsRankByLevel() {
        rankManager.addRank(sampleRank());

        var ranks = rankManager.getRanks();

        assertThat(ranks.get(0).getId()).isEqualTo(RankManager.DEFAULT_RANK_ID);
        assertThat(ranks.get(1).getId()).isEqualTo(4);
        assertThat(ranks.get(2).getId()).isEqualTo(1);
        assertThat(ranks.get(3).getId()).isEqualTo(3);
    }

    @Test
    public void whenAddsRank_recalculatesPermissions() {
        rankManager.addRank(sampleRank());

        assertThat(rankManager.getRankById(RankManager.DEFAULT_RANK_ID).get().hasPermission("a")).isTrue();
        assertThat(rankManager.getRankById(RankManager.DEFAULT_RANK_ID).get().hasPermission("a.b.c.d.e")).isFalse();

        assertThat(rankManager.getRankById(4).get().hasPermission("a")).isFalse();
        assertThat(rankManager.getRankById(4).get().hasPermission("a.b.c.d.e")).isFalse();
        assertThat(rankManager.getRankById(4).get().hasPermission("a.b.*")).isTrue();

        assertThat(rankManager.getRankById(1).get().hasPermission("a")).isFalse();
        assertThat(rankManager.getRankById(1).get().hasPermission("a.b.c.d.e")).isTrue();
        assertThat(rankManager.getRankById(1).get().hasPermission("a.b.*")).isTrue();
        assertThat(rankManager.getRankById(1).get().hasPermission("a.b.c")).isFalse();
        assertThat(rankManager.getRankById(1).get().hasPermission("a.*")).isTrue();
        assertThat(rankManager.getRankById(1).get().hasPermission("a.b")).isTrue();
        assertThat(rankManager.getRankById(1).get().hasPermission("a.b.c.d")).isFalse();

        assertThat(rankManager.getRankById(3).get().hasPermission("a")).isFalse();
        assertThat(rankManager.getRankById(3).get().hasPermission("a.b.c.d.e")).isTrue();
        assertThat(rankManager.getRankById(3).get().hasPermission("a.b.*")).isTrue();
        assertThat(rankManager.getRankById(3).get().hasPermission("a.b.c")).isFalse();
        assertThat(rankManager.getRankById(3).get().hasPermission("a.*")).isFalse();
        assertThat(rankManager.getRankById(3).get().hasPermission("a.b")).isTrue();
        assertThat(rankManager.getRankById(3).get().hasPermission("a.b.c.d")).isFalse();
    }

    @Test
    public void givenAlreadyExistingId_whenAddsRank_throwsException() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> rankManager.addRank(
                        Rank.builder()
                                .id(1)
                                .rankPermissions(List.of())
                                .rankNegatedPermissions(List.of())
                                .level(1)
                                .name("")
                                .displayName("")
                                .chatFormat("")
                                .build()
                ));
    }

    @Test
    public void givenAlreadyExistingLevel_whenAddsRank_throwsException() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> rankManager.addRank(
                        Rank.builder()
                                .id(4)
                                .rankPermissions(List.of())
                                .rankNegatedPermissions(List.of())
                                .level(5)
                                .name("")
                                .displayName("")
                                .chatFormat("")
                                .build()
                ));
    }

    @Test
    public void whenSetsPlayers_setsTheirRank() {
        addSamplePlayers();

        assertThat(rankManager.getRankByPlayer("p10").get().getId()).isEqualTo(1);
        assertThat(rankManager.getRankByPlayer("p11").get().getId()).isEqualTo(1);
        assertThat(rankManager.getRankByPlayer("p2").get().getId()).isEqualTo(RankManager.DEFAULT_RANK_ID);
        assertThat(rankManager.getRankByPlayer("p3").get().getId()).isEqualTo(3);
    }

    @Test
    public void givenNonExistingRankId_whenSetsPlayers_throwsException() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> rankManager.setPlayer("p", 500));
    }

    @Test
    public void whenRemovesRank_removesRankById() {
        rankManager.removeRank(1);

        assertThat(rankManager.getRankById(1)).isEmpty();
    }

    @Test
    public void whenRemovesRank_removesRankByLevel() {
        rankManager.removeRank(1);

        var ranks = rankManager.getRanks();

        assertThat(ranks.get(0).getId()).isEqualTo(RankManager.DEFAULT_RANK_ID);
        assertThat(ranks.get(1).getId()).isEqualTo(3);
    }

    @Test
    public void whenRemovesRank_recalculatesPermissions() {
        rankManager.removeRank(1);

        assertThat(rankManager.getRankById(RankManager.DEFAULT_RANK_ID).get().hasPermission("a")).isTrue();
        assertThat(rankManager.getRankById(RankManager.DEFAULT_RANK_ID).get().hasPermission("a.b.c.d.e")).isFalse();

        assertThat(rankManager.getRankById(3).get().hasPermission("a")).isTrue();
        assertThat(rankManager.getRankById(3).get().hasPermission("a.b.c.d.e")).isFalse();
        assertThat(rankManager.getRankById(3).get().hasPermission("a.b.c")).isFalse();
        assertThat(rankManager.getRankById(3).get().hasPermission("a.*")).isFalse();
        assertThat(rankManager.getRankById(3).get().hasPermission("a.b")).isFalse();
        assertThat(rankManager.getRankById(3).get().hasPermission("a.b.c.d")).isFalse();
    }

    @Test
    public void whenRemovesRank_setsPlayersRanksToDefault() {
        addSamplePlayers();

        rankManager.removeRank(1);

        assertThat(rankManager.getRankByPlayer("p10").get().getId()).isEqualTo(RankManager.DEFAULT_RANK_ID);
        assertThat(rankManager.getRankByPlayer("p11").get().getId()).isEqualTo(RankManager.DEFAULT_RANK_ID);
        assertThat(rankManager.getRankByPlayer("p2").get().getId()).isEqualTo(RankManager.DEFAULT_RANK_ID);
        assertThat(rankManager.getRankByPlayer("p3").get().getId()).isEqualTo(3);
    }

    @Test
    public void givenNonExistingId_whenRemovesRank_throwsException() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> rankManager.removeRank(500));
    }

    @Test
    public void givenDefaultRankId_whenRemovesRank_throwsException() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> rankManager.removeRank(RankManager.DEFAULT_RANK_ID));
    }

    @Test
    public void whenModifiesRank_modifiesFields() {
        rankManager.modifyRank(1, sampleRankModification());

        var rank = rankManager.getRankById(1);

        assertThat(rank.get().getLevel()).isEqualTo(1);
        assertThat(rank.get().getName()).isEqualTo("1new");
        assertThat(rank.get().getDisplayName()).isEqualTo("d1new");
        assertThat(rank.get().getChatFormat()).isEqualTo("c1new");
        assertThat(rank.get().getRankPermissions())
                .containsExactlyInAnyOrder("a.b.c", "a.*", "a.b.c.d.e", "a.b.c.*", "a.*.b");
        assertThat(rank.get().getRankNegatedPermissions()).containsExactlyInAnyOrder("a.b.c.d", "a");
    }

    @Test
    public void whenModifiesRank_reordersRanksByLevel() {
        rankManager.modifyRank(1, sampleRankModification());

        var ranks = rankManager.getRanks();

        assertThat(ranks.get(0).getId()).isEqualTo(1);
        assertThat(ranks.get(1).getId()).isEqualTo(RankManager.DEFAULT_RANK_ID);
        assertThat(ranks.get(2).getId()).isEqualTo(3);
    }

    @Test
    public void whenModifiesRank_recalculatesPermissions() {
        rankManager.modifyRank(1, sampleRankModification());

        assertThat(rankManager.getRankById(1).get().hasPermission("a.b.c")).isTrue();
        assertThat(rankManager.getRankById(1).get().hasPermission("a.*")).isTrue();
        assertThat(rankManager.getRankById(1).get().hasPermission("a.b")).isFalse();
        assertThat(rankManager.getRankById(1).get().hasPermission("a.b.c.d.e")).isTrue();
        assertThat(rankManager.getRankById(1).get().hasPermission("a.b.c.d")).isFalse();
        assertThat(rankManager.getRankById(1).get().hasPermission("a.b.c.*")).isTrue();
        assertThat(rankManager.getRankById(1).get().hasPermission("a.*.b")).isTrue();
        assertThat(rankManager.getRankById(1).get().hasPermission("a")).isFalse();

        assertThat(rankManager.getRankById(RankManager.DEFAULT_RANK_ID).get().hasPermission("a.b.c")).isTrue();
        assertThat(rankManager.getRankById(RankManager.DEFAULT_RANK_ID).get().hasPermission("a.*")).isTrue();
        assertThat(rankManager.getRankById(RankManager.DEFAULT_RANK_ID).get().hasPermission("a.b")).isFalse();
        assertThat(rankManager.getRankById(RankManager.DEFAULT_RANK_ID).get().hasPermission("a.b.c.d.e")).isFalse();
        assertThat(rankManager.getRankById(RankManager.DEFAULT_RANK_ID).get().hasPermission("a.b.c.d")).isFalse();
        assertThat(rankManager.getRankById(RankManager.DEFAULT_RANK_ID).get().hasPermission("a.b.c.*")).isTrue();
        assertThat(rankManager.getRankById(RankManager.DEFAULT_RANK_ID).get().hasPermission("a.*.b")).isTrue();
        assertThat(rankManager.getRankById(RankManager.DEFAULT_RANK_ID).get().hasPermission("a")).isTrue();

        assertThat(rankManager.getRankById(3).get().hasPermission("a.b.c")).isTrue();
        assertThat(rankManager.getRankById(3).get().hasPermission("a.*")).isFalse();
        assertThat(rankManager.getRankById(3).get().hasPermission("a.b")).isFalse();
        assertThat(rankManager.getRankById(3).get().hasPermission("a.b.c.d.e")).isFalse();
        assertThat(rankManager.getRankById(3).get().hasPermission("a.b.c.d")).isFalse();
        assertThat(rankManager.getRankById(3).get().hasPermission("a.b.c.*")).isTrue();
        assertThat(rankManager.getRankById(3).get().hasPermission("a.*.b")).isTrue();
        assertThat(rankManager.getRankById(3).get().hasPermission("a")).isTrue();
    }

    @Test
    public void givenOldLevel_whenModifiesRank_doesNotThrowException() {
        rankManager.modifyRank(1, RankModification.builder()
                .level(5)
                .build());
    }

    @Test
    public void givenExistingNewLevel_whenModifiesRank_throwsException() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> rankManager.modifyRank(1, RankModification.builder()
                        .level(100)
                        .build())
                );
    }

    private Rank sampleRank() {
        return Rank.builder()
                .id(4)
                .rankPermissions(List.of("a.b.*"))
                .rankNegatedPermissions(List.of("a"))
                .level(3)
                .name("4")
                .displayName("d4")
                .chatFormat("c4")
                .build();
    }

    private RankModification sampleRankModification() {
        return RankModification.builder()
                .addedPermission(List.of("a.b.c.*", "a.*.b"))
                .removedPermission(List.of("a.b"))
                .addedNegatedPermission(List.of("a"))
                .removedNegatedPermission(List.of("a.b.c"))
                .level(1)
                .name("1new")
                .displayName("d1new")
                .chatFormat("c1new")
                .build();
    }

    private void addSamplePlayers() {
        rankManager.setPlayer("p10", 1);
        rankManager.setPlayer("p11", 1);
        rankManager.setPlayer("p2", RankManager.DEFAULT_RANK_ID);
        rankManager.setPlayer("p3", 3);
    }
}
