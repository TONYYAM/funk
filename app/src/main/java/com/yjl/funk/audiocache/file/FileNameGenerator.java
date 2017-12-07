package com.yjl.funk.audiocache.file;

import com.yjl.funk.model.MusicTrack;

/**
 * Generator for files to be used for caching.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public interface FileNameGenerator {

    String generate(MusicTrack track);

}
