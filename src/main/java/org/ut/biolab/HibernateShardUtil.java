package org.ut.biolab;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.shards.ShardId;
import org.hibernate.shards.ShardedConfiguration;
import org.hibernate.shards.cfg.ConfigurationToShardConfigurationAdapter;
import org.hibernate.shards.strategy.ShardStrategy;
import org.hibernate.shards.strategy.ShardStrategyFactory;
import org.hibernate.shards.strategy.ShardStrategyImpl;
import org.hibernate.shards.strategy.access.ParallelShardAccessStrategy;
import org.hibernate.shards.strategy.access.ShardAccessStrategy;
import org.hibernate.shards.strategy.resolution.ShardResolutionStrategy;
import org.hibernate.shards.strategy.selection.ShardSelectionStrategy;

public class HibernateShardUtil {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    static {
        try {
            Configuration config = new Configuration();
            config.configure("hibernate0.cfg.xml");
            config.addResource("variant.hbm.xml");
            List shardConfigs = new ArrayList();
            shardConfigs.add(new ConfigurationToShardConfigurationAdapter(new Configuration().configure("hibernate0.cfg.xml")));
            shardConfigs.add(new ConfigurationToShardConfigurationAdapter(new Configuration().configure("hibernate1.cfg.xml")));
            ShardStrategyFactory shardStrategyFactory = buildShardStrategyFactory();
            ShardedConfiguration shardedConfig = new ShardedConfiguration(config, shardConfigs, shardStrategyFactory);
            sessionFactory = shardedConfig.buildShardedSessionFactory();
        } catch (Throwable ex) {
            ex.printStackTrace();
            sessionFactory = null;
        }
    }

    static ShardStrategyFactory buildShardStrategyFactory() {
        ThreadFactory factory = new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread t = Executors.defaultThreadFactory().newThread(r);
                t.setDaemon(true);
                return t;
            }
        };

        final ThreadPoolExecutor exec = new ThreadPoolExecutor(10, 50, 60, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), factory);

        ShardStrategyFactory shardStrategyFactory = new ShardStrategyFactory() {
            public ShardStrategy newShardStrategy(List<ShardId> shardIds) {
                ShardSelectionStrategy pss = new VariantShardSelectionStrategy();
                ShardResolutionStrategy prs = new VariantShardResolutionStrategy(shardIds);
                ShardAccessStrategy pas = new ParallelShardAccessStrategy(exec);
                return new ShardStrategyImpl(pss, prs, pas);
            }
        };
        return shardStrategyFactory;
    }
}
