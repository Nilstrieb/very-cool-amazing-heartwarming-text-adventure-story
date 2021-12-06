package ch.bbw.m226.nils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class StoryReader {
    public Story read(Path path) throws IOException {
        String content = Files.readString(path);

        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

        return objectMapper.readValue(content, Story.class);
    }
}
