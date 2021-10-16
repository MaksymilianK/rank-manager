package com.github.maksymiliank.rankmanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public final class RankModification {

    private final Integer level;
    private final String name;
    private final String displayName;
    private final String chatFormat;
    private final List<String> removedPermissions;
    private final List<String> addedPermissions;
    private final List<String> removedNegatedPermissions;
    private final List<String> addedNegatedPermissions;

    private RankModification(Integer level, String name, String displayName, String chatFormat,
                             List<String> removedPermissions, List<String> addedPermissions,
                             List<String> removedNegatedPermissions, List<String> addedNegatedPermissions) {
        this.level = level;
        this.name = name;
        this.displayName = displayName;
        this.chatFormat = chatFormat;
        this.removedPermissions = removedPermissions;
        this.addedPermissions = addedPermissions;
        this.removedNegatedPermissions = removedNegatedPermissions;
        this.addedNegatedPermissions = addedNegatedPermissions;
    }

    public Optional<Integer> getLevel() {
        return Optional.ofNullable(level);
    }

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public Optional<String> getDisplayName() {
        return Optional.ofNullable(displayName);
    }

    public Optional<String> getChatFormat() {
        return Optional.ofNullable(chatFormat);
    }

    public List<String> getRemovedPermissions() {
        return removedPermissions;
    }

    public List<String> getAddedPermissions() {
        return addedPermissions;
    }

    public List<String> getRemovedNegatedPermissions() {
        return removedNegatedPermissions;
    }

    public List<String> getAddedNegatedPermissions() {
        return addedNegatedPermissions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private final List<String> removedPermissions = new ArrayList<>();
        private final List<String> addedPermissions = new ArrayList<>();
        private final List<String> removedNegatedPermissions = new ArrayList<>();
        private final List<String> addedNegatedPermissions = new ArrayList<>();

        private Integer level;
        private String name;
        private String displayName;
        private String chatFormat;

        private Builder() {}

        public Builder removedPermission(Collection<String> removedPermission) {
            this.removedPermissions.addAll(removedPermission);
            return this;
        }

        public Builder addedPermission(Collection<String> addedPermission) {
            this.addedPermissions.addAll(addedPermission);
            return this;
        }

        public Builder removedNegatedPermission(Collection<String> removedNegatedPermission) {
            this.removedNegatedPermissions.addAll(removedNegatedPermission);
            return this;
        }

        public Builder addedNegatedPermission(Collection<String> addedNegatedPermission) {
            this.addedNegatedPermissions.addAll(addedNegatedPermission);
            return this;
        }

        public Builder level(Integer level) {
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

        public RankModification build() {
            return new RankModification(level, name, displayName, chatFormat, List.copyOf(removedPermissions),
                    List.copyOf(addedPermissions), List.copyOf(removedNegatedPermissions),
                    List.copyOf(addedNegatedPermissions));
        }
    }
}
