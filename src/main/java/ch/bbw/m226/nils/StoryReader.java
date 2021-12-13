package ch.bbw.m226.nils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;

public class StoryReader {
    public Story read(String path) throws IOException {

        var objectMapper = new ObjectMapper(new YAMLFactory());

        return objectMapper.readValue(new File(path), Story.class);
    }
}
