// @java.file.header

/*  _________        _____ __________________        _____
 *  __  ____/___________(_)______  /__  ____/______ ____(_)_______
 *  _  / __  __  ___/__  / _  __  / _  / __  _  __ `/__  / __  __ \
 *  / /_/ /  _  /    _  /  / /_/ /  / /_/ /  / /_/ / _  /  _  / / /
 *  \____/   /_/     /_/   \_,__/   \____/   \__,_/  /_/   /_/ /_/
 */

package org.gridgain.examples.datagrid.store;

import org.gridgain.examples.*;
import org.gridgain.grid.*;
import org.gridgain.grid.cache.*;
import org.gridgain.grid.dataload.*;
import org.gridgain.grid.lang.*;

import java.util.concurrent.*;

/**
 * Loads data from persistent store at cache startup by calling
 * {@link GridCache#loadCache(GridBiPredicate, long, Object...)} method on
 * all nodes.
 * <p>
 * You should not be using stand-alone nodes (started with {@code 'ggstart.sh})
 * because GridGain nodes do not know about the cache stores
 * we define in this example. However, users can always add JAR-files with their classes to
 * {@code GRIDGAIN_HOME/libs/ext} folder to make them available to GridGain.
 * If this was done here (i.e. we had JAR-file containing custom cache store built
 * and put to {@code GRIDGAIN_HOME/libs/ext} folder), we could easily startup
 * remote nodes with {@code 'ggstart.sh examples/config/example-cache.xml'}
 * command.
 * <p>
 * Please note that this example loads large amount of data into memory and therefore
 * requires larger heap size. Please add {@code -Xmx1g} to JVM startup options.
 *
 * @author @java.author
 * @version @java.version
 */
public class CacheStoreLoadDataExample {
    /** Heap size required to run this example. */
    public static final int MIN_MEMORY = 1024 * 1024 * 1024;

    /** Number of entries to load. */
    private static final int ENTRY_COUNT = 1000000;

    /**
     * Generates and loads data onto in-memory data grid directly form {@link GridDataLoader}
     * public API.
     *
     * @param args Parameters.
     * @throws Exception If failed.
     */
    public static void main(String[] args) throws Exception {
        ExamplesUtils.checkMinMemory(MIN_MEMORY);

        try (Grid g = GridGain.start(CacheNodeWithStoreStartup.configure())) {
            final GridCache<String, Integer> cache = g.cache(null);

            long start = System.currentTimeMillis();

            // Start loading cache on all caching nodes.
            g.forCache(null).compute().broadcast(new Callable<Object>() {
                @Override public Object call() throws Exception {
                    // Load cache from persistent store.
                    cache.loadCache(null, 0, ENTRY_COUNT);

                    return null;
                }
            }).get();

            long end = System.currentTimeMillis();

            System.out.println(">>> Loaded " + ENTRY_COUNT + " keys with backups in " + (end - start) + "ms.");
        }
    }
}
