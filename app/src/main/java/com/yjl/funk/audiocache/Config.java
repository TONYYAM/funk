package com.yjl.funk.audiocache;


import com.yjl.funk.audiocache.file.DiskUsage;
import com.yjl.funk.audiocache.file.FileNameGenerator;
import com.yjl.funk.audiocache.headers.HeaderInjector;
import com.yjl.funk.audiocache.sourcestorage.SourceInfoStorage;
import com.yjl.funk.model.MusicTrack;

import java.io.File;

/**
 * Configuration for proxy cache.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
class Config {

    public final File cacheRoot;
    public final FileNameGenerator fileNameGenerator;
    public final DiskUsage diskUsage;
    public final SourceInfoStorage sourceInfoStorage;
    public final HeaderInjector headerInjector;

    Config(File cacheRoot, FileNameGenerator fileNameGenerator, DiskUsage diskUsage, SourceInfoStorage sourceInfoStorage, HeaderInjector headerInjector) {
        this.cacheRoot = cacheRoot;
        this.fileNameGenerator = fileNameGenerator;
        this.diskUsage = diskUsage;
        this.sourceInfoStorage = sourceInfoStorage;
        this.headerInjector = headerInjector;
    }

    File generateCacheFile(MusicTrack track) {
        String name = fileNameGenerator.generate(track);
        return new File(cacheRoot, name);
    }

}
