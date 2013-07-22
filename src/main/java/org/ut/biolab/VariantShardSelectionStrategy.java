package org.ut.biolab;

import org.hibernate.shards.ShardId;
import org.hibernate.shards.strategy.selection.ShardSelectionStrategy;

public class VariantShardSelectionStrategy implements ShardSelectionStrategy {
    public ShardId selectShardIdForNewObject(Object obj) {
        // temporary single shard
        return new ShardId(0);
    }
}