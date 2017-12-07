package com.yjl.funk.audiocache.file;

import com.yjl.funk.model.MusicTrack;
import com.yjl.funk.utils.FileUtils;


/**
 * Implementation of {@link FileNameGenerator} that uses MD5 of url as file name
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class MusicFileNameGenerator implements FileNameGenerator {

    private static final int MAX_EXTENSION_LENGTH = 4;

    @Override
    public String generate( MusicTrack track) {
        String name = "";
        if(track!=null) {
             name = track.getAuthor()+"-"+track.getTitle()+ FileUtils.MP3;
        }
        return name;
    }

}
