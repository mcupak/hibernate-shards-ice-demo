package org.ut.biolab;

import java.util.List;
import org.hibernate.shards.ShardId;
import org.hibernate.shards.strategy.resolution.AllShardsShardResolutionStrategy;
import org.hibernate.shards.strategy.selection.ShardResolutionStrategyData;

public class VariantShardResolutionStrategy extends AllShardsShardResolutionStrategy {
    public VariantShardResolutionStrategy(List<ShardId> shardIds) {
        super(shardIds);
    }

    public List<ShardId> selectShardIdsFromShardResolutionStrategyData(ShardResolutionStrategyData srsd) {
        return super.selectShardIdsFromShardResolutionStrategyData(srsd);
    }
}
