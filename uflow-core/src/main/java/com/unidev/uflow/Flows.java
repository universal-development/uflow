package com.unidev.uflow;

import com.unidev.idgenerator.YoutubeIdGenerator;

/**
 * Common flow operations
 */
public class Flows {

    public String genId(String key) {
        YoutubeIdGenerator youtubeIdGenerator = new YoutubeIdGenerator();
        return youtubeIdGenerator.generate(key);
    }

}
